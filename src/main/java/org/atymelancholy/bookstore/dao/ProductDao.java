package org.atymelancholy.bookstore.dao;

import java.util.List;
import java.util.Optional;

import org.atymelancholy.bookstore.model.Product;

public interface ProductDao {

    /**
     * Returns all products.
     *
     * @return list of products
     */
    List<Product> findAll();

    /**
     * Returns a page of products ordered by id.
     *
     * @param limit max rows to return
     * @param offset zero-based offset
     * @return list of products
     */
    List<Product> findPage(int limit, int offset);

    /**
     * Counts all products in the catalog.
     *
     * @return total number of products
     */
    long countAll();

    /**
     * Find product by id.
     *
     * @param id product id
     * @return optional product
     */
    Optional<Product> findById(long id);

    /**
     * Inserts a new product.
     *
     * @param name product name
     * @param description product description
     * @param priceCents price in cents
     * @param stock initial stock
     * @return generated id
     */
    long insert(String name, String description, int priceCents, int stock);

    /**
     * Updates an existing product.
     *
     * @param id product id
     * @param name product name
     * @param description product description
     * @param priceCents price in cents
     * @param stock stock value
     */
    void update(long id,
                String name,
                String description,
                int priceCents,
                int stock);

    /**
     * Deletes a product by id.
     *
     * @param id product id
     */
    void delete(long id);
}
