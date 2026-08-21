# Development guide

## Prerequisites

- JDK 25 or newer capable of compiling with `--release 25`
- Maven 3.9+
- Docker Engine or Docker Desktop
- Docker Compose 2.2+

Confirm the local tools:

```bash
java -version
mvn -version
docker version
docker compose version
```

## Fastest local startup

From the repository root:

```bash
mvn spring-boot:run
```

The `spring-boot-docker-compose` development dependency discovers `compose.yaml`. During startup,
Spring Boot:

1. creates the Compose network and named PostgreSQL volume when needed;
2. starts PostgreSQL on a random available host port;
3. waits for `pg_isready` to report a healthy database;
4. supplies the JDBC connection details to the application;
5. runs Liquibase changesets;
6. starts the HTTP server on port `8080`.

With `lifecycle-management: start-and-stop`, stopping the JVM also stops the Compose service that the
application started. The named volume keeps development data between runs.

No local datasource URL is required for this development path.

## Start infrastructure separately

```bash
docker compose up -d --wait
docker compose ps
mvn spring-boot:run
```

Spring Boot detects the already running service and uses its mapped PostgreSQL port.

Inspect the mapped port:

```bash
docker compose port postgres 5432
```

Inspect the schema and data:

```bash
docker compose exec postgres psql -U commerce -d commerce \
  -c '\dt' \
  -c 'select id, author, filename, exectype from databasechangelog order by orderexecuted;' \
  -c 'select * from customers;' \
  -c 'select * from products;' \
  -c 'select * from inventory;' \
  -c 'select * from orders;'
```

Stop the services while retaining data:

```bash
docker compose down
```

Remove the services and development data:

```bash
docker compose down -v
```

The `-v` command deletes the project-specific PostgreSQL volume and cannot be undone.

## Build and run the executable JAR

Create the packaged application:

```bash
mvn clean package
```

The executable artifact is:

```text
target/commerce-0.0.1-SNAPSHOT.jar
```

Spring Boot's Docker Compose integration is development-time tooling and is excluded from the
executable JAR. Start PostgreSQL explicitly and pass datasource settings when running the artifact:

```bash
docker compose up -d --wait
commerce_postgres_port=$(docker compose port postgres 5432 | awk -F: '{print $NF}')

SPRING_DATASOURCE_URL="jdbc:postgresql://127.0.0.1:${commerce_postgres_port}/commerce" \
SPRING_DATASOURCE_USERNAME=commerce \
SPRING_DATASOURCE_PASSWORD=commerce \
java -jar target/commerce-0.0.1-SNAPSHOT.jar
```

The same environment variables can point the JAR at any compatible PostgreSQL instance.

## Database migrations

Liquibase is the only schema migration mechanism. Hibernate is configured with:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Hibernate therefore validates mappings against the migrated schema but never creates or alters
tables. Spring Boot loads Liquibase's conventional master changelog without extra configuration:

```text
src/main/resources/db/changelog
├── db.changelog-master.yaml
└── changes
    ├── 001-create-customers.yaml
    ├── 002-create-products.yaml
    ├── 003-create-inventory.yaml
    └── 004-create-orders.yaml
```

The master changelog explicitly includes each file in dependency order. Each included file owns one
stable changeset with a unique `id` and `author`, named constraints, and an explicit rollback. The
changesets use Liquibase change types instead of embedding PostgreSQL DDL where a portable change
type exists. PostgreSQL check constraints use small `sql` changes because the Community dependency
does not include the commercial `addCheckConstraint` change type.

Liquibase stores applied changesets in `databasechangelog` and coordinates concurrent startup with
`databasechangeloglock`.

### One-time transition from Flyway

Flyway and Liquibase maintain different history tables. This teaching application replaces the old
Flyway history with a Liquibase baseline of the current schema rather than implementing a production
data migration between tools.

If the local Compose volume was created by a Flyway-based version, back up anything you need and
reset it once before starting this version:

