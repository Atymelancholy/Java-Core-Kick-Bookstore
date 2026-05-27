package org.atymelancholy.bookstore.model;

/** One catalog line when placing an order. */
public record OrderLine(long productId, int quantity) {
}
