# Motherheart Architecture: practical examples

These examples put the [manifest](../MANIFEST.md) into code. The same shop is built twice, so the two persistence technologies can be compared side by side. A small rules library makes the manifest executable.

| Module | What it is |
|---|---|
| [`motherheart-core`](motherheart-core) | **The manifest as code**: ArchUnit rules, reusable in any project (`MotherheartArchitecture`) |
| [`motherheart-jakarta-data`](motherheart-jakarta-data) | The shop with Quarkus + Hibernate Data Repositories (Jakarta Data) |
| [`motherheart-jooq`](motherheart-jooq) | The same shop, same REST API, same tests, with Quarkus + jOOQ |

Stack: Java 21, Quarkus 3.40, PostgreSQL, Flyway, ArchUnit 1.5, jOOQ 3.21.

## Run it

You need Java 21 and Docker (Quarkus Dev Services starts PostgreSQL by itself).

```bash
./mvnw verify                                   # everything: rules, both shops, architecture tests
./mvnw quarkus:dev -pl motherheart-jooq         # or -pl motherheart-jakarta-data
curl 'localhost:8080/dashboard/revenue-by-month?year=2025'
```

In dev and test mode, every executed SQL statement is printed in the console (*trust requires visibility*).

## The shop

One schema, [`V1__shop_schema.sql`](motherheart-jooq/src/main/resources/db/migration/V1__shop_schema.sql), is the **heart**. It has four tables: `customer`, `product`, `purchase_order`, `order_line`.

```
shop/
  common/                exception mapping (shared kernel)
  customer/
    api/                 CustomerResource
    service/             CustomerService
    persistence/         Customer (entity), CustomerRepository (CRUD + lists, one per domain; split it if it grows)
    dto/                 CustomerListItem, CustomerView, CustomerRequest
  product/               same layout
  order/                 same layout; OrderService calls CustomerService.exists() and ProductService.currentPrice()
  dashboard/             api / service / persistence (reports only, no CRUD) / dto
```

| Endpoint | Kind | Implemented with |
|---|---|---|
| `POST/GET/PUT/DELETE /customers/{id}`, same for `/products` | CRUD | entities (JPA) / `UpdatableRecord` (jOOQ) in `*Repository` |
| `GET /customers`, `GET /products?maxPrice=` | list | DTO projection, in the same `*Repository` |
| `POST /orders` | write across domains | `OrderService` → other domains' **services** |
| `GET /orders?customerId=` | list with join and aggregation | explicit join on ids, DTO |
| `GET /dashboard/revenue-by-month?year=`, `GET /dashboard/top-products?year=&limit=` | report | explicit SQL, DTO |

### Conventions (checked, not just documented)

The **package** tells the layer. Class names are free: a name is just a name.
Entities are recognised by what they are (`@Entity`, or a jOOQ `UpdatableRecord`).

| Layer | Package | May use | Visible to other domains? |
|---|---|---|---|
| REST API | `<domain>.api` | its own service, DTOs | — |
| Service | `<domain>.service` | its own persistence, **other domains' service and dto** | **yes**, the only door |
| Persistence | `<domain>.persistence` | entities, database. Repositories (CRUD) and queries (lists, reports) | no |
| Entity | `@Entity` / jOOQ `UpdatableRecord` | — | no, other domains use **ids** |
| DTO | `<domain>.dto` | nothing persistent | yes, when a service returns it |

**If it is a list, it is DTOs. A single object may be an entity or a DTO.**

## From the manifest to the code

| Manifest | In the code | Enforced by (`MotherheartArchitecture`) |
|---|---|---|
| Rule 1: entities for CRUD | Jakarta Data: `CustomerRepository extends CrudRepository<Customer, Long>`, no CRUD code written. jOOQ: `store()` / `delete()` on the generated records | `listsNeverContainEntities`: a single entity is fine. `CrudRepository` offers `findAll()`, but **calling** it fails the build |
| Rule 2: lists use DTO projections | `CustomerRepository.list()` → `CustomerListItem(id, name)`. The e-mail is not even selected | `listsNeverContainEntities` (checks return types and calls, whatever the method or class is called) |
| Rule 3: complex reads in explicit SQL | `DashboardRepository`, `OrderRepository.summariesFor()` | `queriesAreValidated` (explicit, *and* validated) |
| Rule 4: do not load unused fields | `ProductRepository.priceOf(id)` selects one column, not a product | `listsNeverContainEntities` |
| Rule 5: no hidden generated joins | `PurchaseOrder.customerId` is a `Long`, not a `@ManyToOne Customer`. `OrderLine` is its own entity, not a `@OneToMany` | `entitiesBelongToOneDomain` (across domains). Inside a domain, avoiding `@OneToMany` is left to the developer |
| §4: domain isolation | one package per domain, one sub-package per layer. Cross-domain calls go service → service only | `domainClassesLiveInALayer`, `domainLayering`, `domainsMeetOnlyThroughServices`, `domainsAreFreeOfCycles` |
| §7: responsibility stays visible | only `persistence` holds an `EntityManager` / `DSLContext` | `onlyPersistenceTalksToTheDatabase` |
| Rule 6: keep the database visible | Flyway SQL owns the schema (`schema-management=validate`). jOOQ generates its Java view **from the same SQL files** | — |
| *The heart protects itself* | deleting a customer who has orders: the foreign key refuses, the API answers `409` | `CustomerResourceTest.theHeartRefusesToDeleteACustomerWithOrders` |

