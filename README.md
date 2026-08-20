# Commerce

A deliberately small commerce API showing how hexagonal architecture scales beyond a single
feature in Spring Boot. It has four business modules backed by one PostgreSQL database:

- **Customers** registers and queries customers.
- **Catalog** creates, queries, lists, and deactivates products.
- **Inventory** restocks, queries, reserves, and releases stock.
- **Orders** places, queries, lists, confirms, and cancels orders.

## Architecture

The code is package-by-business-capability first. Each module then owns its domain, application
logic, ports, and adapters:

```text
com.codillas.academy.commerce
├── CommerceApplication
├── customers
├── catalog
├── inventory
└── orders
    ├── api                         # inbound ports and public module contracts
    ├── domain                      # framework-free business model
    ├── application
    │   ├── service                 # use-case implementations
    │   ├── port.outbound           # interfaces required by the core
    │   └── exception
    └── adapter
        ├── inbound.web             # REST controllers and HTTP models
        └── outbound.persistence    # JPA implementations of repository ports
```

The same inner hexagon is repeated within each business module. Spring Modulith named interfaces
make cross-module dependencies explicit:

```mermaid
flowchart TB
    client["Web / mobile client"]

    subgraph application["Commerce Spring Boot modular monolith"]
        direction TB

        boot["CommerceApplication<br/>@SpringBootApplication · @Modulithic<br/>@Bean Clock"]

        subgraph modules["Business modules — package by capability"]
            direction TB

            subgraph customers["customers — no module dependencies"]
                direction LR
                cWeb["CustomerController<br/>REST adapter"]
                cApi["«api ports»<br/>RegisterCustomerUseCase<br/>GetCustomerUseCase · ListCustomersUseCase<br/>CustomerDirectory"]
                cService["CustomerApplicationService<br/>@Service · @Transactional"]
                cDomain["Customer<br/>framework-free domain"]
                cRepo["«outbound port»<br/>CustomerRepository"]
                cJpa["JpaCustomerRepositoryAdapter<br/>@Repository"]

                cWeb -->|invokes| cApi
                cService -.->|implements| cApi
                cService -->|uses| cDomain
                cService -->|depends on| cRepo
                cJpa -.->|implements| cRepo
            end

            subgraph catalog["catalog — no module dependencies"]
                direction LR
                pWeb["ProductController<br/>REST adapter"]
                pApi["«api ports»<br/>CreateProductUseCase · GetProductUseCase<br/>ListProductsUseCase · DeactivateProductUseCase<br/>ProductCatalog"]
                pService["ProductApplicationService<br/>@Service · @Transactional"]
                pDomain["Product<br/>framework-free domain"]
                pRepo["«outbound port»<br/>ProductRepository"]
                pJpa["JpaProductRepositoryAdapter<br/>@Repository"]

                pWeb -->|invokes| pApi
                pService -.->|implements| pApi
                pService -->|uses| pDomain
                pService -->|depends on| pRepo
                pJpa -.->|implements| pRepo
            end

            subgraph inventory["inventory — allowed dependency: catalog::api"]
                direction LR
                iWeb["InventoryController<br/>REST adapter"]
                iApi["«api ports»<br/>RestockInventoryUseCase<br/>GetInventoryUseCase<br/>InventoryOperations"]
                iService["InventoryApplicationService<br/>@Service · @Transactional"]
                iDomain["Stock<br/>framework-free domain"]
                iRepo["«outbound port»<br/>InventoryRepository"]
                iJpa["JpaInventoryRepositoryAdapter<br/>@Repository<br/>pessimistic stock lock"]

                iWeb -->|invokes| iApi
                iService -.->|implements| iApi
                iService -->|uses| iDomain
                iService -->|depends on| iRepo
                iJpa -.->|implements| iRepo
            end

            subgraph orders["orders — allowed dependencies: customers::api, catalog::api, inventory::api"]
                direction LR
                oWeb["OrderCommandController<br/>OrderQueryController<br/>REST adapters"]
                oApi["«api ports»<br/>PlaceOrderUseCase · GetOrderUseCase<br/>ListOrdersUseCase · ConfirmOrderUseCase<br/>CancelOrderUseCase"]
                oService["OrderApplicationService<br/>@Service · @Transactional<br/>orchestrates order workflow"]
                oDomain["Order aggregate<br/>framework-free state machine"]
                oRepo["«outbound port»<br/>OrderRepository"]
                oJpa["JpaOrderRepositoryAdapter<br/>@Repository<br/>pessimistic lifecycle lock"]

                oWeb -->|invokes| oApi
                oService -.->|implements| oApi
                oService -->|uses| oDomain
                oService -->|depends on| oRepo
                oJpa -.->|implements| oRepo
            end
        end

        iService -->|calls catalog::api| pApi
        oService -->|calls customers::api| cApi
        oService -->|calls catalog::api| pApi
        oService -->|reserves / releases via inventory::api| iApi

        boot ==>|constructor injection| cService
        boot ==>|constructor injection| pService
        boot ==>|constructor injection| iService
        boot ==>|constructor injection| oService
    end

    subgraph infrastructure["Infrastructure"]
        direction LR
        compose["Spring Boot Docker Compose support<br/>start-and-stop lifecycle · health check"]
        flyway["Flyway<br/>V1 → V2 → V3 migrations"]
        postgres[("PostgreSQL<br/>customers · products · inventory · orders")]

        compose -->|starts| postgres
        flyway -->|migrates schema| postgres
    end

    subgraph guardrails["Architecture guardrails"]
        direction LR
        modulith["Spring Modulith<br/>@ApplicationModule allowedDependencies<br/>@NamedInterface api"]
        moduleTest["ApplicationModules.of<br/>(CommerceApplication.class).verify()"]
        archunit["ArchUnit<br/>domain/API framework-free<br/>core cannot depend on adapters"]

        modulith -->|verified by| moduleTest
    end

    client -->|HTTP /api/customers| cWeb
    client -->|HTTP /api/products| pWeb
    client -->|HTTP /api/inventory| iWeb
    client -->|HTTP /api/orders| oWeb

    cJpa -->|JPA / SQL| postgres
    pJpa -->|JPA / SQL| postgres
    iJpa -->|JPA / SQL| postgres
    oJpa -->|JPA / SQL| postgres

    boot -->|development lifecycle| compose
    boot -->|runs at startup| flyway
    application -.->|module boundaries| modulith
    application -.->|dependency direction| archunit

    classDef external fill:#f8fafc,stroke:#64748b,color:#0f172a
    classDef adapter fill:#e0f2fe,stroke:#0284c7,color:#0c4a6e
    classDef port fill:#fef3c7,stroke:#d97706,color:#78350f
    classDef applicationNode fill:#dcfce7,stroke:#16a34a,color:#14532d
    classDef domainNode fill:#ede9fe,stroke:#7c3aed,color:#4c1d95
    classDef springNode fill:#fee2e2,stroke:#dc2626,color:#7f1d1d
    classDef guardrail fill:#ffffff,stroke:#475569,color:#334155,stroke-dasharray:4 3

    class client,postgres external
    class cWeb,cJpa,pWeb,pJpa,iWeb,iJpa,oWeb,oJpa adapter
    class cApi,cRepo,pApi,pRepo,iApi,iRepo,oApi,oRepo port
    class cService,pService,iService,oService applicationNode
    class cDomain,pDomain,iDomain,oDomain domainNode
    class boot,compose,flyway springNode
    class modulith,moduleTest,archunit guardrail
```

