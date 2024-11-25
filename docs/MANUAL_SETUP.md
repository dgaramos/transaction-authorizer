---
### Running the Project Manually

If you prefer to run the application without using the provided script, follow these steps:

---

#### Prerequisites

1. **Install Docker and Docker Compose**:  
   Ensure Docker and Docker Compose are installed and running on your system. You can verify this by running:
   ```bash
   docker --version
   docker compose version
   ```

2. **Install Java 17 (OpenJDK)**:  
   Download and install OpenJDK 17 for running Gradle commands and building the project.

3. **Install Gradle** (Optional):  
   If Gradle is not installed, the `./gradlew` wrapper included in the project can be used instead.

---

#### Steps to Run the Application

1. **Build the Application**:  
   Use Gradle to clean and build the project:
   ```bash
   ./gradlew clean build
   ```

   This will compile the source code, run tests, and generate a runnable `.jar` file in the `build/libs` directory.

2. **Set Up the Database**:  
   Start the PostgreSQL service using Docker Compose:
   ```bash
   docker compose up postgres-dev
   ```

   Wait until the PostgreSQL container is fully initialized. You can check its logs to ensure it's ready:
   ```bash
   docker logs postgres-dev
   ```

3. **Run the Application**:  
   With the database running, start the Spring Boot application locally:
   ```bash
   java -jar build/libs/transaction-authorizer-<version>.jar
   ```

   Replace `<version>` with the actual version number of the generated `.jar` file (e.g., `1.0.0`).

4. **Access the Application**:
    - The application should now be running and accessible at [http://localhost:8080](http://localhost:8080).
    - Swagger UI for API documentation is available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

---

#### Cleaning Up

To stop and clean up the environment:

1. **Stop Services**:  
   Stop the PostgreSQL container:
   ```bash
   docker compose down
   ```

2. **Remove Docker Volumes (Optional)**:  
   If you want a clean slate, remove Docker volumes:
   ```bash
   docker volume prune -f
   ```

3. **Remove Build Artifacts (Optional)**:  
   If you want to remove build files:
   ```bash
   rm -rf build
   ```

---

#### Notes

- **Database Configuration**:  
  By default, the application connects to a PostgreSQL instance defined in `docker-compose.yaml`. If you're running PostgreSQL outside Docker, update the database credentials in `application.properties` or `application.yml`.

- **Custom Gradle Commands**:  
  If you need to skip tests or use additional build options, modify the Gradle command accordingly. For example:
   ```bash
   ./gradlew clean build -x test
   ```

- **Troubleshooting**:
    - If the application fails to start, check the logs for errors in the database connection or application configuration.
    - Ensure the PostgreSQL container is running and reachable at the configured host and port.

---