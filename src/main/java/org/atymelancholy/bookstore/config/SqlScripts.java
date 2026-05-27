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
 * Runs classpath SQL scripts against a datasource (schema bootstrap).
 */
public final class SqlScripts {
    private static final Logger LOG = LogManager.getLogger(SqlScripts.class);

    private SqlScripts() {
    }

    /**
     * Row count for a simple identifier table name (internal use only).
     */
    public static long countRows(DataSource ds, String table) {
        if (!table.chars().allMatch(ch -> Character.isLetterOrDigit(ch) || ch == '_')) {
            throw new IllegalArgumentException("invalid table: " + table);
        }
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (Exception e) {
            LOG.error("countRows {}", table, e);
            throw new IllegalStateException(e);
        }
    }

    public static void runClasspath(DataSource ds, String classpathResource) {
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
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

    private static String readResource(String path) {
        ClassLoader cl = SqlScripts.class.getClassLoader();
        try (InputStream in = cl.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing resource: " + path);
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return r.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
