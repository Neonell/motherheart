package ch.motherheart.examples.jooq.shop.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(@NotBlank @Size(max = 200) String name, @NotBlank @Email @Size(max = 320) String email) {
}
