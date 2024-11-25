#!/bin/bash

# ==============================
# VARIABLES
# ==============================
DB_NAME="postgres-dev"
DB_CONTAINER_NAME="transaction-authorizer-$DB_NAME-1"
FLYWAY_MIGRATE_COMMAND="./gradlew flywayMigrate"
FLYWAY_CLEAN_COMMAND="./gradlew flywayClean"
DATA_INIT_FILE="./src/main/resources/db/dev/data_init.sql"

# ==============================
# DATABASE FUNCTIONS
# ==============================

# Function to execute flywayMigrate
run_migrations() {
  echo "Running flywayMigrate..."
  $FLYWAY_MIGRATE_COMMAND || { echo "Failed to run flywayMigrate"; exit 1; }
  echo "flywayMigrate completed successfully!"
}

# Function to execute flywayClean
clean_database() {
  echo "Running flywayClean..."
  $FLYWAY_CLEAN_COMMAND || { echo "Failed to run Flyway clean"; exit 1; }
  echo "flywayClean completed successfully!"
}

# Function to rebuild the database
rebuild_database() {
  echo "Dropping and rebuilding the database..."
  clean_database
  run_migrations
  echo "Database schema recreated successfully!"
}

# Function to populate the database with initial data
first_data_charge() {
  echo "Running the first data charge using $DATA_INIT_FILE..."

  if [ ! -f "$DATA_INIT_FILE" ]; then
    echo "Error: Data initialization file '$DATA_INIT_FILE' not found."
    exit 1
  fi

  docker cp "$DATA_INIT_FILE" "$DB_CONTAINER_NAME:/data_init.sql" || {
    echo "Failed to copy data_init.sql to the database container.";
    exit 1;
  }

  docker exec $DB_CONTAINER_NAME psql -U demo_dev_rw -d demo_db -f /data_init.sql || {
    echo "Failed to execute data_init.sql.";
    exit 1;
  }

  echo "First data charge completed successfully!"
}

# ==============================
# UTILITY FUNCTIONS
# ==============================

# Helper function to remove folders with cache files
purge_folders() {
  echo "Purging -r build and ~/.docker/config.json..."

  rm -r build

  rm ~/.docker/config.json

  echo "Folders -r build and ~/.docker/config.json purged."
}

# Helper function to remove Docker containers and volumes
purge_cache() {
  echo "Purging Docker containers and volumes..."

  # Stop and remove containers
  docker compose down

  # Remove volumes to ensure a fresh start
  docker volume prune -f
  docker network prune -f

  rm -r build
  rm ~/.docker/config.json

  echo "Docker containers and volumes purged."
}

# Function to build, test, and start the application
start_application() {
  echo "Building and testing the application..."

  # Run tests (assuming you use Gradle or Maven for tests)
  ./gradlew clean build test || { echo "Tests failed. Aborting."; exit 1; }

  # Build and start the Docker containers
  docker compose up --build -d

  echo "Application started successfully!"
}

# Function to check Docker login status
check_docker_login() {
  echo "Checking Docker login status..."

  # Check if Docker is logged in
  if ! docker info | grep -q 'Username'; then
    echo "Not logged in to Docker. Please log in."
    docker login || { echo "Docker login failed. Aborting."; exit 1; }
  else
    echo "Docker is already logged in."
  fi
}

# Function to check network connectivity by pulling a test image
check_network_connectivity() {
  echo "Checking Docker network connectivity..."

  # Test pulling an image to check network connectivity
  if ! docker pull openjdk:17-jdk-slim; then
    echo "Failed to pull the image. Check your network connection."
    exit 1
  else
    echo "Network connectivity is working."
  fi
}

# Function to build and start the database container only
start_database() {
  echo "Building and starting the PostgreSQL database container..."

  # Build and start the Docker containers for the database
  docker compose up -d postgres

  echo "Database container started successfully!"
}

# ==============================
# MAIN SCRIPT EXECUTION
# ==============================

# Command line argument parsing
case "$1" in
  --purge-folders-and-cache-and-restart-application)
    echo "Purging folders and cache and restarting application..."
    purge_folders
    purge_cache
    start_application
    ;;

  --purge-cache)
    echo "Purging cache and restarting application..."
    purge_cache
    start_application
    ;;

  --purge-folders)
    echo "Purging folders..."
    purge_folders
    ;;

  --run-migrations)
    run_migrations
    ;;

  --clean-database)
    clean_database
    ;;

  --rebuild-database)
    rebuild_database
    ;;

  --first-data-charge)
    first_data_charge
    ;;

  --start-application)
    # First-time setup or application start...

    # Check Docker login status
    check_docker_login

    # Check Docker network connectivity
    check_network_connectivity

    # Start the application after checks
    start_application
    ;;

  --start-database)
    start_database
    ;;

  *)
    echo "Usage: $0 {--purge-folders-and-cache|--purge-cache|--purge-folders|--run-migrations|--clean-database|--rebuild-database|--first-data-charge|--start-application}"
    exit 1
    ;;
esac
