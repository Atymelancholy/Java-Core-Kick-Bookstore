package org.atymelancholy.bookstore.service;

import java.util.List;
import java.util.Optional;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.Product;

/**
 * Catalog CRUD.
 */
public final class ProductService {

    private final DaoFactory daoFactory;

    public ProductService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public List<Product> listAll() {
        return daoFactory.products().findAll();
    }

    public Optional<Product> find(long id) {
        return daoFactory.products().findById(id);
    }

    public long create(String name, String description, int priceCents, int stock) {
        return daoFactory.products().insert(name, description, priceCents, stock);
    }

    public void update(long id, String name, String description, int priceCents, int stock) {
        daoFactory.products().update(id, name, description, priceCents, stock);
    }

    public void delete(long id) {
        daoFactory.products().delete(id);
    }
}
