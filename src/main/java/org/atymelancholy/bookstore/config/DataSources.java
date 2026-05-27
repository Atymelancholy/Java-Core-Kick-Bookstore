package org.atymelancholy.bookstore.config;

import java.io.InputStream;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Thread-safe connection pool without application-level {@code synchronized} or {@code volatile}.
 * <p>PostgreSQL: URL/user/password from environment ({@code SHOP_JDBC_*}) or classpath
 * {@code /shop.db.properties} (see {@code shop.db.properties.example}).</p>
 */
public enum DataSources {
    /** Singleton pool holder. */
    INSTANCE;

    private final HikariDataSource dataSource;

    DataSources() {
        Properties file = loadClasspathProperties("shop.db.properties");
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(firstNonBlank(System.getenv("SHOP_JDBC_URL"), file.getProperty("jdbc.url"),
                "jdbc:postgresql://localhost:5432/shop"));
        cfg.setUsername(firstNonBlank(System.getenv("SHOP_JDBC_USER"), file.getProperty("jdbc.user"), "shop"));
        cfg.setPassword(firstNonBlank(System.getenv("SHOP_JDBC_PASSWORD"), file.getProperty("jdbc.password"), "shop"));
        cfg.setMaximumPoolSize(8);
        cfg.setPoolName("shop-pool");
        this.dataSource = new HikariDataSource(cfg);
    }

    private static Properties loadClasspathProperties(String name) {
        Properties p = new Properties();
        try (InputStream in = DataSources.class.getResourceAsStream("/" + name)) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load " + name, e);
        }
        return p;
    }

    private static String firstNonBlank(String a, String b, String defaults) {
        if (a != null && !a.isBlank()) {
            return a.strip();
        }
        if (b != null && !b.isBlank()) {
            return b.strip();
        }
        return defaults;
    }

    public DataSource get() {
        return dataSource;
    }
}
