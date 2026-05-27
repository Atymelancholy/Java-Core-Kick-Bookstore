package org.atymelancholy.bookstore.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.atymelancholy.bookstore.dao.OrderDao;
import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.OrderSummary;
import org.atymelancholy.bookstore.model.OrderSummary.OrderLineView;

/**
 * JDBC implementation using only {@link PreparedStatement} (SQL injection safe).
 */
public final class JdbcOrderDao implements OrderDao {

    private final DataSource dataSource;

    public JdbcOrderDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<OrderSummary> listByUser(long userId, int limit, int offset) {
        String qOrders = """
                SELECT id, user_id, status, created_at
                FROM orders
                WHERE user_id = ?
                ORDER BY id DESC
                LIMIT ? OFFSET ?
                """;
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(qOrders)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            Map<Long, Holder> map = new LinkedHashMap<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    map.put(id, new Holder(
                            id,
                            userId,
                            rs.getString("status"),
                            rs.getTimestamp("created_at").toInstant()));
                }
            }
            if (map.isEmpty()) {
                return List.of();
            }
            loadLines(c, map.keySet().stream().toList(), map);
            return map.values().stream()
                    .map(h -> new OrderSummary(h.id, h.userId, h.status, h.createdAt, List.copyOf(h.lines)))
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void loadLines(Connection c, List<Long> orderIds, Map<Long, Holder> holders) throws Exception {
        String in = String.join(",", orderIds.stream().map(x -> "?").toList());
        String q = "SELECT oi.order_id, oi.product_id, oi.quantity, p.name, p.price_cents "
                + "FROM order_items oi JOIN products p ON p.id = oi.product_id WHERE oi.order_id IN (" + in + ")";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            int i = 1;
            for (Long id : orderIds) {
                ps.setLong(i++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long oid = rs.getLong("order_id");
                    holders.get(oid).lines.add(new OrderLineView(
                            rs.getLong("product_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getInt("price_cents")));
                }
            }
        }
    }

    @Override
    public long countByUser(long userId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM orders WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long createPlacedOrder(long userId, List<OrderLine> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("empty cart");
        }
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                long orderId = insertOrder(c, userId, "PLACED");
                for (OrderLine line : lines) {
                    insertLine(c, orderId, line.productId(), line.quantity());
                    decrementStock(c, line.productId(), line.quantity());
                }
                c.commit();
                return orderId;
            } catch (Exception e) {
                c.rollback();
                throw e instanceof RuntimeException re ? re : new IllegalStateException(e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void cancelOrder(long userId, long orderId) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement lock = c.prepareStatement(
                        "SELECT id FROM orders WHERE id = ? AND user_id = ? AND status = 'PLACED' FOR UPDATE")) {
                    lock.setLong(1, orderId);
                    lock.setLong(2, userId);
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException("order not cancellable");
                        }
                    }
                }
                List<OrderLineView> lines = loadLinesForOrder(c, orderId);
                for (OrderLineView line : lines) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE products SET stock = stock + ? WHERE id = ?")) {
                        ps.setInt(1, line.quantity());
                        ps.setLong(2, line.productId());
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement delItems = c.prepareStatement("DELETE FROM order_items WHERE order_id = ?")) {
                    delItems.setLong(1, orderId);
                    delItems.executeUpdate();
                }
                try (PreparedStatement delOrd = c.prepareStatement("DELETE FROM orders WHERE id = ?")) {
                    delOrd.setLong(1, orderId);
                    delOrd.executeUpdate();
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e instanceof RuntimeException re ? re : new IllegalStateException(e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static long insertOrder(Connection c, long userId, String status) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO orders (user_id, status) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private static void insertLine(Connection c, long orderId, long productId, int qty) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)")) {
            ps.setLong(1, orderId);
            ps.setLong(2, productId);
            ps.setInt(3, qty);
            ps.executeUpdate();
        }
    }

    private static void decrementStock(Connection c, long productId, int qty) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?")) {
            ps.setInt(1, qty);
            ps.setLong(2, productId);
            ps.setInt(3, qty);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new IllegalStateException("insufficient stock for product " + productId);
            }
        }
    }

    private static List<OrderLineView> loadLinesForOrder(Connection c, long orderId) throws Exception {
        String q = "SELECT oi.product_id, oi.quantity, p.name, p.price_cents FROM order_items oi "
                + "JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ?";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderLineView> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new OrderLineView(
                            rs.getLong("product_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getInt("price_cents")));
                }
                return list;
            }
        }
    }

    private static final class Holder {
        private final long id;
        private final long userId;
        private final String status;
        private final Instant createdAt;
        private final List<OrderLineView> lines = new ArrayList<>();

        private Holder(long id, long userId, String status, Instant createdAt) {
            this.id = id;
            this.userId = userId;
            this.status = status;
            this.createdAt = createdAt;
        }
    }
}
