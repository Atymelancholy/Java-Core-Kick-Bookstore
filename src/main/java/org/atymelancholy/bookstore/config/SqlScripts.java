package org.atymelancholy.bookstore.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Runs SQL scripts from the classpath (schema, seed data).
 * <p>Statements are split on semicolons; each non-empty part is executed
 * separately.</p>
 */
public final class SqlScripts {

    /** Logger for SQL script execution diagnostics. */
    private static final Logger LOG = LogManager.getLogger(SqlScripts.class);

    private SqlScripts() {
    }

    /**
     * Returns the row count for a table.
     * <p>Table name must contain only letters, digits, and underscores;
     * it is supplied from application code.</p>
     *
     * @param ds connection pool
     * @param table table name
     * @return number of rows in the table
     */
    public static long countRows(final DataSource ds, final String table) {
        if (!table.chars().allMatch(ch -> Character.isLetterOrDigit(ch)
                || ch == '_')) {
            throw new IllegalArgumentException("invalid table: " + table);
        }
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM " + table)) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception e) {
            LOG.error("countRows {}", table, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Runs a script from a classpath resource such as {@code db/schema.sql}.
     *
     * @param ds connection pool
     * @param classpathResource classpath resource path
     */
    public static void runClasspath(final DataSource ds,
                                    final String classpathResource) {
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            String sql = readResource(classpathResource);
            for (String part : sql.split(";")) {
                String trimmed = part.strip();
                if (!trimmed.isEmpty()) {
                    st.execute(trimmed);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to run SQL {}", classpathResource, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Reads a SQL text resource as a UTF-8 string.
     *
     * @param path classpath resource path
     * @return resource contents
     */
    private static String readResource(final String path) {
        ClassLoader cl = SqlScripts.class.getClassLoader();
        try (InputStream in = cl.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing resource: " + path);
            }
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return r.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
