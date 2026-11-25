package ch.motherheart.examples.jooq.shop.product.dto;

import java.math.BigDecimal;

public record ProductListItem(Long id, String name, BigDecimal price) {
}