Using the rules in your own project:

```java
@AnalyzeClasses(packages = "com.acme")
class MotherheartArchitectureTest {
    static final MotherheartArchitecture SHOP = MotherheartArchitecture.of("com.acme.shop").shared("common");

    @ArchTest static final ArchRule motherheart = SHOP.all();   // or one @ArchTest per rule (8 rules)
}
```

With jOOQ, declare what the "entities" and the heart are:

```java
MotherheartArchitecture.of("com.acme.shop")
        .heart("com.acme.heart")                                         // generated schema
        .entities(JavaClass.Predicates.assignableTo(UpdatableRecord.class).as("are jOOQ records"))
```

## "As close to SQL as possible, but validated"

Each technology validates queries differently:

| | Jakarta Data (Hibernate) | jOOQ |
|---|---|---|
| Where the query lives | `@Query("select new …Dto(…) from …")` string | Java DSL: `dsl.select(…).from(…).join(…)` |
| Validated | **at compile time** by hibernate-processor, against the *entity model* | **at compile time** by `javac`, against the *generated schema* |
| A renamed column breaks… | the build, if the entity was updated. The DB is only checked at boot (`validate`) | the build, directly from the migration SQL |
| Unvalidated escape hatches (forbidden by `queriesAreValidated`) | `@SQL`, `createNativeQuery`, `createQuery(String)` | `dsl.fetch("…")`, `DSL.field("…")`… plain SQL |

The same dashboard query, as each technology logs it:

```sql
-- Jakarta Data (HQL → SQL)
select extract(year from po1_0.ordered_at), extract(month from po1_0.ordered_at),
       count(distinct po1_0.id), sum((ol1_0.quantity*ol1_0.unit_price))
from purchase_order po1_0 join order_line ol1_0 on ol1_0.order_id=po1_0.id
where po1_0.ordered_at>=? and po1_0.ordered_at<? group by … order by …

-- jOOQ
select extract(year from "purchase_order"."ordered_at"), extract(month from "purchase_order"."ordered_at"),
       count(distinct "purchase_order"."id"), sum(("order_line"."unit_price" * "order_line"."quantity"))
from "purchase_order" join "order_line" on "order_line"."order_id" = "purchase_order"."id"
where ("purchase_order"."ordered_at" >= cast(? as timestamp(6)) and … < …) group by … order by …
```

Both give exactly the query you would write by hand: one join, only the columns needed.

### An honest comparison

**Jakarta Data**
- Strengths: standard API (Jakarta EE). CRUD with very little code. Typos in queries are compile errors. Fits naturally where JPA already lives (like kiBon-statistik).
- What it hides: HQL is *translated* SQL, and the schema seen at build time is the entity model, not the database. **Isolation gap**: `DashboardRepository` must name other domains' entities (`PurchaseOrder`, `Product`) inside its query string. ArchUnit cannot see that dependency because it is text.
- Watch out: `CrudRepository` brings `findAll()` and `insertAll()`/`updateAll()`, which return entity collections: calling them fails `listsNeverContainEntities`. Stateless sessions mean no dirty checking, so `update()` is explicit (good). A DTO in a package named `order` cannot be written in `select new …` (`order` is an HQL keyword): `OrderRepository` lets Hibernate map the selected columns to the record from the return type.

**jOOQ**
- Strengths: the code *is* SQL, checked against the real schema generated from the Flyway files. A reporting domain reads the heart's tables without touching any other domain's Java class. No ORM state, no proxies, no lazy loading.
- What it hides: almost nothing. That is the point, and also the cost: you write every query, even simple ones.
- Watch out: plain-SQL APIs bypass the validation, which is why `queriesAreValidated` forbids them. Records are mutable objects attached to a connection, so keep them inside the domain.

**Closest to the heart: jOOQ.** The heart (the SQL schema) is the single source of truth, and Java is generated *from* it. With Jakarta Data the entity model sits in between, a second description of the heart that has to be kept in sync.
