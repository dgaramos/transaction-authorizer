# AGENTS.md

Guidance for AI agents working in this repository.

---

## Understand the project first

Read `CLAUDE.md` before making any change. It documents the architecture, layering rules, migration policy, and git conventions. This file (`AGENTS.md`) covers agent-specific behaviour on top of that.

---

## Architecture constraints

The project uses a strict interface/implementation pattern:

- Every service and repository has an interface in the parent package and its implementation in `implementations/`.
- Never inject `*Impl` classes directly. Always depend on the interface.
- Never add business logic to a controller. Controllers delegate to services.
- New domain models must extend `BaseModel`. New tables must extend `BaseTable`.

---

## Making changes

### Code changes

1. Identify the right layer: controller → service → repository.
2. If modifying behaviour, update the interface signature first, then the implementation.
3. Add or update the corresponding test. Controller tests use MockMvc + Mockito (no Spring context). Repository tests use H2 via `BaseRepositoryIntegrationTest`.
4. Run tests before finishing: `./gradlew test`.

### Database changes

- Schema changes: add a new `V{n}__description.sql` file in `src/main/resources/db/migration/`. Never edit existing migrations.
- Seed data: edit `src/main/resources/db/dev/data_init.sql`. It is loaded manually and does not run automatically.

### MCC / merchant routing changes

- MCC code lists live in `constants/MccLists.kt`.
- Merchant name overrides live in `constants/MerchantNames.kt`.
- Routing logic lives only in `utils/AccountBalanceTypeUtils.kt`. Do not duplicate it elsewhere.

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

## Docker and infrastructure

- PostgreSQL image is pinned to `postgres:16`. Do not change to `latest`.
- The base JVM image is `eclipse-temurin:17-jre-jammy`. Do not use `openjdk:*` images — they are deprecated on Docker Hub.
- To reset the local environment safely: `./transaction-authorizer.sh --purge-cache`. This calls `docker compose down -v`, which scopes cleanup to the project only.
- Never call `docker volume prune` or `docker network prune` directly — they affect all local Docker projects.

---

## What to avoid

- Do not create or modify files outside the project unless explicitly asked.
- Do not run `./transaction-authorizer.sh --purge-cache` or `docker compose down -v` without confirming with the user — it deletes all local data.
- Do not run `--first-data-charge` without confirming — it truncates and re-seeds all tables.
- Do not amend published commits.
- Do not skip tests (`-x test`) unless the user explicitly asks.
