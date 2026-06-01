package org.atymelancholy.bookstore.dao;

import java.util.List;

import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.OrderSummary;

/**
 * Data access for orders and line items (JDBC / prepared statements only).
 */
public interface OrderDao {

    /**
     * List orders for a user with pagination.
     *
     * @param userId user id
     * @param limit max rows to return
     * @param offset zero-based offset
     * @return list of orders
     */
    List<OrderSummary> listByUser(long userId, int limit, int offset);

    /**
     * Count orders for a user.
     *
     * @param userId user id
     * @return total number of orders
     */
    long countByUser(long userId);

    /**
     * Creates a new order in status {@code PLACED} with given items.
     *
     * @param userId user id
     * @param lines order lines
     * @return created order id
     */
    long createPlacedOrder(long userId, List<OrderLine> lines);

    /**
     * Cancels an existing order and restores stock.
     *
     * @param userId user id
     * @param orderId order id
     */
    void cancelOrder(long userId, long orderId);
}
