package org.motherheart.examples.jakartadata.shop.customer.dto;

/** List projection: only the columns a list screen needs. */
public record CustomerListItem(Long id, String name) {
}
