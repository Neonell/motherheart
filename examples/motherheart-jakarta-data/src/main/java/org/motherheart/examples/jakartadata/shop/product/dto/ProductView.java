package org.motherheart.examples.jakartadata.shop.product.dto;

import java.math.BigDecimal;

public record ProductView(Long id, String name, BigDecimal price) {
}
