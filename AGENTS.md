# AGENTS.md

Guidance for AI agents working in this repository.

---

## Understand the project first

Read `CLAUDE.md` before making any change. It documents the architecture, layering rules, migration policy, and git conventions. This file (`AGENTS.md`) covers agent-specific behaviour on top of that.

---

## Architecture constraints

The project follows Hexagonal Architecture (Ports & Adapters). The dependency rule is strict: outer layers depend on inner layers, never the other way around.

- Every service and repository has an interface (port) in the parent package and its implementation in `implementations/`.
- Never inject `*Impl` classes directly. Always depend on the interface.
- Never add business logic to a controller. Controllers translate DTOs into domain commands and delegate to services.
- Services, domain classes, and repositories must never import from `controller/`.
- New domain models must extend `BaseModel`. New tables must extend `BaseTable`.
- Routing logic belongs in `model/routing/`. Never add MCC or merchant routing to a service.

---

## Making changes

### Code changes

1. Identify the right layer: controller → service (port) → repository (port) → implementation.
2. If modifying behaviour, update the interface signature first, then the implementation.
3. Add or update the corresponding test. Controller tests use MockMvc + MockK (no Spring context). Integration tests extend `AbstractSpringIntegrationTest` and run against PostgreSQL via Testcontainers.
4. Run tests before finishing: `./gradlew test`.

### Database changes

- Schema changes: add a new `V{n}__description.sql` file in `src/main/resources/db/migration/`. Never edit existing migrations.
- Seed data: edit `src/main/resources/db/dev/data_init.sql`. It is loaded manually and does not run automatically.

### MCC / merchant routing changes

- MCC codes live in `model/routing/MccRegistry.kt`.
- Merchant name overrides live in `model/routing/MerchantRegistry.kt`.
- Routing logic lives only in `model/routing/BalanceTypeRouter.kt`. Do not duplicate it elsewhere.
- Callers use `TransactionCommand.resolveBalanceType()` — the only valid call site.

---

## Commits

All commits must use Conventional Commits format:

```
type(scope): description
```

Allowed types: `feat`, `fix`, `docs`, `refactor`, `chore`, `test`, `build`, `ci`

Descriptions are concise, imperative, in English.

## Branch naming

```
type/short-description
```

Examples: `feat/meal-mcc-expansion`, `fix/optimistic-lock-retry`, `chore/upgrade-exposed`.

---

## Running commands

Prefer `dev` over calling `transaction-authorizer.sh` directly — it is the intended interface.

```bash
dev --help        # list all commands
dev app start     # start the full stack
dev db migrate    # run migrations
dev test          # run tests
```

`dev` is available when inside the project directory (direnv loads `bin/` into PATH via `.envrc`). Outside the directory, use the full path `bin/dev` or call `transaction-authorizer.sh` directly.

---

## Docker and infrastructure

- PostgreSQL image is pinned to `postgres:16`. Do not change to `latest`.
- The base JVM image is `eclipse-temurin:17-jre-jammy`. Do not use `openjdk:*` images — they are deprecated on Docker Hub.
- To reset the local environment safely: `./transaction-authorizer.sh --purge-cache`. This calls `docker compose down -v`, which scopes cleanup to the project only.
- Never call `docker volume prune` or `docker network prune` directly — they affect all local Docker projects.

---

## What to avoid

- Do not use star imports (`import foo.*`). Always list each import explicitly.
- Do not create or modify files outside the project unless explicitly asked.
- Do not run `./transaction-authorizer.sh --purge-cache` or `docker compose down -v` without confirming with the user — it deletes all local data.
- Do not run `--first-data-charge` without confirming — it truncates and re-seeds all tables.
- Do not amend published commits.
- Do not skip tests (`-x test`) unless the user explicitly asks.
