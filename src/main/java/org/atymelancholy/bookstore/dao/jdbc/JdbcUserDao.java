package org.atymelancholy.bookstore.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import javax.sql.DataSource;

import org.atymelancholy.bookstore.dao.UserDao;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.model.UserAuthRow;

public final class JdbcUserDao implements UserDao {

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
    public JdbcUserDao(final DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public Optional<UserAuthRow> findByLogin(final String login) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, login, password_hash, email, display_name, role "
                             + "FROM users WHERE login = ?")) {
            ps.setString(PARAM_1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAuth(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<UserAccount> findById(final long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, login, email, display_name, role "
                             + "FROM users WHERE id = ?")) {
            ps.setLong(PARAM_1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAccount(rs));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long insert(final String login,
                       final String passwordHash,
                       final String email,
                       final String displayName,
                       final String role) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users "
                             + "(login, password_hash, email, display_name, role) "
                             + "VALUES (?,?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(PARAM_1, login);
            ps.setString(PARAM_2, passwordHash);
            ps.setString(PARAM_3, email);
            ps.setString(PARAM_4, displayName);
            ps.setString(PARAM_5, role);
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
    public void updateRoleByLogin(final String login, final String role) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE users SET role = ? WHERE login = ?")) {
            ps.setString(PARAM_1, role);
            ps.setString(PARAM_2, login);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void updateProfile(final long id,
                              final String email,
                              final String displayName) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE users SET email = ?, display_name = ? "
                             + "WHERE id = ?")) {
            ps.setString(PARAM_1, email);
            ps.setString(PARAM_2, displayName);
            ps.setLong(PARAM_3, id);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new IllegalStateException("user not found");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static UserAuthRow mapAuth(final ResultSet rs) throws Exception {
        return new UserAuthRow(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("password_hash"),
                rs.getString("email"),
                rs.getString("display_name"),
                rs.getString("role"));
    }

    private static UserAccount mapAccount(final ResultSet rs) throws Exception {
        return new UserAccount(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("email"),
                rs.getString("display_name"),
                rs.getString("role"));
    }
}
