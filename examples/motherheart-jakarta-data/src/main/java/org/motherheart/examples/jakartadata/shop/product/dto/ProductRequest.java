package org.motherheart.examples.jakartadata.shop.product.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(@NotBlank @Size(max = 200) String name,
                             @NotNull @PositiveOrZero @Digits(integer = 10, fraction = 2) BigDecimal price) {
}
