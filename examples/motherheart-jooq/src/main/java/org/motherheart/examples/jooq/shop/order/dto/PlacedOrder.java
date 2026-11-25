package org.motherheart.examples.jooq.shop.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlacedOrder(Long id, Long customerId, LocalDateTime orderedAt, BigDecimal total) {
}