```bash
docker compose down --volumes
```

This deletes the local PostgreSQL data. Production systems with data would require a planned
Liquibase baseline, usually using `changelog-sync`, rather than deleting the database.

### Add a migration

1. Never edit a changeset that may already have run in a shared environment.
2. Add the next ordered file, for example:

   ```text
   src/main/resources/db/changelog/changes/005-describe-the-change.yaml
   ```

3. Give every changeset a stable, unique `id` and `author`; use named constraints and define rollback
   behavior where rollback is safe.
4. Add an explicit `include` to `db.changelog-master.yaml`. Do not rely on filesystem ordering.
5. Make upgrades safe for both populated and empty databases.
6. Run `mvn clean verify`.
7. Start the application against a fresh volume to test the complete changelog.
8. When compatibility matters, also test upgrading a database at the previous application version.

Liquibase validates changeset checksums and applies pending changesets during application startup.

## Test suite

Run the complete verification gate:

```bash
mvn clean verify
```

The suite covers four categories:

- domain tests for business invariants and state transitions;
- application-service tests using in-memory port implementations;
- a Spring Modulith test for module cycles, named interfaces, and allowed dependencies;
- ArchUnit rules for application ownership of ports, directional adapter placement, inward
  dependencies, framework isolation, and dependency injection through port interfaces.

Spring Modulith checks the module graph. ArchUnit stays focused on internal hexagonal boundaries and
does not enforce class-name suffixes.

Run one test class while iterating:

```bash
mvn -Dtest=OrderApplicationServiceTests test
```

Run only architecture verification classes:

```bash
mvn -Dtest=ModularityTests,HexagonalArchitectureTests test
```

## Package conventions

When adding a use case, keep the module-first structure:

```text
com.codillas.academy.commerce.<module>
├── domain
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── service
└── adapter
    ├── in
    │   └── web
    └── out
        └── persistence
```

Guidelines:

- extend the cohesive `*UseCases` interface under `application.port.in` and inject it into the
  controller;
- keep narrower cross-module inbound ports separate when callers need only part of the module;
- implement inbound ports under `application.service`;
- put domain rules in framework-free domain types;
- define required repository or integration interfaces under `application.port.out`;
- implement outbound ports under `adapter.out.persistence` or another `adapter.out` technology;
- put HTTP controllers and transport DTOs under `adapter.in.web`;
- expose `application.port.in` as the Spring Modulith named interface `api`;
- update `allowedDependencies` when introducing a legitimate new module dependency;
- use constructor injection rather than field injection;
- keep REST DTOs and JPA entities out of application ports.

The [architecture guide](architecture.md) explains the dependency rules and transaction model in
more detail.

## Documentation maintenance

- Keep the endpoint table and examples in [api.md](api.md) synchronized with controllers and web
  request/response records.
- Keep the changelog layout in this guide synchronized with the Liquibase master file.
- The architecture diagram embedded in the root README must match
  [commerce-hexagonal-architecture.mmd](diagrams/commerce-hexagonal-architecture.mmd).
- Mermaid syntax can be validated with:

  ```bash
  npx --yes @mermaid-js/mermaid-cli \
    -i docs/diagrams/commerce-hexagonal-architecture.mmd \
    -o /tmp/commerce-architecture.svg
  ```

## Troubleshooting

### Port 8080 is occupied

Run with a different HTTP port:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### PostgreSQL does not become healthy

```bash
docker compose ps
docker compose logs postgres
```

If the development data is disposable, recreate the project volume:

```bash
docker compose down -v
mvn spring-boot:run
```

### Schema validation fails

Check Liquibase history and compare the failing JPA column with the latest changeset:

```bash
docker compose exec postgres psql -U commerce -d commerce \
  -c 'select * from databasechangelog order by orderexecuted;'
```

Do not switch Hibernate to `ddl-auto: update`; fix the migration or mapping mismatch explicitly.
