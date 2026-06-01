package org.atymelancholy.bookstore.model;

/**
 * Catalog product.
 *
 * @param id product id
 * @param name name
 * @param description description
 * @param priceCents price in cents
 * @param stock stock count
 */
public record Product(long id,
                      String name,
                      String description,
                      int priceCents,
                      int stock) {
}
