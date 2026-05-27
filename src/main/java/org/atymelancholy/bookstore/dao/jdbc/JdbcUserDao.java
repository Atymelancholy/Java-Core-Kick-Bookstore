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

    private final DataSource dataSource;

    public JdbcUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UserAuthRow> findByLogin(String login) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, login, password_hash, email, display_name FROM users WHERE login = ?")) {
            ps.setString(1, login);
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
    public Optional<UserAccount> findById(long id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, login, email, display_name FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new UserAccount(
                            rs.getLong("id"),
                            rs.getString("login"),
                            rs.getString("email"),
                            rs.getString("display_name")));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long insert(String login, String passwordHash, String email, String displayName) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (login, password_hash, email, display_name) VALUES (?,?,?,?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, login);
            ps.setString(2, passwordHash);
            ps.setString(3, email);
            ps.setString(4, displayName);
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
    public void updateProfile(long id, String email, String displayName) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE users SET email = ?, display_name = ? WHERE id = ?")) {
            ps.setString(1, email);
            ps.setString(2, displayName);
            ps.setLong(3, id);
            int n = ps.executeUpdate();
            if (n != 1) {
                throw new IllegalStateException("user not found");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static UserAuthRow mapAuth(ResultSet rs) throws Exception {
        return new UserAuthRow(
                rs.getLong("id"),
                rs.getString("login"),
                rs.getString("password_hash"),
                rs.getString("email"),
                rs.getString("display_name"));
    }
}
