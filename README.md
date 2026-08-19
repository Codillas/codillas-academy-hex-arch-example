# Hexagonal Orders

A small Spring Boot orders API organized as a feature module with ports and adapters.

## Architecture

- `order` is the business module and its base package is the public module API.
- `PlaceOrderUseCase` and `GetOrdersQuery` are inbound ports.
- `OrderApplicationService` implements the inbound ports and owns transaction boundaries.
- `OrderRepository` is an outbound port.
- `OrderController` is the inbound HTTP adapter.
- `JpaOrderRepositoryAdapter` is the outbound PostgreSQL adapter.
- The domain model has no Spring, HTTP, or persistence dependencies.
- Spring Modulith and ArchUnit tests enforce module and dependency boundaries.

## Requirements

- Java 25
- Maven 3.9+
- Docker with Docker Compose 2.2+

## Run

```bash
cd ~/Desktop/hexagonal-orders
mvn spring-boot:run
```

Spring Boot discovers `compose.yaml`, starts PostgreSQL, waits for it to become healthy, and injects its connection details. Stopping the JVM also stops PostgreSQL; the named volume preserves the data.

Create and query orders:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"product":"Mechanical keyboard","quantity":2}'

curl http://localhost:8080/api/orders
```

Run PostgreSQL separately when desired:

```bash
docker compose up -d
mvn spring-boot:run
```

Inspect the database:

```bash
docker compose exec postgres psql -U orders -d orders -c 'select * from orders;'
```

## Verify

```bash
mvn test
```

To remove the development database and its persisted data:

```bash
docker compose down -v
```
