package org.atymelancholy.bookstore.model;

import java.time.Instant;
import java.util.List;

/**
 * Order header with lines for list/detail views.
 *
 * @param id order id
 * @param userId user id
 * @param status order status
 * @param createdAt creation time
 * @param lines order lines
 */
public record OrderSummary(long id,
                           long userId,
                           String status,
                           Instant createdAt,
                           List<OrderLineView> lines) {

    /**
     * Sum of all line totals.
     *
     * @return total cents
     */
    public int totalCents() {
        return lines.stream().mapToInt(OrderLineView::lineTotalCents).sum();
    }

    /**
     * A single line in an order.
     *
     * @param productId product id
     * @param productName product name
     * @param quantity quantity
     * @param unitPriceCents unit price in cents
     */
    public record OrderLineView(long productId,
                                String productName,
                                int quantity,
                                int unitPriceCents) {

        /**
         * Line total.
         *
         * @return line total cents
         */
        public int lineTotalCents() {
            return quantity * unitPriceCents;
        }
    }
}
