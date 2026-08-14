# Manual Setup

Follow these steps to run the application without the `dev` CLI or `transaction-authorizer.sh`.

---

## Prerequisites

1. **Docker and Docker Compose** — verify with:
   ```bash
   docker --version
   docker compose version
   ```

2. **Eclipse Temurin 17** — required to run Gradle commands locally.

3. **Gradle** — optional; the `./gradlew` wrapper in the project can be used instead.

---

## Steps

1. **Build the application**:
   ```bash
   ./gradlew clean build
   ```
   This compiles the source, runs all tests, and produces a `.jar` in `build/libs/`.

2. **Start the database**:
   ```bash
   docker compose up postgres-dev
   ```
   Wait until the container is ready — check with `docker logs postgres-dev`.

3. **Run the application**:
   ```bash
   java -jar build/libs/transaction-authorizer-<version>.jar
   ```
   Replace `<version>` with the actual version from the jar filename.

4. **Access the application**:
   - API: [http://localhost:8080](http://localhost:8080)
   - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Cleanup

```bash
# Stop all containers and remove project volumes
docker compose down -v

# Remove build artifacts
rm -rf build
```

---

## Notes

- Database credentials and host are configured in `src/main/resources/application.properties`.
- To build without running tests: `./gradlew clean build -x test`
- If the application fails to start, check Docker logs for connection errors.
