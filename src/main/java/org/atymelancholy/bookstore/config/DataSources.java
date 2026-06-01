package org.atymelancholy.bookstore.config;

import java.io.InputStream;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * HikariCP connection pool for PostgreSQL (single instance per enum).
 * <p>Pool thread safety is provided by HikariCP; application code does not use
 * {@code synchronized} or {@code volatile} for the pool.</p>
 * <p>Configuration: {@code SHOP_JDBC_*} environment variables or classpath file
 * {@code /shop.db.properties} (see {@code shop.db.properties.example}).</p>
 */
public enum DataSources {
    /** Sole holder of the application-wide connection pool. */
    INSTANCE;

    /** Maximum pool size. */
    private static final int MAX_POOL_SIZE = 8;

    /** Underlying HikariCP pool. */
    private final HikariDataSource dataSource;

    DataSources() {
        Properties file = loadClasspathProperties("shop.db.properties");
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(firstNonBlank(
                System.getenv("SHOP_JDBC_URL"),
                file.getProperty("jdbc.url"),
                "jdbc:postgresql://localhost:5432/shop"));
        cfg.setUsername(firstNonBlank(
                System.getenv("SHOP_JDBC_USER"),
                file.getProperty("jdbc.user"),
                "shop"));
        cfg.setPassword(firstNonBlank(
                System.getenv("SHOP_JDBC_PASSWORD"),
                file.getProperty("jdbc.password"),
                "shop"));
        cfg.setMaximumPoolSize(MAX_POOL_SIZE);
        cfg.setPoolName("shop-pool");
        this.dataSource = new HikariDataSource(cfg);
    }

    /**
     * Loads optional properties from the classpath.
     * <p>The file may be absent.</p>
     *
     * @param name classpath resource name
     * @return loaded properties, or empty if the resource is missing
     */
    private static Properties loadClasspathProperties(final String name) {
        Properties p = new Properties();
        try (InputStream in = DataSources.class.getResourceAsStream(
                "/" + name)) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load " + name, e);
        }
        return p;
    }

    /**
     * Returns the first non-blank value among environment variable, file
     * property, and default.
     *
     * @param a first candidate (for example an environment variable)
     * @param b second candidate (for example a file property)
     * @param defaults fallback value
     * @return first non-blank value
     */
    private static String firstNonBlank(final String a,
                                        final String b,
                                        final String defaults) {
        if (a != null && !a.isBlank()) {
            return a.strip();
        }
        if (b != null && !b.isBlank()) {
            return b.strip();
        }
        return defaults;
    }

    /**
     * Returns the application {@link DataSource}.
     *
     * @return connection pool as {@link DataSource}
     */
    public DataSource get() {
        return dataSource;
    }
}
