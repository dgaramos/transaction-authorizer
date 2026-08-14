# CLAUDE.md

## Project Overview

Transaction Authorizer is a Spring Boot + Kotlin REST API that authorizes pseudo credit card transactions for a benefit club. Transactions are routed to FOOD, MEAL, or CASH balances based on MCC code and merchant name. When a specific balance is insufficient, the service falls back to the CASH balance before denying.

The stack is: Kotlin 1.9, Spring Boot 3.4, Jetbrains Exposed (SQL DSL), Flyway, PostgreSQL 16, Docker Compose.

---

## Architecture

The codebase follows a layered architecture with interface/implementation separation at every layer:

```
controller/          → REST endpoints, request/response models
  model/request/     → inbound DTOs
  model/response/    → outbound DTOs
service/             → business logic interfaces
  implementations/   → concrete service classes
repository/          → data access interfaces
  implementations/   → concrete repository classes (Exposed DSL)
model/               → domain models (data classes extending BaseModel)
  table/             → Exposed table objects (extend BaseTable)
exceptions/          → typed exception classes per error scenario
constants/           → MCC lists, merchant name lists
utils/               → stateless helpers (e.g. balance type resolution)
```

Every service and repository is declared as an interface first; the implementation lives in the `implementations/` subdirectory and is annotated with `@Service` or `@Repository`. Never put business logic directly in a controller.

---

## Development Rules

### Adding a new feature

1. Define the interface in `service/` or `repository/`.
2. Implement in the `implementations/` subdirectory.
3. Add request/response DTOs under `controller/model/request` and `controller/model/response`.
4. Write a controller test (Mockito + MockMvc, no Spring context) and a service/repository test.
5. If a new table is needed, add an Exposed table object under `model/table/` and create a new Flyway migration (`V{n}__description.sql`). Never edit existing migrations.

### Database migrations

- All schema changes go through Flyway in `src/main/resources/db/migration/`.
- File naming: `V{n}__{snake_case_description}.sql` (e.g. `V2__add_merchant_index.sql`).
- Never modify an already-applied migration. Always add a new versioned file.
- Seed data for local dev goes in `src/main/resources/db/dev/data_init.sql` and is loaded via `./transaction-authorizer.sh --first-data-charge`.

### MCC / merchant routing

- MCC codes are declared in `constants/MccLists.kt`. Add new codes there.
- Merchant name overrides are declared in `constants/MerchantNames.kt`. Merchant name takes precedence over MCC.
- Balance type resolution logic lives exclusively in `utils/AccountBalanceTypeUtils.kt`.

### Concurrency

`BaseRepository` implements optimistic locking via a `version` column. Every domain model extends `BaseModel` (which carries `id`, `version`, `createdAt`, `updatedAt`). Every update increments `version` and rejects stale writes with `OptimisticLockException`.

---

## Testing

Tests use JUnit 5 + Mockito Kotlin. No Spring context is loaded for controller or unit tests — use `MockitoAnnotations.openMocks(this)` and `MockMvcBuilders.standaloneSetup(...)`.

Repository integration tests extend `BaseRepositoryIntegrationTest` and run against an in-memory H2 database (profile `test`). The `application-test.properties` disables Flyway and uses `ddl-auto=update`.

Run all tests:
```bash
./gradlew test
```

---

## Git Workflow

### Conventional Commits

All commits must follow Conventional Commits format:

```
type(scope): description
```

Allowed types: `feat`, `fix`, `docs`, `refactor`, `chore`, `test`, `build`, `ci`

Examples:
```
feat(transaction): add merchant-name override for MEAL routing
fix(repository): handle stale version in optimistic lock retry
docs(readme): update script usage after image deprecation fix
refactor(service): extract cash fallback into dedicated method
chore(docker): pin postgres image to version 16
test(controller): add missing denial scenario to ReceiveTransactionControllerTest
```

### Branch naming

Branches follow the same type/scope pattern as commits:

```
type/short-description
```

Examples:
```
feat/merchant-name-routing
fix/postgres-image-deprecation
refactor/cash-fallback-logic
chore/update-dependencies
```

Use `main` as the integration branch. Feature branches are short-lived and merged via PR.

---

## Running the Project

The project ships a `dev` CLI (via `bin/dev` + `.envrc`). When inside the project directory, `direnv` adds `bin/` to `PATH` automatically so `dev` is available without a path prefix.

```bash
dev                  # interactive fzf menu with all commands
dev --help           # show all available commands

dev app start        # build + test + start all containers
dev app stop         # stop all containers
dev app logs         # follow application logs
dev app restart      # stop and start (no purge)

dev db start         # start PostgreSQL only
dev db migrate       # run Flyway migrations
dev db clean         # drop all schema objects
dev db rebuild       # drop schema and re-run migrations
dev db seed          # load initial data from data_init.sql

dev test             # run the full test suite
dev build            # build without running tests
dev purge            # wipe containers, volumes and cache, then restart
```

The underlying `transaction-authorizer.sh` is still available for direct use. See `README.md` for the full setup flow.

The app exposes Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

---

## What NOT to do

- Do not put business logic in controllers.
- Do not bypass the interface layer (never inject `*Impl` classes directly).
- Do not edit applied Flyway migrations.
- Do not use `docker volume prune` or `docker network prune` — use `docker compose down -v` instead to avoid affecting other local projects.
- Do not use `postgres:latest` — it is pinned to `postgres:16` for stability.
