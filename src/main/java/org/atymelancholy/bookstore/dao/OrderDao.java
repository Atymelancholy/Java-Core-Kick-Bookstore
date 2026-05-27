package org.atymelancholy.bookstore.dao;

import java.util.List;

import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.OrderSummary;

/**
 * Data access for orders and line items (JDBC / prepared statements only).
 */
public interface OrderDao {

    List<OrderSummary> listByUser(long userId, int limit, int offset);

    long countByUser(long userId);

    long createPlacedOrder(long userId, List<OrderLine> lines);

    void cancelOrder(long userId, long orderId);
}
