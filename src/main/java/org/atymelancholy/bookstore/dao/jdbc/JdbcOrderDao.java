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
import org.atymelancholy.bookstore.model.OrderStatus;
import org.atymelancholy.bookstore.model.OrderSummary;
import org.atymelancholy.bookstore.model.OrderSummary.OrderLineView;

/**
 * JDBC implementation using only {@link PreparedStatement}.
 * <p>SQL injection safe.</p>
 */
public final class JdbcOrderDao implements OrderDao {

    /** 1st prepared-statement parameter index. */
    private static final int PARAM_1 = 1;
    /** 2nd prepared-statement parameter index. */
    private static final int PARAM_2 = 2;
    /** 3rd prepared-statement parameter index. */
    private static final int PARAM_3 = 3;

    /** JDBC data source. */
    private final DataSource dataSource;

    /**
     * Create JDBC DAO.
     *
     * @param ds data source
     */
    public JdbcOrderDao(final DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public List<OrderSummary> listByUser(final long userId,
                                         final int limit,
                                         final int offset) {
        String qOrders = """
                SELECT id, user_id, status, created_at
                FROM orders
                WHERE user_id = ?
                ORDER BY id DESC
                LIMIT ? OFFSET ?
                """;
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(qOrders)) {
            ps.setLong(PARAM_1, userId);
            ps.setInt(PARAM_2, limit);
            ps.setInt(PARAM_3, offset);
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
                    .map(h -> new OrderSummary(
                            h.id,
                            h.userId,
                            h.status,
                            h.createdAt,
                            List.copyOf(h.lines)))
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void loadLines(final Connection c,
                           final List<Long> orderIds,
                           final Map<Long, Holder> holders) throws Exception {
        String in = String.join(
                ",",
                orderIds.stream().map(x -> "?").toList());
        String q = "SELECT oi.order_id, oi.product_id, oi.quantity, p.name, "
                + "p.price_cents "
                + "FROM order_items oi "
                + "JOIN products p ON p.id = oi.product_id "
                + "WHERE oi.order_id IN (" + in + ")";
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
    public long countByUser(final long userId) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM orders WHERE user_id = ?")) {
            ps.setLong(PARAM_1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long createPlacedOrder(final long userId,
                                  final List<OrderLine> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("empty cart");
        }
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                long orderId = insertOrder(c, userId, OrderStatus.PLACED);
                for (OrderLine line : lines) {
                    insertLine(c, orderId, line.productId(), line.quantity());
                    decrementStock(c, line.productId(), line.quantity());
                }
                c.commit();
                return orderId;
            } catch (Exception e) {
                c.rollback();
                throw e instanceof RuntimeException re
                        ? re
                        : new IllegalStateException(e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void cancelOrder(final long userId, final long orderId) {
        try (Connection c = dataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement lock = c.prepareStatement(
                        "SELECT id FROM orders WHERE id = ? AND user_id = ? "
                                + "AND status = ? FOR UPDATE")) {
                    lock.setLong(PARAM_1, orderId);
                    lock.setLong(PARAM_2, userId);
                    lock.setString(PARAM_3, OrderStatus.PLACED);
                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalStateException(
                                    "order not cancellable");
                        }
                    }
                }
                List<OrderLineView> lines = loadLinesForOrder(c, orderId);
                for (OrderLineView line : lines) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE products SET stock = stock + ? "
                                    + "WHERE id = ?")) {
                        ps.setInt(PARAM_1, line.quantity());
                        ps.setLong(PARAM_2, line.productId());
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement update = c.prepareStatement(
                        "UPDATE orders SET status = ? "
                                + "WHERE id = ? AND user_id = ?")) {
                    update.setString(PARAM_1, OrderStatus.CANCELLED);
                    update.setLong(PARAM_2, orderId);
                    update.setLong(PARAM_3, userId);
                    if (update.executeUpdate() != 1) {
                        throw new IllegalStateException("order not cancelled");
                    }
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e instanceof RuntimeException re
                        ? re
                        : new IllegalStateException(e);
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static long insertOrder(final Connection c,
                                    final long userId,
                                    final String status) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO orders (user_id, status) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(PARAM_1, userId);
            ps.setString(PARAM_2, status);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    private static void insertLine(final Connection c,
                                   final long orderId,
                                   final long productId,
                                   final int qty) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity) "
                        + "VALUES (?, ?, ?)")) {
            ps.setLong(PARAM_1, orderId);
            ps.setLong(PARAM_2, productId);
            ps.setInt(PARAM_3, qty);
            ps.executeUpdate();
        }
    }

    private static void decrementStock(final Connection c,
                                       final long productId,
                                       final int qty) throws Exception {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE products SET stock = stock - ? "
                        + "WHERE id = ? AND stock >= ?")) {
            ps.setInt(PARAM_1, qty);
            ps.setLong(PARAM_2, productId);
            ps.setInt(PARAM_3, qty);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new IllegalStateException(
                        "insufficient stock for product " + productId);
            }
        }
    }

    private static List<OrderLineView> loadLinesForOrder(
            final Connection c,
            final long orderId) throws Exception {
        String q = "SELECT oi.product_id, oi.quantity, p.name, p.price_cents "
                + "FROM order_items oi "
                + "JOIN products p ON p.id = oi.product_id "
                + "WHERE oi.order_id = ?";
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(PARAM_1, orderId);
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
        /** Order id. */
        private final long id;
        /** User id. */
        private final long userId;
        /** Order status. */
        private final String status;
        /** Creation timestamp. */
        private final Instant createdAt;
        /** Lines of this order. */
        private final List<OrderLineView> lines = new ArrayList<>();

        private Holder(final long orderId,
                       final long ownerUserId,
                       final String orderStatus,
                       final Instant orderCreatedAt) {
            this.id = orderId;
            this.userId = ownerUserId;
            this.status = orderStatus;
            this.createdAt = orderCreatedAt;
        }
    }
}
