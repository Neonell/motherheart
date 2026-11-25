package ch.motherheart.examples.jooq.shop.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummary(Long id, LocalDateTime orderedAt, Long lineCount, BigDecimal total) {
}
