# Lumina Agent

Lumina Agent is a Java-based application built with Spring Boot, designed for retrieval-augmented generation (RAG) tasks. It integrates with a vector database (PostgreSQL with pgvector) for efficient similarity searches and uses Zipkin for distributed tracing.

## Features

- Vector-based document ingestion and retrieval
- Distributed tracing with Zipkin
- RESTful API for interacting with the agent

## Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- Docker and Docker Compose

## Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd lumina-agent
   ```

2. Start the required services using Docker Compose:
   ```bash
   docker-compose up -d
   ```
   This will start:
   - PostgreSQL with pgvector extension on port 5432
   - Zipkin on port 9411

3. Build the application:
   ```bash
   ./gradlew build
   ```

## Running the Application

Run the application using Gradle:
```bash
./gradlew bootRun
```

The application will start on the default port (usually 8080).

## Usage

Once running, you can interact with the API endpoints. Refer to the application logs or documentation for specific endpoints.

## Configuration

Configuration is managed via `src/main/resources/application.yml`. Update database connection details if necessary.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is licensed under the MIT License.
