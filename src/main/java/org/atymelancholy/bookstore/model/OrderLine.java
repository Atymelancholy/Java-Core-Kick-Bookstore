package org.atymelancholy.bookstore.model;

/**
 * One catalog line when placing an order.
 *
 * @param productId product id
 * @param quantity quantity
 */
public record OrderLine(long productId, int quantity) {
}
