# Gemini CLI Instruction Guide - back-office-service

This document provides essential context and instructions for AI agents working on the **back-office-service**.

## Project Overview
The `back-office-service` is a core microservice for an e-commerce platform's back-office operations. It manages the product catalog, category hierarchy, user/employee management, and order tracking.

- **Primary Role:** Product and Category master data management.
- **Port:** 9100
- **Database:** PostgreSQL (`back_office_db`)
- **Messaging:** Kafka (Producer for catalog updates, Consumer for orders)
- **Storage:** Cloudflare R2 (via AWS S3 SDK) for images and avatars.

## Tech Stack
- **Language:** Java 25
- **Framework:** Spring Boot 4.0.2 (Spring Data JPA, Spring Security, Spring WebMVC)
- **Messaging:** Spring Kafka
- **Security:** JWT-based (Portable Identity)
- **Lombok:** Extensively used for boilerplate reduction.
- **Build System:** Maven (using `./mvnw` wrapper)

## Core Architecture & Patterns

### 1. Layered Architecture
Standard flow: `Controller` -> `Service (Interface/Impl)` -> `Repository (Spring Data JPA)` -> `DAO (Entities)`.
- **Controllers:** Return `ResponseEntity<ApiResponse<T>>`.
- **Services:** Implement business logic, validation, and Kafka event publishing.
- **DTOs:** Separate request and response objects in `edu.hcmut.datn.back_office_service.dto`.

### 2. Category Hierarchy (3-Layer)
- **Level 1 (Category):** `isSubCategory = "N"`
- **Level 2 (Subcategory):** `isSubCategory = "Y"`, links to parent via `belongToCategory`.
- **Level 3 (SubSubcategory):** Separate entity/table, links to Level 2 via `subcategoryId`.

### 3. Event-Driven Architecture (Kafka)
- **Producers:** Publish events (e.g., `CategoryCreatedEvent`, `ProductGeneralCreatedEvent`) to topics after DB persistence.
- **Consumers:** Listen for events (e.g., `OrderCreatedEvent`) from other services.
- **Convention:** Events are implemented as Java `record` types in `messaging` sub-packages.

### 4. File Storage (Cloudflare R2)
- Managed via `R2UploadService`.
- Images are uploaded to R2, and public URLs are stored in the database.
- Configuration includes bucket names and public URL prefixes in `application.yaml`.

## Development Guidelines

### Coding Standards
- **Lombok:** Use `@RequiredArgsConstructor` for constructor injection. Use `@Getter` and `@Setter` at field level in Entities (DAO).
- **Entities:** All entities must include `createdAt` and `updatedAt` with `@PrePersist` and `@PreUpdate` hooks.
- **API Response:** Always wrap responses in the `ApiResponse<T>` record found in `dto.response`.
- **Exceptions:** Use custom exceptions organized by entity package in the `exception/` directory.

### Build & Run Commands
```bash
# Build and install (skip tests)
./mvnw clean install -DskipTests

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a specific test
./mvnw test -Dtest=CategoryServiceTest
```

### Environment Variables
Ensure a `.env` file exists at the root (use `.env.example` as a template):
- `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`
- `KAFKA_HOST`, `KAFKA_PORT`
- `R2_ACCOUNT_ID`, `R2_ACCESS_KEY`, `R2_SECRET_KEY`

## Project Structure
- `common/`: Shared enums (e.g., `OrderStatus`, `CategoryStatus`).
- `config/`: Spring configurations (CORS, R2, Web).
- `dao/`: JPA entities.
- `dto/`: Request/Response data transfer objects.
- `messaging/`: Kafka producers and consumers.
- `repository/`: Spring Data JPA interfaces.
- `security/`: JWT filter and security configuration.
- `service/`: Business logic interfaces and implementations (`impl/`).
- `db_scheme/`: Database ERD and SQL backups.
