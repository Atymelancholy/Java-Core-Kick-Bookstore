package org.atymelancholy.bookstore.dao;

import org.atymelancholy.bookstore.dao.jdbc.JdbcOrderDao;
import org.atymelancholy.bookstore.dao.jdbc.JdbcProductDao;
import org.atymelancholy.bookstore.dao.jdbc.JdbcUserDao;

import javax.sql.DataSource;

/**
 * DAO factory: single entry point for JDBC implementations bound to a
 * {@link DataSource}.
 */
public final class DaoFactory {

    /** User DAO. */
    private final UserDao users;
    /** Catalog product DAO. */
    private final ProductDao products;
    /** Order DAO. */
    private final OrderDao orders;

    /**
     * Creates a DAO factory for the given {@link DataSource}.
     *
     * @param dataSource database connection source
     */
    public DaoFactory(final DataSource dataSource) {
        this.users = new JdbcUserDao(dataSource);
        this.products = new JdbcProductDao(dataSource);
        this.orders = new JdbcOrderDao(dataSource);
    }

    /**
     * User DAO.
     *
     * @return {@link UserDao}
     */
    public UserDao users() {
        return users;
    }

    /**
     * Catalog product DAO.
     *
     * @return {@link ProductDao}
     */
    public ProductDao products() {
        return products;
    }

    /**
     * Order DAO.
     *
     * @return {@link OrderDao}
     */
    public OrderDao orders() {
        return orders;
    }
}