The module dependency graph is acyclic:

```text
inventory ──> catalog
orders ─────> customers + catalog + inventory
```

Interfaces are the ports. Application services implement inbound ports, JPA adapters implement
outbound repository ports, and constructor injection is the glue. The domain and public API types
have no Spring or Jakarta dependencies. Spring annotations stay on application services and adapters,
where dependency injection and transaction boundaries are infrastructure concerns.

Order placement validates the customer and active product, then reserves inventory and saves a
price/name snapshot in one database transaction. Inventory uses a pessimistic row lock so two
concurrent orders cannot spend the same stock. Lifecycle changes also lock the order row, so even
concurrent repeated cancellations release stock exactly once.

Spring Modulith verifies module access, named interfaces, cycles, and declared dependencies.
ArchUnit separately enforces hexagonal dependency direction and framework-free domain/API code.

## Requirements

- Java 25
- Maven 3.9+
- Docker with Docker Compose 2.2+

## Run

```bash
cd ~/Desktop/hexagonal-orders
mvn spring-boot:run
```

Spring Boot discovers `compose.yaml`, starts PostgreSQL, waits for its health check, and injects
the connection details. Stopping the JVM stops PostgreSQL; the named volume preserves data.
Flyway owns the schema and Hibernate only validates it.

Docker Compose integration is a development-time dependency, so it is intentionally excluded from
the executable archive. To run the packaged JAR, start the infrastructure explicitly and pass the
discovered random PostgreSQL port:

