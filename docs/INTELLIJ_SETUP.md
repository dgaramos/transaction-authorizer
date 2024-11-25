# Running the Project in IntelliJ IDEA

## Prerequisites

1. **Install IntelliJ IDEA**:  
   Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/).

2. **Install Java 17 (OpenJDK)**:  
   Ensure Java 17 is installed and set up on your system.

## Steps to Set Up the Project

1. **Import the Project**:
    - Open IntelliJ IDEA and select **Open**.
    - Navigate to the root directory of the project and open it.

2. **Configure the JDK**:
    - Go to **File > Project Structure > SDKs**.
    - Add **Java 17 (OpenJDK)** as the project SDK.

3. **Build the Project**:
    - Open the **Terminal** in IntelliJ and run:
      ```bash
      ./gradlew clean build
      ```

4. **Set Up the Database (PostgreSQL)**:
    - Start the PostgreSQL container using Docker:
      ```bash
      docker-compose up postgres-dev
      ```

5. **Run the Application**:
    - Locate the `TransactionAuthorizerApplication.kt` file in the `src/main/kotlin/br/com/transactionauthorizer` package.
    - Right-click the file and select **Run 'TransactionAuthorizerApplication'**.

## Access the Application

- Application: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Stopping Services

1. **Stop PostgreSQL Container**:
    - Open a terminal and run:
      ```bash
      docker-compose down
      ```

2. **Clean Up Docker Resources** (Optional):
    - Remove volumes and unused resources:
      ```bash
      docker volume prune -f
      ```

## Notes

- Update the `compose.yml` file for custom database configurations if needed.
- If you encounter issues with Gradle, use IntelliJ's built-in Gradle tool to refresh dependencies.
