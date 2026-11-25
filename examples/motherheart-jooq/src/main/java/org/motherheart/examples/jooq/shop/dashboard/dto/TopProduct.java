package org.motherheart.examples.jooq.shop.dashboard.dto;

import java.math.BigDecimal;

public record TopProduct(Long productId, String name, Long quantity, BigDecimal revenue) {
}
