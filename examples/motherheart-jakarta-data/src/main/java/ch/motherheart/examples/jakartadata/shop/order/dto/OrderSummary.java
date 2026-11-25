package ch.motherheart.examples.jakartadata.shop.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummary(Long id, LocalDateTime orderedAt, Long lineCount, BigDecimal total) {
}
