# Use an OpenJDK image as the base
FROM openjdk:17-jdk-slim

# Install netcat (nc) for the entrypoint script
RUN apt-get update && apt-get install -y netcat && apt-get clean

# Set a working directory in the container
WORKDIR /app

# Copy the JAR file from your build context to the container
COPY build/libs/transaction-authorizer-0.0.1-SNAPSHOT.jar application.jar

# Expose the port your app runs on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "application.jar"]