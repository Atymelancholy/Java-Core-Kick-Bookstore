package org.atymelancholy.bookstore.model;

/**
 * Order status values stored in the database.
 */
public final class OrderStatus {

    /** Active order that can still be cancelled. */
    public static final String PLACED = "PLACED";
    /** Cancelled (annulled) order; kept in history. */
    public static final String CANCELLED = "CANCELLED";

    private OrderStatus() {
    }
}
