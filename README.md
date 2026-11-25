# Mother-Heart Architecture (MHA)
Mother-Heart Architecture (MHA) - ou architecture coeur-mère
By Frédéric Nell

# Definitions
Check out our [manifest](MANIFEST.md)

This architecture follow two main pillars:

## First Pillar
Use Entities only for CRUD operation
As soon as you have a list, use DTOs, stay close to the natural language of a database
and prefers explicit, SQL-like querying that preserves visibility and intent.
Keep an understandable, visible and intentional data flow

## Second Pillar
Don't add unnecessary layers, you need to understand clearly what you write, stay close to the truth, to what we TRUST

## Third Pillar
DTOs, repositories, and APIs should remain isolated within their domain boundary.

# Practical examples
See [examples](examples/README.md): one shop, built twice (Jakarta Data and jOOQ), with the manifest enforced by ArchUnit rules (`motherheart-core`).

```bash
cd examples && ./mvnw verify
```

# About me
I'm a french mothertongue IT enthousiast with a deep thinking and critical mindeset. I like to re-invent the wheels for fun and lose myself in weird concepts thoughs.

I love to philosphy about everything.

I like humans, but I often don't understand them.

-> Therefore I like to help humans, so that they can better undestand themself and find happyness in simplicity
