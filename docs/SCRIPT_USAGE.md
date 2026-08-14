# Script Usage Guide: `transaction-authorizer.sh`

> **Prefer `dev` over calling the script directly.** The `bin/dev` CLI wraps all commands below with a friendlier interface and an interactive fzf menu. See [CLAUDE.md](../CLAUDE.md) for the full `dev` reference.

---

## Prerequisites

- Docker and Docker Compose installed and running.
- Script has executable permissions:
  ```bash
  chmod +x transaction-authorizer.sh
  ```

---

## Commands

### Start Application
Build, test, and start all containers (PostgreSQL + Spring Boot):
```bash
./transaction-authorizer.sh --start-application
```
Verifies Docker login and network connectivity before proceeding.

### Start Database Only
Start only the PostgreSQL container:
```bash
./transaction-authorizer.sh --start-database
```

### Run Migrations
Execute Flyway migrations against the running database:
```bash
./transaction-authorizer.sh --run-migrations
```

### Load Seed Data
Populate the database with initial data from `data_init.sql`:
```bash
./transaction-authorizer.sh --first-data-charge
```
> Only run this once on a fresh schema.

### Clean Database
Drop all tables via Flyway clean:
```bash
./transaction-authorizer.sh --clean-database
```

### Rebuild Database
Drop and re-run all migrations:
```bash
./transaction-authorizer.sh --rebuild-database
```

### Purge Folders
Remove the `build` directory and Docker credentials file:
```bash
./transaction-authorizer.sh --purge-folders
```

### Purge Cache and Restart
Stop containers, remove project volumes, and restart clean:
```bash
./transaction-authorizer.sh --purge-cache
```

### Full Purge (Folders + Cache)
Remove both build artifacts and Docker resources, then restart:
```bash
./transaction-authorizer.sh --purge-folders-and-cache-and-restart-application
```

---

## Troubleshooting

**Permission denied**: `chmod +x transaction-authorizer.sh`

**Not logged in to Docker**: `docker login`

**Network issues**: ensure internet connectivity for pulling Docker images.