```bash
mvn clean package
docker compose up -d --wait
commerce_postgres_port=$(docker compose port postgres 5432 | awk -F: '{print $NF}')

SPRING_DATASOURCE_URL="jdbc:postgresql://127.0.0.1:${commerce_postgres_port}/commerce" \
SPRING_DATASOURCE_USERNAME=commerce \
SPRING_DATASOURCE_PASSWORD=commerce \
java -jar target/commerce-0.0.1-SNAPSHOT.jar
```

## Try the workflow

Register a customer:

```bash
curl -i -X POST http://localhost:8080/api/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ada Lovelace","email":"ada@example.com"}'
```

Create a product:

```bash
curl -i -X POST http://localhost:8080/api/products \
  -H 'Content-Type: application/json' \
  -d '{"name":"Mechanical keyboard","price":129.90}'
```

Use the returned IDs to add five units and place an order for two:

```bash
curl -X POST http://localhost:8080/api/inventory/{productId}/restocks \
  -H 'Content-Type: application/json' \
  -d '{"quantity":5}'

curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"{customerId}","productId":"{productId}","quantity":2}'
```

Then inspect or change the order lifecycle:

```bash
curl http://localhost:8080/api/orders/{orderId}
curl -X PUT http://localhost:8080/api/orders/{orderId}/confirmation
curl -X PUT http://localhost:8080/api/orders/{orderId}/cancellation
curl http://localhost:8080/api/inventory/{productId}
```

## HTTP use cases

| Module | HTTP | Endpoint | Use case |
|---|---|---|---|
| Customers | `POST` | `/api/customers` | Register customer |
| Customers | `GET` | `/api/customers/{id}` | Get customer |
| Customers | `GET` | `/api/customers` | List customers |
| Catalog | `POST` | `/api/products` | Create product |
| Catalog | `GET` | `/api/products/{id}` | Get product |
| Catalog | `GET` | `/api/products` | List products |
| Catalog | `PUT` | `/api/products/{id}/deactivation` | Deactivate product |
| Inventory | `POST` | `/api/inventory/{productId}/restocks` | Add stock |
| Inventory | `GET` | `/api/inventory/{productId}` | Get stock |
| Orders | `POST` | `/api/orders` | Place order and reserve stock |
| Orders | `GET` | `/api/orders/{id}` | Get order |
| Orders | `GET` | `/api/orders` | List orders |
| Orders | `PUT` | `/api/orders/{id}/confirmation` | Confirm order |
| Orders | `PUT` | `/api/orders/{id}/cancellation` | Cancel order and release stock |

Validation and business failures use RFC 9457 problem responses. Examples include `404` for an
unknown resource, `409` for insufficient stock or an invalid state transition, and `422` when an
order references an unknown customer or unavailable product.

## Database and verification

Run PostgreSQL separately when desired:

```bash
docker compose up -d --wait
mvn spring-boot:run
```

Inspect all module tables:

```bash
docker compose exec postgres psql -U commerce -d commerce \
  -c '\dt' \
  -c 'select * from customers;' \
  -c 'select * from products;' \
  -c 'select * from inventory;' \
  -c 'select * from orders;'
```

Run all domain, application, architecture, and module-boundary tests:

```bash
mvn clean verify
```

Remove PostgreSQL and its persisted development data:

```bash
docker compose down -v
```
