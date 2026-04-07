# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Service Overview

**back-office-service** is a Spring Boot microservice managing product catalog, category hierarchy, and back-office operations for the e-commerce platform. Runs on port 9100 with PostgreSQL (back_office_db) and publishes events to Kafka.

**Stack**: Spring Boot 4.0.2, Java 25, Spring Security (JWT), Spring Data JPA, Kafka, AWS SDK S3 (Cloudflare R2)

## Build & Run Commands

**Development with Maven Wrapper** (preferred - no global Maven needed):
```bash
# Build (skip tests)
./mvnw clean install -DskipTests

# Build with tests
./mvnw clean install

# Run tests only
./mvnw test

# Run specific test class
./mvnw test -Dtest=CategoryServiceTest

# Run application locally
./mvnw spring-boot:run

# Package JAR
./mvnw package -DskipTests
```

**Docker Build**:
```bash
# Build locally then containerize (faster, avoids Docker network issues)
./mvnw clean package -DskipTests
docker build -f Dockerfile.local -t back-office-service .

# OR build inside container
docker build -t back-office-service .
```

**Using parent repository scripts** (from `/whole-system/`):
```bash
# Rebuild and restart after code changes
./service.sh rebuild back-office

# View logs
./service.sh logs back-office

# Restart without rebuilding
./service.sh restart back-office
```

## Architecture

### Package Structure

```
edu.hcmut.datn.back_office_service/
├── common/           # Enums, constants
├── config/           # R2Config, WebConfig (CORS)
├── controller/       # REST endpoints (@RestController)
├── dao/              # JPA entities (@Entity)
├── dto/              # Request/Response DTOs
│   ├── request/
│   └── response/
├── exception/        # Custom exceptions per entity
├── messaging/        # Kafka producers/consumers
│   ├── category/     # CategoryProducer, events
│   ├── productgeneral/ # ProductGeneralProducer
│   ├── order/        # OrderCreatedConsumer
│   └── user/
├── repository/       # Spring Data JPA repositories
├── security/         # JWT authentication
│   └── portable/     # JwtAuthenticationFilter, SecurityConfig
├── service/          # Business logic interfaces
│   ├── impl/         # Service implementations
│   └── scheduler/    # DynamicSchedulerService
└── util/             # Utilities
```

### Layered Architecture Pattern

Standard flow: **Controller → Service → Repository → Database**

- **Controllers**: Handle HTTP requests, return `ApiResponse<T>` wrapper
- **Services**: Business logic, validation, Kafka event publishing
  - Interfaces in `service/`, implementations in `service/impl/`
  - Use `@RequiredArgsConstructor` (Lombok) for constructor injection
  - Methods: `create()`, `read()`, `readAll()`, `update()`, `delete()`
- **Repositories**: Spring Data JPA, extend `JpaRepository<Entity, Long>`
- **DAO (Entities)**: JPA entities with `@PrePersist`/`@PreUpdate` for timestamps

**Service Implementation Example**:
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryProducer categoryProducer;
    
    @Override
    @Transactional
    public Category create(Category category) {
        Category saved = categoryRepository.save(category);
        categoryProducer.publishCategoryCreated(event);
        return saved;
    }
}
```

## Category System Architecture

**3-Layer Hierarchy**: Category → Subcategory → SubSubcategory

- **Category**: Main categories AND subcategories (same table)
  - `isSubCategory = "N"` → Main category
  - `isSubCategory = "Y"` → Subcategory (has `belongToCategory` parent ID)
- **SubSubcategory**: Finest granularity (separate table)
  - References `subcategoryId` (Category where `isSubCategory = "Y"`)

**Example Hierarchy**:
```
Electronics (Category, isSubCategory="N")
  └─ Mobile Phones (Category, isSubCategory="Y", belongToCategory=Electronics.id)
      └─ Smartphones (SubSubcategory, subcategoryId=MobilePhones.id)
      └─ Feature Phones (SubSubcategory)
