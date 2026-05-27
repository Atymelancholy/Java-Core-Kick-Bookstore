package org.atymelancholy.bookstore.service;

import java.util.List;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.OrderSummary;

/**
 * Order placement, listing with pagination, cancellation.
 */
public final class OrderService {

    public static final int PAGE_SIZE = 5;

    private final DaoFactory daoFactory;

    public OrderService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public List<OrderSummary> listForUser(long userId, int pageZeroBased) {
        int offset = Math.max(0, pageZeroBased) * PAGE_SIZE;
        return daoFactory.orders().listByUser(userId, PAGE_SIZE, offset);
    }

    public long orderCount(long userId) {
        return daoFactory.orders().countByUser(userId);
    }

    public long placeOrder(long userId, List<OrderLine> lines) {
        return daoFactory.orders().createPlacedOrder(userId, lines);
    }

    public void cancel(long userId, long orderId) {
        daoFactory.orders().cancelOrder(userId, orderId);
    }
}
