/**
 * The Motherheart shop, Jakarta Data flavour. Every sub-package is a domain, and inside a domain
 * the package tells the layer:
 *
 * <ul>
 *   <li>{@code api}: the REST resource, uses only its own service.</li>
 *   <li>{@code service}: the only door into a domain; may ask other domains' services.</li>
 *   <li>{@code persistence}: entities, and one repository per domain: CRUD from {@code CrudRepository},
 *       plus queries (HQL validated at compile time by hibernate-processor) projected into DTOs.
 *       Split it when it grows too big.</li>
 *   <li>{@code dto}: records. A list is always DTOs; a single object may be an entity or a DTO.</li>
 * </ul>
 */
package org.motherheart.examples.jakartadata.shop;
