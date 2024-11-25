# Script Usage Guide: `run-transaction-authorizer.sh`

This guide explains how to use the provided script, `run-transaction-authorizer.sh`, to manage the **Transaction Authorizer** application.

---

## Prerequisites

Before running the script, ensure that the following are set up:

1. **Docker**:
   Ensure Docker and Docker Compose are installed and running on your machine.

2. **Executable Permissions**:
   Make sure the script has executable permissions. Run the following command if necessary:
   ```bash
   chmod +x transaction-authorizer.sh
   ```

---

## Script Commands

The script provides a set of commands for building, testing, managing the database, and running the application.

### 1. **Default Command** (Start the Application)
To build, test, and start the application (including PostgreSQL and the Spring Boot service):
   ```bash
   ./transaction-authorizer.sh
   ```
This command performs the following:
- Builds the application using Gradle.
- Runs tests to ensure that the application is working correctly.
- Starts Docker containers for PostgreSQL and the Spring Boot application.

---

### 2. **Purge Cache and Restart Application**
To clean all Docker containers and cached volumes before starting the application:
   ```bash
   ./transaction-authorizer.sh --purge-cache
   ```
This will:
- Stop and remove all Docker containers.
- Remove unused Docker volumes and networks.
- Build, test, and restart the application.

---

### 3. **Purge Folders Only**
To clean the `build` folder and remove the Docker credentials file (`~/.docker/config.json`):
   ```bash
   ./transaction-authorizer.sh --purge-folders
   ```
This command only cleans local folders and doesn't affect Docker containers or volumes.

---

### 4. **Full Purge (Folders + Cache)**
To remove both the cached Docker resources and temporary folders, and restart the application:
   ```bash
   ./transaction-authorizer.sh --purge-folders-and-cache
   ```
This command will:
- Purge both Docker containers/volumes and temporary folders (`build` and Docker credentials).
- Restart the application with a clean environment.

---

### 5. **Run Database Migrations**
To execute Flyway database migrations:
   ```bash
   ./transaction-authorizer.sh --run-migrations
   ```
This will run the Flyway migrations to ensure the database schema is up-to-date.

---

### 6. **Clean Database**
To clean the database schema using Flyway (removes all data and resets the schema):
   ```bash
   ./transaction-authorizer.sh --clean-database
   ```
This command will:
- Drop all database tables.
- Clean the database schema to prepare for fresh migrations.

---

### 7. **Rebuild Database**
To drop and rebuild the database schema:
   ```bash
   ./transaction-authorizer.sh --rebuild-database
   ```
This will:
- Clean and re-run migrations to reset the database schema.

---

### 8. **First Data Charge**
To initialize the database with initial data from the `data_init.sql` file:
   ```bash
   ./transaction-authorizer.sh --first-data-charge
   ```
This will:
- Copy the `data_init.sql` file into the running PostgreSQL container.
- Run the SQL file to populate the database with initial data.

---

### 9. **Start Application with All Checks**
To start the application with Docker login check and network connectivity check before proceeding:
   ```bash
   ./transaction-authorizer.sh --start-application
   ```
This will:
- Verify Docker login status.
- Test network connectivity by attempting to pull a test image from Docker Hub.
- If the checks pass, it will build, test, and start the application.

---

## Script Features

### Docker Login Check
- The script checks if you're logged in to Docker. If you're not, it will prompt you to log in.
   ```bash
   docker login
   ```

### Network Connectivity Test
- The script ensures Docker can pull images. If there's a network issue, the script will exit with an error message.

### Automated Build and Test
- Before starting the application, the script runs the following Gradle commands:
   ```bash
   ./gradlew clean build test
   ```
  This ensures that the application is successfully built and all tests pass.

---

## Troubleshooting

### Permission Issues
If you encounter a `permission denied` error, ensure that the script has executable permissions:
   ```bash
   chmod +x transaction-authorizer.sh
   ```

### Missing Docker Login
If you're not logged in to Docker, you can log in with:
   ```bash
   docker login
   ```

### Network Issues
If you experience network issues, ensure you have a stable internet connection for pulling Docker images.

---

## Notes

- The script is designed for both **local development** and **initial deployments**. It's especially useful for testing and running the application in a Dockerized environment.
- For manual setup or further configuration, refer to the [Manual Setup Guide](docs/MANUAL_SETUP.md).
- For IntelliJ IDEA setup, see the [IntelliJ Setup Guide](docs/INTELLIJ_SETUP.md).
