package org.atymelancholy.bookstore.service;

import java.util.List;
import java.util.Optional;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.Product;

/**
 * Catalog CRUD.
 */
public final class ProductService {

    /** Default page size for catalog listing. */
    public static final int PAGE_SIZE = 10;

    /** DAO factory. */
    private final DaoFactory daoFactory;

    /**
     * Creates product service.
     *
     * @param dao DAO factory
     */
    public ProductService(final DaoFactory dao) {
        this.daoFactory = dao;
    }

    /**
     * List all products.
     *
     * @return list of products
     */
    public List<Product> listAll() {
        return daoFactory.products().findAll();
    }

    /**
     * Lists one catalog page (does not load the full catalog into memory).
     *
     * @param pageZeroBased page index starting from 0
     * @return products for that page
     */
    public List<Product> listPage(final int pageZeroBased) {
        int offset = Math.max(0, pageZeroBased) * PAGE_SIZE;
        return daoFactory.products().findPage(PAGE_SIZE, offset);
    }

    /**
     * Total number of products in the catalog.
     *
     * @return product count
     */
    public long productCount() {
        return daoFactory.products().countAll();
    }

    /**
     * Find product by id.
     *
     * @param id product id
     * @return optional product
     */
    public Optional<Product> find(final long id) {
        return daoFactory.products().findById(id);
    }

    /**
     * Create a new product.
     *
     * @param name name
     * @param description description
     * @param priceCents price in cents
     * @param stock stock
     * @return created id
     */
    public long create(final String name,
                       final String description,
                       final int priceCents,
                       final int stock) {
        return daoFactory.products().insert(
                name,
                description,
                priceCents,
                stock);
    }

    /**
     * Update a product.
     *
     * @param id id
     * @param name name
     * @param description description
     * @param priceCents price in cents
     * @param stock stock
     */
    public void update(final long id,
                       final String name,
                       final String description,
                       final int priceCents,
                       final int stock) {
        daoFactory.products().update(id, name, description, priceCents, stock);
    }

    /**
     * Delete product.
     *
     * @param id id
     */
    public void delete(final long id) {
        daoFactory.products().delete(id);
    }
}
