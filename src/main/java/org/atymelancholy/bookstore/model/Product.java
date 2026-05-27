package org.atymelancholy.bookstore.model;

/** Catalog product. */
public record Product(long id, String name, String description, int priceCents, int stock) {
}
