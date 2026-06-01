package org.atymelancholy.bookstore.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.atymelancholy.bookstore.dao.ProductDao;
import org.atymelancholy.bookstore.model.Product;

public final class JdbcProductDao implements ProductDao {

    /** 1st prepared-statement parameter index. */
    private static final int PARAM_1 = 1;
    /** 2nd prepared-statement parameter index. */
    private static final int PARAM_2 = 2;
    /** 3rd prepared-statement parameter index. */
    private static final int PARAM_3 = 3;
    /** 4th prepared-statement parameter index. */
    private static final int PARAM_4 = 4;
    /** 5th prepared-statement parameter index. */
    private static final int PARAM_5 = 5;

    /** JDBC data source. */
    private final DataSource dataSource;

    /**
     * Create JDBC DAO.
     *
     * @param ds data source
     */
    public JdbcProductDao(final DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public List<Product> findAll() {
        return findPage(Integer.MAX_VALUE, 0);
    }

    @Override
    public List<Product> findPage(final int limit, final int offset) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, name, description, price_cents, stock "
                             + "FROM products ORDER BY id "
                             + "LIMIT ? OFFSET ?")) {
            ps.setInt(PARAM_1, limit);
            ps.setInt(PARAM_2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(map(rs));
                }
                return list;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long countAll() {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM products");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<Product> findById(final long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, name, description, price_cents, stock "
                             + "FROM products WHERE id = ?")) {
            ps.setLong(PARAM_1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long insert(final String name,
                       final String description,
                       final int priceCents,
                       final int stock) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO products "
                             + "(name, description, price_cents, stock) "
                             + "VALUES (?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(PARAM_1, name);
            ps.setString(PARAM_2, description);
            ps.setInt(PARAM_3, priceCents);
            ps.setInt(PARAM_4, stock);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void update(final long id,
                       final String name,
                       final String description,
                       final int priceCents,
                       final int stock) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE products SET name=?, description=?, "
                             + "price_cents=?, stock=? WHERE id=?")) {
            ps.setString(PARAM_1, name);
            ps.setString(PARAM_2, description);
            ps.setInt(PARAM_3, priceCents);
            ps.setInt(PARAM_4, stock);
            ps.setLong(PARAM_5, id);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new IllegalStateException("product not found");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void delete(final long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM products WHERE id=?")) {
            ps.setLong(PARAM_1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Product map(final ResultSet rs) throws Exception {
        return new Product(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("price_cents"),
                rs.getInt("stock"));
    }
}
