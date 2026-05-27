package org.atymelancholy.bookstore.dao;

import java.util.List;
import java.util.Optional;

import org.atymelancholy.bookstore.model.Product;

public interface ProductDao {

    List<Product> findAll();

    Optional<Product> findById(long id);

    long insert(String name, String description, int priceCents, int stock);

    void update(long id, String name, String description, int priceCents, int stock);

    void delete(long id);
}
