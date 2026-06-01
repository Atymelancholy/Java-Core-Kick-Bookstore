package org.atymelancholy.bookstore.service;

import java.util.List;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.OrderSummary;

/**
 * Order placement, listing with pagination, cancellation.
 */
public final class OrderService {

    /** Default page size for order listing. */
    public static final int PAGE_SIZE = 5;

    /** DAO factory. */
    private final DaoFactory daoFactory;

    /**
     * Creates order service.
     *
     * @param dao DAO factory
     */
    public OrderService(final DaoFactory dao) {
        this.daoFactory = dao;
    }

    /**
     * Lists orders for a user.
     *
     * @param userId user id
     * @param pageZeroBased page index starting from 0
     * @return list of orders
     */
    public List<OrderSummary> listForUser(final long userId,
                                          final int pageZeroBased) {
        int offset = Math.max(0, pageZeroBased) * PAGE_SIZE;
        return daoFactory.orders().listByUser(userId, PAGE_SIZE, offset);
    }

    /**
     * Counts orders for a user.
     *
     * @param userId user id
     * @return order count
     */
    public long orderCount(final long userId) {
        return daoFactory.orders().countByUser(userId);
    }

    /**
     * Places an order.
     *
     * @param userId user id
     * @param lines order lines
     * @return created order id
     */
    public long placeOrder(final long userId,
                           final List<OrderLine> lines) {
        return daoFactory.orders().createPlacedOrder(userId, lines);
    }

    /**
     * Cancels an order.
     *
     * @param userId user id
     * @param orderId order id
     */
    public void cancel(final long userId, final long orderId) {
        daoFactory.orders().cancelOrder(userId, orderId);
    }
}
