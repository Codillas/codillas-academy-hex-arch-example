# Project documentation

This directory describes the currently implemented commerce application.

| Document | Purpose |
| --- | --- |
| [Architecture](architecture.md) | Module boundaries, ports and adapters, dependency injection, transactions, locking, and extension rules |
| [HTTP API](api.md) | Endpoints, JSON contracts, lifecycle behavior, and error responses |
| [Development guide](development.md) | Local startup, packaged JAR execution, PostgreSQL, Flyway migrations, testing, and common commands |
| [Mermaid source](diagrams/commerce-hexagonal-architecture.mmd) | Standalone source for the architecture diagram embedded in the root README |

## System at a glance

The repository contains one deployable Spring Boot modular monolith with four business modules:

```text
customers
catalog
inventory ──> catalog::api
orders ─────> customers::api + catalog::api + inventory::api
```

Every module applies the same inner hexagonal structure:

```text
adapter.in.web → application.port.in ← application.service → domain
                                            ↓
                              application.port.out ← adapter.out.persistence → PostgreSQL
```

The arrows describe runtime calls; source-code dependencies still point toward interfaces and the
domain. Spring Modulith publishes `application.port.in` as each module's named interface `api`.
ArchUnit enforces application ownership of ports, directional adapters, dependency direction,
framework isolation, and dependency injection through interfaces.
