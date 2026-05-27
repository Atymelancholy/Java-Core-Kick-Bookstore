package org.atymelancholy.bookstore.dao;

import org.atymelancholy.bookstore.dao.jdbc.JdbcOrderDao;
import org.atymelancholy.bookstore.dao.jdbc.JdbcProductDao;
import org.atymelancholy.bookstore.dao.jdbc.JdbcUserDao;

import javax.sql.DataSource;

/**
 * Factory for JDBC DAO implementations (simple factory / abstract factory style).
 */
public final class DaoFactory {

    private final UserDao users;
    private final ProductDao products;
    private final OrderDao orders;

    public DaoFactory(DataSource dataSource) {
        this.users = new JdbcUserDao(dataSource);
        this.products = new JdbcProductDao(dataSource);
        this.orders = new JdbcOrderDao(dataSource);
    }

    public UserDao users() {
        return users;
    }

    public ProductDao products() {
        return products;
    }

    public OrderDao orders() {
        return orders;
    }
}
