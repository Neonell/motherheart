package org.motherheart.examples.jooq.shop.customer.dto;

/** List projection: only the columns a list screen needs. */
public record CustomerListItem(Long id, String name) {
}
