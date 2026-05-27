package org.atymelancholy.bookstore.model;

import java.time.Instant;
import java.util.List;

/**
 * Order header with lines for list/detail views.
 */
public record OrderSummary(long id, long userId, String status, Instant createdAt, List<OrderLineView> lines) {

    public int totalCents() {
        return lines.stream().mapToInt(OrderLineView::lineTotalCents).sum();
    }

    public record OrderLineView(long productId, String productName, int quantity, int unitPriceCents) {
        public int lineTotalCents() {
            return quantity * unitPriceCents;
        }
    }
}
