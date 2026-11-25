/**
 * The Motherheart shop, jOOQ flavour. Every sub-package is a domain, and inside a domain
 * the package tells the layer:
 *
 * <ul>
 *   <li>{@code api}: the REST resource, uses only its own service.</li>
 *   <li>{@code service}: the only door into a domain; may ask other domains' services.</li>
 *   <li>{@code persistence}: one repository per domain: CRUD on jOOQ {@code UpdatableRecord}s (the jOOQ
 *       "entities"), and type-safe SQL against the generated heart, mapped into DTOs. Split it when it grows too big.</li>
 *   <li>{@code dto}: records. A list is always DTOs; a single object may be an entity or a DTO.</li>
 * </ul>
 *
 * <p>The schema itself lives in {@code org.motherheart.examples.jooq.heart}, generated from the Flyway scripts.
 */
package org.motherheart.examples.jooq.shop;
