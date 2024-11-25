

# Swagger Usage Guide

This guide provides instructions on how to use the Swagger UI to interact with the API and how to export the API documentation to Postman for further testing.

## Using Swagger UI

Swagger UI provides a visual interface for interacting with the API. To access it:

1. Ensure the application is running locally (either via the provided script or manually).
2. Open a web browser and go to:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. You will see the interactive API documentation, where you can:
    - **Explore endpoints**: Browse through the different available API endpoints.
    - **Test the API**: Use the **Try it out** feature to execute different HTTP methods (GET, POST, etc.) and view the responses.
    - **View Models and Status Codes**: Swagger will show you the expected responses, including HTTP status codes and data models.

## Exporting to Postman

To export the API documentation to Postman:

1. Access the OpenAPI JSON definition:
   ```
   http://localhost:8080/v3/api-docs
   ```

2. **Import into Postman**:
    - Open Postman and click the **Import** button.
    - Select the **Link** tab and paste the URL of the OpenAPI definition (`http://localhost:8080/v3/api-docs`).
    - Click **Continue**.

3. Postman will generate a collection of requests based on the OpenAPI specification, which you can use to send requests to the API and inspect responses.

This provides an easy way to automate testing and explore the API in Postman.
