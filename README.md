# Transaction Authorizer

This is an example **transaction authorization system** that validates and stores pseudo credit card transactions of a benefit club which has various types of balances (FOOD, MEAL, etc.) and has a miscellaneous type of balance for other types of transactions (CASH).

The system integrates with a **PostgreSQL database** for persistence and exposes a **REST API** for external interaction. All available endpoints can be found in the [**Swagger** documentation](#swagger-documentation).

---

## 📌 Table of Contents

1. **[Project Overview](#project-overview)**
2. **[Project Stack](#project-stack)**
3. **[Database Structure](#database-structure)**
4. **[Running the Project](#running-the-project)**
   - [Using the Provided Script](#running-the-project-with-the-script)
   - [Manual Setup](docs/MANUAL_SETUP.md)
   - [IntelliJ IDEA Setup](docs/INTELLIJ_SETUP.md)
5. **[Swagger Documentation](#swagger-documentation)**
6. **[Additional Resources](#additional-resources)**
7. **[Folder Structure](#folder-structure)**
8. **[Notes](#notes)**

---

## Project Stack

The Transaction Authorizer Application leverages the following technologies:

| **Technology**         | **Description**                                                                                               | **Badge**                                                                                                                                              |
|-------------------------|-------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Kotlin**             | Primary programming language for development.                                                              | ![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?logo=kotlin&logoColor=white&style=flat-square)                                                   |
| **Spring Boot**        | Java-based framework for creating web applications and REST APIs.                                          | ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=spring-boot&logoColor=white&style=flat-square)                                     |
| **Java 17 (OpenJDK)**  | Runtime environment ensuring compatibility with modern Java features.                                       | ![Java](https://img.shields.io/badge/Java_17-007396?logo=java&logoColor=white&style=flat-square)                                                      |
| **PostgreSQL**         | Relational database for transaction storage and data persistence.                                          | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?logo=postgresql&logoColor=white&style=flat-square)                                       |
| **Gradle**             | Build tool for compiling, testing, and managing dependencies.                                              | ![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white&style=flat-square)                                                   |
| **Docker**             | Containerization platform for the application and its dependencies.                                       | ![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white&style=flat-square)                                                   |
| **Swagger**            | API documentation tool for visualizing and testing the REST APIs.                                           | ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=white&style=flat-square)                                                |

---

## Project Overview

The **Transaction Authorizer** is a Spring Boot application designed to:

1. Connect to a PostgreSQL database.
2. Authenticate and authorize transactions based on specific rules.
3. Provide a REST API for transaction authorization.

The application is configured to run in Docker containers with PostgreSQL as the backend database. A custom script is provided to streamline the process of building, running, and managing the application.

---

## Database Structure

The database consists of three main tables:

- **account**: Stores information about accounts.
- **account_balance**: Manages different balance types (FOOD, MEAL, CASH) for each account.
- **card_transaction**: Tracks transactions and their statuses.

Below is the **Entity Relationship Diagram (ERD)** illustrating the database structure:

![Transaction Authorizer ERD](https://github.com/user-attachments/assets/f49d350d-26e1-4f1d-99fa-be830b40f2c9)

---

## Running the Project

You can run the project in multiple ways:

1. **Using the Provided Script** (Recommended):  
   Follow the steps in [Running the Project with the Script](#running-the-project-with-the-script) below for quick setup.

2. **[Manual Setup](docs/MANUAL_SETUP.md)**:  
   Set up and run the project manually without the script.

3. **[IntelliJ IDEA Setup](docs/INTELLIJ_SETUP.md)**:  
   Develop and run the application in IntelliJ IDEA.

---

### Running the Project with the Script

The easiest way to run the project is using the `transaction-authorizer.sh` script. Here’s how:

1. **Ensure Prerequisites**:
   - Docker and Docker Compose are installed and running on your machine.
   - The script has execution permissions:
     ```bash
     chmod +x transaction-authorizer.sh
     ```

2. **Start Database**:
   Run the script to start only the database so you can run the migrations:
   ```bash
   ./transaction-authorizer.sh --start-database
   ```

3. **Run Migrations**:
   Run the script to run the migrations using flyway to generate the database schema:
   ```bash
   ./transaction-authorizer.sh --run-migrations
   ```
 4. **Start the Project**:
   Run the script to build and start the application:
   ```bash
   ./transaction-authorizer.sh --start-application
   ```

4. **Reset Environment**:
   If you need to purge cache, use the `--purge-cache` option:
   ```bash
   ./transaction-authorizer.sh --purge-cache
   ```

5. **Access the Application**:
   Once started, the application will be available at [http://localhost:8080](http://localhost:8080).

If you encounter issues with the script or want more details on its features, refer to the [Script Usage Guide](docs/SCRIPT_USAGE.md).

---

## Swagger Documentation

This project includes Swagger for API documentation, allowing you to interact with the available REST APIs through an interactive UI.

### Accessing Swagger UI

1. [**Run the application**](#running-the-project).
2. Open your browser and go to:
   ```
   http://localhost:8080/swagger-ui.html
   ```
   From here, you can explore and test API endpoints.

For more detailed instructions on using Swagger and exporting to Postman, see the [Swagger Usage Guide](docs/SWAGGER_USAGE.md).

---

## Additional Resources

- **[Manual Setup Guide](docs/MANUAL_SETUP.md)**:  
  Step-by-step instructions for running the project manually without the script.

- **[IntelliJ IDEA Setup Guide](docs/INTELLIJ_SETUP.md)**:  
  A guide to running and developing the application using IntelliJ IDEA.

- **[Script Usage Guide](docs/SCRIPT_USAGE.md)**:  
  Detailed instructions for using the provided script.

---

## Folder Structure

- `src/`: Contains the source code of the Spring Boot application.
- `run-transaction-authorizer.sh`: The automation script for building and running the application.
- `build.gradle`: The Gradle build file for the project.
- `application.properties`: The configuration file for Spring Boot.
- `docs/`: Contains all the documentation files, including:
   - `MANUAL_SETUP.md`: Guide for manually running the project.
   - `INTELLIJ_SETUP.md`: Guide for IntelliJ IDEA usage.
   - `SCRIPT_USAGE.md`: Guide for using the provided script.

---

## Notes

- Use the provided script for quick setup and testing.
- For development and debugging, IntelliJ IDEA is recommended.
- If you're setting up the project in a custom environment, follow the Manual Setup Guide.
