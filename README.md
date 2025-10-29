# Spring Boot Clean Architecture Boilerplate
#### 🇧🇷 [Documentação em Português](README_pt_br.md)

This project is a modular Spring Boot boilerplate structured around Clean Architecture principles. It provides a foundation for building maintainable services with clear separation between the domain, application, and infrastructure layers. The repository includes a complete JWT-based authentication implementation, MySQL integration with Flyway migrations, and automated testing setup.

## Table of Contents
- [Project Structure](#project-structure)
- [Architectural Overview](#architectural-overview)
  - [Domain Layer](#domain-layer)
  - [Application Layer](#application-layer)
  - [Infrastructure Layer](#infrastructure-layer)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Clone the Repository](#clone-the-repository)
- [Running the Application](#running-the-application)
  - [1. Start MySQL with Docker](#1-start-mysql-with-docker)
  - [2. Build the Project](#2-build-the-project)
  - [3. Run via Executable Jar](#3-run-via-executable-jar)
  - [4. Alternative: Run with Gradle](#4-alternative-run-with-gradle)
- [Running Tests](#running-tests)
- [API Authentication](#api-authentication)
- [Troubleshooting](#troubleshooting)
- [Useful Commands](#useful-commands)
- [Contributing](#contributing)

## Project Structure

```
.
├── application/              # Application use cases and services
├── domain/                   # Enterprise business rules and models
├── infrastructure/           # Frameworks, adapters, external integrations
├── docs/                     # Supplementary documentation
├── build.gradle.kts          # Gradle build configuration (root project)
├── docker-compose.yml        # Local MySQL setup
└── README.md                 # Project documentation
```

## Architectural Overview

The codebase follows Clean Architecture to keep business rules independent from frameworks and drivers. Each module has its own Gradle build file and can be developed, tested, and packaged in isolation.

### Domain Layer

dir: `domain/`

- Contains core business entities, value objects, and gateway interfaces.
- Has no dependencies on Spring or other frameworks.
- Defines contracts (`Gateway` interfaces) that the infrastructure layer implements.

### Application Layer

dir: `application/`

- Implements use cases orchestrating domain logic.
- Depends only on the domain module.
- Contains input/output ports, DTOs, and service facades.

### Infrastructure Layer

dir: `infrastructure/`

- Implements adapters for persistence (MySQL via Spring Data JPA), web controllers, security, and external services.
- Provides concrete `Gateway` implementations that satisfy domain contracts.
- Hosts Spring Boot configuration files, Flyway migrations, API controllers, and security setup.

### Authentication Design

Full details about the JWT-based authentication flow, involved components, and testing strategy can be found in the dedicated documentation:

- [Authentication Implementation](docs/AUTHENTICATION_IMPLEMENTATION.md)

## Getting Started

### Prerequisites
- Java 22+
- Gradle (wrapper included)
- Docker and Docker Compose
- Git

### Clone the Repository

```bash
git clone https://github.com/Lucassamuel97/springboot-clean-architecture-boilerplate.git
cd springboot-clean-architecture-boilerplate
```

## Running the Application

### Option 1: Docker (Recommended)

The easiest way to run the entire stack (application + MySQL) is using Docker Compose:

```bash
# Build and start all services
DOCKER_BUILDKIT=1 docker compose up --build

# Or run in detached mode
DOCKER_BUILDKIT=1 docker compose up --build -d

# View logs
docker compose logs -f app

# Stop services
docker compose down

# Stop services and remove volumes (clean database)
docker compose down -v
```

The application will be available at `http://localhost:8080` and MySQL at `localhost:3307`.

**Note**: The Docker setup uses multi-stage builds to create an optimized production image with Java 22 JRE Alpine.

### Option 2: Local Development

#### 1. Start MySQL with Docker

```bash
docker compose up -d mysql
```

This command starts only the MySQL 8 instance exposed on port `3307`. Data persists in a local Docker volume named `mysql_data`.

#### 2. Build the Project

```bash
./gradlew clean build
```

The build runs unit and integration tests, compiles all modules, and produces the executable jar under `infrastructure/build/libs/`.

#### 3. Run via Executable Jar

```bash
java -jar infrastructure/build/libs/infrastructure-0.0.1-SNAPSHOT.jar
```

By default the application listens on `http://localhost:8080`. Update `application.yml` or provide environment variables to customize database URL, credentials, and JWT settings.

#### 4. Alternative: Run with Gradle

```bash
./gradlew :infrastructure:bootRun
```

This command is useful during development because it enables Spring DevTools for hot reload.

## Running Tests

The project contains unit, integration, and web layer tests with specific test profiles. Execute the entire suite with:

```bash
./gradlew test
```

To focus on infrastructure module tests only:

```bash
./gradlew :infrastructure:test
```

Test reports can be found under `build/reports/tests/` for the root project or within each module's build directory.

## API Authentication

The application exposes login endpoints that return JWT tokens and protects business endpoints using role-based access control. Refer to the detailed guide for usage examples, Swagger tips, and security recommendations:

- [Authentication Implementation](docs/AUTHENTICATION_IMPLEMENTATION.md)

## Troubleshooting

- **Database connection errors**: Ensure Docker container `mysql-clean-architecture` is running and accessible at `localhost:3307`.
- **Failed Flyway migrations**: Check the Flyway scripts in `infrastructure/src/main/resources/db/migration` and verify the database is clean.
- **JWT validation issues**: Confirm the `app.jwt.secret` value matches between token generation and validation environments.
- **Port conflicts**: The API uses port `8080` by default. Override with `SERVER_PORT` or `--server.port=9090` when running the jar.

## Useful Commands

| Task | Command |
|------|---------|
| Start all services (Docker) | `DOCKER_BUILDKIT=1 docker compose up --build -d` |
| View application logs | `docker compose logs -f app` |
| Stop all services | `docker compose down` |
| Stop and remove volumes | `docker compose down -v` |
| Start MySQL only | `docker compose up -d mysql` |
| Stop MySQL | `docker compose down` |
| Build all modules | `./gradlew clean build` |
| Create boot jar | `./gradlew :infrastructure:bootJar` |
| Run via jar (local) | `java -jar infrastructure/build/libs/infrastructure-0.0.1-SNAPSHOT.jar` |
| Run with Gradle | `./gradlew :infrastructure:bootRun` |
| Run tests | `./gradlew test` |

## Contributing

1. Fork this repository.
2. Create a feature branch: `git checkout -b feat/my-feature`.
3. Commit your changes following conventional commits.
4. Ensure tests pass: `./gradlew clean test`.
5. Open a pull request describing your change and any relevant context.

Feel free to suggest improvements or report issues. Enjoy building with Clean Architecture! 🚀
