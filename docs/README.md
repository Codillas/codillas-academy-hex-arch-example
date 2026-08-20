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
web adapter → API port ← application service → domain
                              ↓
                    repository port ← persistence adapter → PostgreSQL
```

The arrows describe runtime calls; source-code dependencies still point toward interfaces and the
domain. Spring Modulith verifies module APIs while strict ArchUnit rules enforce package shape,
dependency direction, framework isolation, and port-based dependency injection.
