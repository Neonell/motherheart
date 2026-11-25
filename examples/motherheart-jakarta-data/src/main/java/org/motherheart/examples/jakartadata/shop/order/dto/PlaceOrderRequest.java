package org.motherheart.examples.jakartadata.shop.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record PlaceOrderRequest(@NotNull Long customerId, @NotEmpty List<@Valid @NotNull Line> lines) {

    public record Line(@NotNull Long productId, @Positive int quantity) {
    }
}
