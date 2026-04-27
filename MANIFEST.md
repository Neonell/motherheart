# Motherheart Architecture (MHA) or Architecture coear-mère (ACM) Manifest

## Definition

**Motherheart Architecture** is a software architecture philosophy built on trust, transparency, and proximity to reality.

It rejects unnecessary abstraction between software and its data source.

It promotes a system where developers remain close to the technologies they use, where data remains understandable, and where responsibility for behavior is never hidden behind layers of automation.

Motherheart Architecture is not anti-framework.

It accepts frameworks when they provide clear value.

But it rejects blind trust in abstraction when abstraction removes visibility, ownership, or responsibility.

---

# The Meaning of Motherheart

The name comes from two essential concepts:

* **The Heart** represents the database — the central source of truth.
* **The Mother** represents the architecture — the protective structure that keeps the heart safe, understandable, and respected.

The database is not an implementation detail.

It is the living center of the system.

The architecture exists to protect that center from unnecessary complexity, hidden behavior, and loss of control.

Just as a mother protects what is vital, the architecture protects the integrity of the data.

---

# Core Belief

Software should stay close to reality.

The closer software remains to the technologies that actually execute behavior, the easier it becomes to understand, maintain, debug, optimize, and trust.

When abstraction becomes too distant from reality, engineers lose visibility.

When visibility is lost, responsibility disappears.

Motherheart Architecture exists to prevent this separation.

---

# The Problem Motherheart Solves

Modern persistence frameworks often simplify database interaction through predicates, ORM abstractions, query builders, and generated SQL.

These tools are powerful.

But they also create distance between the engineer and the executed query.

This distance introduces hidden complexity.

Examples include:

* Generated SQL that is never reviewed
* Hidden joins and subqueries
* Uncontrolled query expansion
* Loading fields that are never used
* Large object graphs for small data needs
* Performance costs that are invisible in code
* Security concerns caused by unintentional data exposure
* Developers losing ownership of what truly happens at runtime

Motherheart Architecture does not reject convenience.

It rejects losing awareness.

---

# Foundational Principles

## 1. Stay Close to the Technology

Use the technology closest to the execution layer whenever possible.

Examples:

* SQL for database interaction
* Java EE integrated APIs where appropriate
* JPA where it adds clear value
* Well-understood frameworks that remain transparent

Abstraction should not replace understanding.

---

## 2. Trust Requires Visibility

You cannot trust what you cannot see.

Generated SQL should never become a hidden side effect.

A system should make database behavior predictable and understandable.

Engineers must remain aware of:

* What query executes
* What joins occur
* What data is loaded
* What cost exists behind a request

Trust is earned through transparency.

---

## 3. Use Entities for CRUD

Entities are trusted for CRUD operations.

CRUD is deterministic.

CRUD is predictable.

CRUD rarely requires deep query optimization.

Entities are excellent for:

* Create
* Read by identifier
* Update
* Delete

For these operations, ORM models provide strong value.

They simplify persistence while remaining understandable.

---

## 4. Use SQL and DTOs for Lists and Complex Reads

As soon as a query becomes list-oriented, aggregated, analytical, or performance-sensitive, Motherheart Architecture moves closer to SQL.

Rules:

* Use DTOs for projections
* Write explicit SQL for complex retrieval
* Avoid loading entire entity graphs for list screens
* Fetch only required data
* Keep the query understandable

Nobody intentionally writes:

```sql
SELECT *
FROM 20_TABLES_JOINED
```

Because reality teaches restraint.

Motherheart Architecture follows the same principle.

Do not load what you do not need.

---

## 5. Limit Abstraction Intentionally

Abstraction is not automatically good.

Every abstraction creates distance.

Distance creates interpretation.

Interpretation creates uncertainty.

Motherheart Architecture does not remove abstraction.

It uses abstraction only when the value is greater than the cost.

---

## 6. Responsibility Must Stay Visible

The engineer remains responsible for the executed behavior.

Responsibility should never disappear behind:

* Generated queries
* Hidden framework behavior
* Implicit loading
* Unseen performance costs

A developer should understand what happens between code and database.

---

## 7. Data Stays Close to Its Source

Information should remain as close as possible to its origin.

Transformations should be explicit.

Movement between layers should remain predictable.

Motherheart Architecture values:

* Transparent data flow
* Clear ownership
* Limited unnecessary mapping
* Predictable boundaries

---

# Architectural Rule Set

## Rule 1

Entities are allowed for CRUD operations.

## Rule 2

Lists, dashboards, statistics, and reporting should prefer DTO projections.

## Rule 3

Complex reads should use explicit SQL.

## Rule 4

Do not load unused fields.

## Rule 5

Do not rely on hidden generated joins.

## Rule 6

Keep the database visible in architectural thinking.

## Rule 7

Prefer understanding over convenience.

## Rule 8

Prefer explicitness over magic.

---

# Anti-Patterns

Motherheart Architecture rejects:

* Blind ORM usage
* Hidden SQL generation
* Deep object graph loading without need
* Fetching complete entities for small projections
* Large implicit joins
* Framework dependence without understanding
* Layers that exist only for abstraction
* Data flow that cannot be explained clearly

---

# The Motherheart Statement

> We trust our layers like we trust our mother.
>
> The architecture protects the heart.
>
> The heart is the database.
>
> The database remains visible, respected, and close to reality.
>
> Software should not hide truth.
>
> Software should make truth understandable.

---

# Official Definition

**Motherheart Architecture** is a software architecture where data remains close to its source, abstraction is intentionally limited, and information flows through transparent and predictable layers.

It prioritizes visibility, explicit data access, controlled abstraction, and trust through understanding.

The architecture protects the database as the system’s heart while keeping engineers responsible for what truly executes.

---

# Motherheart Philosophy

Motherheart Architecture is built on a simple belief:

> Stay close to reality.
> Add complexity only when needed.

The closer software remains to reality, the more stable, understandable, and trustworthy it becomes.
