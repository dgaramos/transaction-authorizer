# Transaction Authorizer

[![CI](https://github.com/dgaramos/transaction-authorizer/actions/workflows/ci.yml/badge.svg)](https://github.com/dgaramos/transaction-authorizer/actions/workflows/ci.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)

A Spring Boot REST API that authorizes pseudo credit card transactions for a benefit club. Transactions are routed to **FOOD**, **MEAL**, or **CASH** balances based on MCC code and merchant name. When a specific balance is insufficient, the service falls back to the CASH balance before denying.

---

## Table of Contents

1. [Database Structure](#database-structure)
2. [Running the Project](#running-the-project)
3. [Swagger Documentation](#swagger-documentation)
4. [Folder Structure](#folder-structure)
5. [Development Reference](#development-reference)

---

## Database Structure

Three tables managed by Flyway migrations:

- **account** — stores account records
- **account_balance** — one row per balance type (FOOD, MEAL, CASH) per account
- **card_transaction** — transaction history with status and routed balance

![Transaction Authorizer ERD](https://github.com/user-attachments/assets/f49d350d-26e1-4f1d-99fa-be830b40f2c9)

---

## Running the Project

The project ships a `dev` CLI (via `bin/dev` + `.envrc`). With [direnv](https://direnv.net) installed, `dev` is available as soon as you enter the project directory.

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

**Prerequisites**: Docker running, direnv installed (`brew install direnv`).

For alternative setup options see [Manual Setup](docs/MANUAL_SETUP.md) and [IntelliJ IDEA Setup](docs/INTELLIJ_SETUP.md).

---

## Swagger Documentation

With the application running, the interactive API docs are available at:

```
http://localhost:8080/swagger-ui/index.html
```

For instructions on exporting to Postman, see the [Swagger Usage Guide](docs/SWAGGER_USAGE.md).

---

## Folder Structure

```
src/                        Spring Boot application source
bin/dev                     Developer CLI (fzf menu wrapping the script)
transaction-authorizer.sh   Underlying automation script
compose.yaml                Docker Compose (postgres-dev + application)
Dockerfile                  Eclipse Temurin 17 container for the app
build.gradle.kts            Gradle build file
.envrc                      direnv config — adds bin/ to PATH
CLAUDE.md                   Architecture reference and dev conventions
AGENTS.md                   Behavioural constraints for AI agents
docs/                       Setup and usage guides
```

---

## Development Reference

Layered architecture: controller → service → repository, with interface/implementation separation at every layer. Commits follow [Conventional Commits](https://www.conventionalcommits.org/). Branches follow the same `type/description` pattern.

- [CLAUDE.md](CLAUDE.md) — architecture, layering rules, migration policy, git conventions, `dev` CLI reference
- [AGENTS.md](AGENTS.md) — guidance for AI agents working in this codebase