```

**Key Services**:
- `CategoryService`: Manages both categories and subcategories
  - `create(Category)` - validates parent if subcategory
  - `readSubcategories(parentId)` - get subcategories of a category
  - `createSubSubcategory(SubSubcategory)` - creates sub-subcategory
  - `readAllSubSubcategories(subcategoryId)` - get sub-subcategories

**Kafka Events Published**:
- `category-events` → `CategoryCreatedEvent` (for categories and subcategories)
- `subsubcategory-events` → `SubSubcategoryCreatedEvent`
- Consumed by: product_storage_service

## Event-Driven Architecture

### Kafka Producers

**Topics Published**:
- `category-events` - Category/Subcategory creation
- `subsubcategory-events` - SubSubcategory creation  
- `product-general-events` - ProductGeneral creation/updates

**Producer Pattern**:
```java
@Service
@RequiredArgsConstructor
public class ProductGeneralProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishProductGeneralCreated(ProductGeneralCreatedEvent event) {
        kafkaTemplate.send("product-general-events", event.prodGenId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event", ex);
                } else {
                    log.info("Event sent with offset {}", result.getRecordMetadata().offset());
                }
            });
    }
}
```

**Events use Java Records**:
```java
public record ProductGeneralCreatedEvent(
    Long prodGenId,
    String name,
    String description,
    // ... fields
) {}
```

### Kafka Consumers

**Topics Consumed**:
- `order-item-events` → `OrderCreatedConsumer` (from ecommerce-service)

**Consumer Pattern**:
```java
@Service
@Slf4j
public class OrderCreatedConsumer {
    @KafkaListener(topics = "order-item-events", groupId = "back-office-group")
    public void consume(OrderCreatedEvent event) {
        log.info("Received order event: {}", event);
        // Process event
    }
}
```

**Kafka Configuration**:
- Bootstrap servers: `${KAFKA_HOST}:${KAFKA_PORT}` (from .env)
- `KafkaProducerConfig` and `KafkaConsumerConfig` in `messaging/`

## Security Architecture

**JWT-based Authentication** (portable from identity-service):

- `JwtAuthenticationFilter` - Validates JWT tokens from `Authorization: Bearer <token>` header
- `JwtTokenValidator` - Parses and validates JWT claims
- `SecurityConfig` - Configures Spring Security filter chain
- `AuthenticatedUser` - Holds current user context
- `CustomAccessDeniedHandler` - Handles 403 Forbidden
- `CustomAuthenticationEntryPoint` - Handles 401 Unauthorized

**Security is DISABLED by default in development** - check `SecurityConfig` for current state.

**Protected Endpoints**: Most `/api/**` endpoints require JWT token.

## Cloudflare R2 Integration

**File Storage Service**: `R2UploadService` uses AWS S3 SDK to interact with Cloudflare R2.

**Buckets**:
- `back-office-user-avts` - User avatars
- `product-general-img` - Product images

**Configuration** (application.yaml):
```yaml
cloudflare:
  r2:
    account-id: ${R2_ACCOUNT_ID}
    endpoint: https://${R2_ACCOUNT_ID}.r2.cloudflarestorage.com
    access-key: ${R2_ACCESS_KEY}
    secret-key: ${R2_SECRET_KEY}
    region: auto

app:
  user-avatar-bucket: back-office-user-avts
  product-general-img-bucket: product-general-img
  user-avatar-public-bucket-url: https://pub-954e99f131cf4cc896de1ad360338682.r2.dev
  product-general-image-public-bucket-url: https://pub-0365edd1781141cdb68675969c7cdb87.r2.dev
```

**R2Config** creates S3 client bean using R2 endpoint.

**Service Methods**:
- `upload(MultipartFile file, String bucket)` → Returns public URL
- `delete(String key, String bucket)` → Deletes file

**Usage in Services**: Upload user avatars and product images, store public URL in database.

## Entity Conventions

**All entities follow these patterns**:

1. **Auto-managed timestamps**:
```java
@Column(name = "created_at")
private LocalDateTime createdAt;

@Column(name = "updated_at")
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

2. **ID Generation**: `@GeneratedValue(strategy = GenerationType.IDENTITY)` for auto-increment
   - **Exception**: `ProductGeneral` uses manually assigned IDs (passed from service)

3. **Lombok Annotations**: Selective `@Getter`/`@Setter` on fields (NOT class-level)

4. **Table Naming**: Snake_case plural (`categories`, `product_generals`, `sub_subcategories`)

**Key Entities**:
- `Category` - Main categories and subcategories
- `SubSubcategory` - Finest category level
- `ProductGeneral` - Product catalog (linked to SubSubcategory)
- `User`, `Employee`, `Buyer` - User management
- `Order`, `OrderItem` - Order tracking
- `Event`, `SaleEvent` - Promotional events
- `Provider`, `EnterpriseStore` - Supplier management
- `PaymentMethod`, `CouponPolicy`, `PreorderPolicy` - Business rules
- `DemandResponse`, `ProductRequest` - Product requests

## Database Schema

**Database**: `back_office_db` (PostgreSQL 17)

**Schema Documentation**: See `db_scheme/README.md` for backup/restore commands.

**JPA Configuration**:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Auto-creates/updates schema
    show-sql: false     # Set to true for debugging
```

**Connection** (from .env):
```
jdbc:postgresql://${DB_HOST}:${DB_PORT}/back_office_db
```

## Scheduler Service

**DynamicSchedulerService**: Allows runtime scheduling of tasks (see `service/scheduler/`).

**SchedulerConfig**: Enables Spring's `@EnableScheduling` for cron jobs.

**Usage**: Schedule periodic tasks dynamically without redeploying.

## Exception Handling

**Custom Exceptions** organized by entity in `exception/`:
- `exception/category/` - Category-related exceptions
- `exception/productrequest/` - ProductRequest exceptions  
- `exception/user/` - User exceptions
- `exception/r2service/` - R2 upload exceptions
- etc.

**Naming Convention**: `{Entity}NotFoundException`, `{Entity}AlreadyExistsException`

**Controller Error Handling**:
```java
try {
    // service call
} catch (Exception e) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
}
```

**ApiResponse Wrapper**:
```java
public record ApiResponse<T>(String status, String message, T data) {
    public static <T> ApiResponse<T> SUCCESS(String status, String message, T data) { ... }
    public static <T> ApiResponse<T> ERROR(String status, String message, T data) { ... }
}
```

## Environment Variables

**Required in .env**:
```env
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
KAFKA_HOST=192.168.96.110
KAFKA_PORT=9092
R2_ACCOUNT_ID=youraccountid
R2_ACCESS_KEY=yourr2accesskey
R2_SECRET_KEY=yourr2privatekey
USER_AVATAR_PUBLIC_BUCKET_URL=user_avatar_public_bucket_url
```

**Copy template**: `cp .env.example .env` and fill in values.

## Testing

**Run all tests**:
```bash
./mvnw test
```

**Run specific test**:
```bash
./mvnw test -Dtest=CategoryServiceTest
```

**Test Framework**: Spring Boot Test with JPA and WebMVC testing support.

**Test Directory**: `src/test/java/edu/hcmut/datn/back_office_service/`

## Service Dependencies

**This service is a Kafka PRODUCER** - publishes events consumed by:
- **product_storage_service** - consumes `category-events`, `subsubcategory-events`, `product-general-events`

**This service is a Kafka CONSUMER** - consumes events from:
- **ecommerce-service** - consumes `order-item-events` (order tracking)

**No direct HTTP dependencies** on other services (event-driven architecture).

## Development Workflow

1. **Start infrastructure** (from `/whole-system/`):
   ```bash
   # Start PostgreSQL + Kafka
   cd infrastructure/database && ./setup.sh
   cd infrastructure/kafka && docker-compose up -d
   ```

2. **Configure environment**:
   ```bash
   cp .env.example .env
   # Edit .env with your credentials
   ```

3. **Run service**:
   ```bash
   ./mvnw spring-boot:run
   # OR
   ./service.sh rebuild back-office  # From parent repo
   ```

4. **Verify startup**:
   - Service: http://localhost:9100
   - Kafka UI: http://localhost:9280 (check topics)
   - Database: `psql -U <user> -d back_office_db -h localhost -p 5432`

5. **Make changes**:
   - Edit code
   - Rebuild: `./service.sh rebuild back-office` (fastest)
   - OR: `./mvnw clean install -DskipTests && docker restart back-office-service`

## Key Business Logic

### Category Hierarchy Validation
- When creating subcategory (`isSubCategory="Y"`), validates parent category exists
- When creating SubSubcategory, validates subcategory exists
- Cascading deletes NOT enabled - must delete in order: ProductGeneral → SubSubcategory → Subcategory → Category

### Product General Management
- ProductGeneral entities use manually assigned IDs (not auto-increment)
- Linked to SubSubcategory (finest granularity)
- Images stored in R2, public URLs in database
- Publishes `product-general-events` when created/updated

### Event Publishing Pattern
- All create operations publish Kafka events after successful database save
- Uses `@Transactional` to ensure atomicity
- Events published asynchronously with callback logging
- Failures logged but don't roll back transaction

### File Upload Pattern
- Multipart files accepted via controllers
- Uploaded to R2 via `R2UploadService`
- Public URL returned and stored in entity
- Old files deleted when entity updated/deleted

## Troubleshooting

**Port 9100 already in use**:
```bash
# Find process
lsof -i :9100
# Kill process
kill -9 <PID>
```

**Database connection refused**:
- Verify PostgreSQL is running: `docker ps | grep postgres`
- Check .env credentials match database setup
- Test connection: `psql -U <user> -d back_office_db -h localhost -p 5432`

**Kafka events not received by consumers**:
- Check Kafka UI (localhost:9280) - verify topic exists and has messages
- Check consumer group ID and bootstrap servers
- Verify event serialization/deserialization (JSON format)

**R2 upload fails**:
- Verify R2 credentials in .env
- Check bucket exists in Cloudflare R2 dashboard
- Verify file size under 10MB limit (configurable in application.yaml)

**Build fails**:
- Ensure Java 25 installed: `java -version`
- Clear Maven cache: `./mvnw clean`
- Check Lombok annotation processing in IDE settings
