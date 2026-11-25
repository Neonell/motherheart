package ch.motherheart.examples.jakartadata.shop.dashboard.dto;

import java.math.BigDecimal;

public record TopProduct(Long productId, String name, Long quantity, BigDecimal revenue) {
}
