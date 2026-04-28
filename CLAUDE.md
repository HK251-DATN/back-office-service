# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Service Overview

**back-office-service** is a Spring Boot microservice managing product catalog, category hierarchy, order processing, employee management, and back-office operations for the e-commerce platform. Runs on port 9100 with PostgreSQL (back_office_db), publishes and consumes Kafka events, and integrates with Cloudflare R2 for file storage.

**Stack**: Spring Boot 4.0.2, Java 25, Spring Security (JWT), Spring Data JPA, Kafka, AWS SDK S3 (Cloudflare R2)

**Core Responsibilities**:
- Product catalog and 3-layer category hierarchy management
- Order processing workflow (confirm → package → ship → deliver)
- Employee management for order fulfillment
- User and buyer synchronization from identity-service
- Business rules (payment methods, coupons, preorders, sale events)
- Supplier/provider management
- Vietnamese fresh food sample data seeding

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
│   └── enums/        # Unit, OrderStatus, Gender, EmployeeStatus, PaymentType, etc.
├── config/           # R2Config, WebConfig (CORS), DataSeeder
├── controller/       # 13 REST controllers (@RestController)
│   ├── CategoryController           # Category/SubSubcategory CRUD
│   ├── ProductGeneralController     # Product catalog
│   ├── OrderController              # Order management & employee tasks
│   ├── EmployeeController           # Employee CRUD
│   ├── UserController, BuyerController  # User management
│   ├── EventController, SaleEventController  # Event management
│   ├── PaymentMethodController, CouponPolicyController, PreorderPolicyController
│   ├── ProviderController, EnterpriseStoreController  # Supplier management
│   └── DemandResponseController     # Product requests
├── dao/              # 17 JPA entities (@Entity)
│   ├── Category, SubSubcategory, ProductGeneral
│   ├── Order, OrderItem             # Order processing
│   ├── Employee                     # Order fulfillment staff
│   ├── User, Buyer                  # User management
│   ├── Event, SaleEvent             # Promotional events
│   ├── PaymentMethod, CouponPolicy, PreorderPolicy
│   ├── Provider, EnterpriseStore    # Suppliers
│   ├── ProductRequest, DemandResponse
│   └── ...
├── dto/              # Request/Response DTOs
│   ├── request/
│   │   ├── event/    # Event-related DTOs
│   │   ├── CategoryCreateRequest, SubSubcategoryCreateRequest
│   │   ├── OrderCreateRequest, OrderUpdateRequest
│   │   └── PackagingProgressUpdateRequest
│   └── response/
│       └── ApiResponse<T>  # Standard wrapper
├── exception/        # Custom exceptions (14 entity-specific packages)
│   ├── category/, productgeneral/, order/, employee/, user/, buyer/
│   ├── saleevent/, event/, paymentmethod/, couponpolicy/, preorderpolicy/
│   ├── provider/, enterprisestore/, demandresponse/, productrequest/
│   └── r2service/    # R2 upload exceptions
├── messaging/        # Kafka producers (3) & consumers (4)
│   ├── category/     # CategoryProducer (2 events)
│   ├── productgeneral/ # ProductGeneralProducer
│   ├── order/        # OrderEventProducer, 3 consumers
│   └── user/         # UserCreatedConsumer
├── repository/       # Spring Data JPA repositories
│   └── projection/   # OrderInformation, BuyerUserProjection
├── security/         # JWT authentication (portable from identity-service)
│   └── portable/     # JwtAuthenticationFilter, SecurityConfig, AuthenticatedUser, etc.
├── service/          # 17 business logic services
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
  - `isSubCategory = "N"` → Main category (e.g., "Thịt & Hải Sản")
  - `isSubCategory = "Y"` → Subcategory (has `belongToCategory` parent ID, e.g., "Gia Cầm")
- **SubSubcategory**: Finest granularity (separate table)
  - References `subcategoryId` (Category where `isSubCategory = "Y"`)
  - Contains `avgShelfDays` - average shelf life in days for products in this category (important for inventory/expiry tracking)

**Example Hierarchy**:
```
Electronics (Category, isSubCategory="N")
  └─ Mobile Phones (Category, isSubCategory="Y", belongToCategory=Electronics.id)
      └─ Smartphones (SubSubcategory, subcategoryId=MobilePhones.id)
      └─ Feature Phones (SubSubcategory)
```

**Key Services**:
- `CategoryService`: Manages both categories and subcategories
  - `create(Category)` - validates parent if subcategory, publishes `CategoryCreatedEvent`
  - `read(Long categoryId)` - get category by ID
  - `readAll()` - get all main categories (isSubCategory="N")
  - `readSubcategories(parentId)` - get subcategories of a category
  - `update(Long categoryId, Category)` - partial update (null fields ignored)
  - `delete(Long categoryId)` - delete category
  - `createSubSubcategory(SubSubcategory)` - creates sub-subcategory, validates parent is subcategory, publishes `SubSubcategoryCreatedEvent`
  - `readSubSubcategory(Long subSubcategoryId)` - get sub-subcategory by ID
  - `readAllSubSubcategories(subcategoryId)` - get sub-subcategories for a subcategory
  - `readAllSubSubcategories_v2()` - get all sub-subcategories
  - `updateSubSubcategory(Long subSubcategoryId, SubSubcategory)` - partial update
  - `deleteSubSubcategory(Long subSubcategoryId)` - delete sub-subcategory

**CategoryController Endpoints**:
- `POST /api/categories` - Create category/subcategory
- `GET /api/categories` - Get all main categories
- `GET /api/categories/{categoryId}` - Get category by ID
- `GET /api/categories/{parentId}/subcategories` - Get subcategories
- `PUT /api/categories/{categoryId}` - Update category
- `DELETE /api/categories/{categoryId}` - Delete category
- `POST /api/categories/subsubcategories` - Create sub-subcategory
- `GET /api/categories/subsubcategories` - Get all sub-subcategories
- `GET /api/categories/subsubcategories/{subSubcategoryId}` - Get sub-subcategory by ID
- `GET /api/categories/{subcategoryId}/subsubcategories` - Get sub-subcategories for subcategory
- `PUT /api/categories/subsubcategories/{subSubcategoryId}` - Update sub-subcategory
- `DELETE /api/categories/subsubcategories/{subSubcategoryId}` - Delete sub-subcategory

**Kafka Events Published**:
- `category-events` → `CategoryCreatedEvent` (for both main categories and subcategories)
- `subsubcategory-events` → `SubSubcategoryCreatedEvent` (includes avgShelfDays field)
- Consumed by: product_storage_service

## Event-Driven Architecture

### Kafka Producers (3 topics published)

**Topics Published**:
- `category-events` - Category/Subcategory creation (consumed by product_storage_service)
- `subsubcategory-events` - SubSubcategory creation (consumed by product_storage_service)
- `product-general-events` - ProductGeneral creation/updates (consumed by product_storage_service)

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

public record CategoryCreatedEvent(
    Long categoryId,
    String name,
    String description,
    Integer displayOrder,
    String iconUrl,
    String isSubCategory,
    Long belongToCategory
) {}

public record SubSubcategoryCreatedEvent(
    Long subSubcategoryId,
    String name,
    String description,
    String iconUrl,
    Long subcategoryId,
    Integer avgShelfDays  // NEW: Average shelf life in days
) {}
```

### Kafka Consumers (4 topics consumed)

**Topics Consumed**:
- `user-events` → `UserCreatedConsumer` (from identity-service)
  - Syncs users and creates corresponding Buyer records
- `order-events` → `OrderCreatedConsumer` (from ecommerce-service)
  - Creates Order records for back-office tracking
- `order-confirmed-events` → `OrderConfirmedConsumer` (from ecommerce-service)
  - Updates order status to CONFIRMED with confirmedBy employee
- `order-packaging-progress-update-events` → `OrderUpdatePackagingProgressEventConsumer` (from ecommerce-service)
  - Updates packaging progress percentage (0-100%)

**Consumer Pattern**:
```java
@Component
@Slf4j
@AllArgsConstructor
public class OrderCreatedConsumer {
    private final OrderService orderService;
    
    @KafkaListener(topics = "order-events")
    public void consume(OrderCreatedEvent event) {
        log.info("Received event: {}", event);
        try {
            Order newOrder = event.toOrderEntity();
            orderService.create(newOrder);
            log.info("Create order {} success", event.getOrderId());
        } catch (Exception e) {
            log.error("Create order {} fail due to: {}", event.getOrderId(), e.getMessage());
        }
    }
}
```

**Kafka Configuration**:
- Bootstrap servers: `${KAFKA_HOST}:${KAFKA_PORT}` (from .env)
- Consumer group: `back-office-group`
- `KafkaProducerConfig` and `KafkaConsumerConfig` in `messaging/`

## Order Management System

**Full order processing workflow** with employee assignments and progress tracking.

### Order Entity Fields

```java
@Entity
@Table(name = "orders")
public class Order {
    private Long orderId;              // From ecommerce-service
    private OrderStatus status;        // PENDING → CONFIRMED → PACKING → SHIPPING → DELIVERED
    private Long ownedBy;              // Buyer ID
    private Long confirmedBy;          // Employee who confirmed order
    private Long packagedBy;           // Employee who packaged order
    private Long shippedBy;            // Employee who shipped order
    private Long totalPrice;           // Total order price
    private Integer packagingProgress; // 0-100% completion
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Order Status Flow

```
PENDING (created from ecommerce-service)
  ↓ /confirm (employee confirms order)
CONFIRMED
  ↓ /package (employee starts packaging)
PACKING
  ↓ /progress (employee updates packaging progress 0-100%)
PACKING (100% complete)
  ↓ /ship (employee marks as shipped)
SHIPPING
  ↓ /deliver (employee marks as delivered)
DELIVERED
```

### OrderController Endpoints

**Admin Order Management**:
- `GET /api/order/admin` - Get all orders, filter by status
- `GET /api/order/admin/{orderId}` - Get specific order with buyer info (OrderInformation projection)
- `GET /api/order/{orderId}` - Get order by ID
- `POST /api/order` - Create order (usually via Kafka)
- `PUT /api/order/{orderId}` - Update order
- `DELETE /api/order/{orderId}` - Delete order

**Employee Order Processing**:
- `PUT /api/order/{orderId}/confirm` - Employee confirms order (sets confirmedBy, status=CONFIRMED)
- `PUT /api/order/{orderId}/package` - Start packaging (sets packagedBy, status=PACKING)
- `PUT /api/order/{orderId}/progress` - Update packaging progress (0-100%)
- `PUT /api/order/{orderId}/ship` - Mark as shipped (sets shippedBy, status=SHIPPING)
- `PUT /api/order/{orderId}/deliver` - Mark as delivered (status=DELIVERED)

**Employee Task Views**:
- `GET /api/order/emp/packaging-tasks` - Get orders assigned to current employee (status=PACKING)
- `GET /api/order/emp/delivering-tasks` - Get orders for delivery (status=SHIPPING)

### OrderInformation Projection

Joins Order with User/Buyer data for admin views:
```java
public interface OrderInformation {
    String getOrderId();
    String getBuyerId();
    OrderStatus getOrderStatus();
    Long getTotalPrice();
    Integer getPackagingProgress();
    String getBuyerFName();
    String getBuyerLName();
    String getBuyerEmail();
    LocalDateTime getLastUpdate();
}
```

Used by `OrderRepository.adminReadAll()` for complex queries filtering by status, packagedBy, shippedBy, orderId.

### Order Service Methods

```java
public interface OrderService {
    Order create(Order order);
    Order read(Long orderId);
    List<OrderInformation> adminReadAll(String status, Long packagedBy, Long shippedBy, Long orderId);
    Order update(Long orderId, Order order);
    void delete(Long orderId);
    
    // Employee workflow methods
    void empConfirmOrder(Long orderId, Long employeeId);
    void empPackageOrder(Long orderId, Long employeeId);
    void empShipOrder(Long orderId, Long employeeId);
    void empDeliverOrder(Long orderId, Long employeeId);
    void updatePackagingProgress(Long orderId, Integer progress);
}
```

## Employee Management System

**Employee entity** for order fulfillment staff (warehouse, delivery).

### Employee Entity

```java
@Entity
@Table(name = "employees")
public class Employee {
    private Long empId;                // Employee ID
    private LocalDate hireDate;        // Date hired
    private Long userId;               // References User entity
    private EmployeeStatus empStatus;  // ACTIVE, INACTIVE, etc.
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### EmployeeController Endpoints

Standard CRUD operations:
- `POST /api/employee` - Create employee
- `GET /api/employee/{empId}` - Get employee by ID
- `GET /api/employee` - Get all employees
- `PUT /api/employee/{empId}` - Update employee
- `DELETE /api/employee/{empId}` - Delete employee

### Employee-Order Relationship

- Employees are assigned to orders via `confirmedBy`, `packagedBy`, `shippedBy` fields
- Employees can query their assigned tasks via employee task endpoints
- Employee ID comes from authenticated user context (`AuthenticatedUser.getId()`)

## Data Seeder

**Comprehensive Vietnamese fresh food sample data** seeded on first startup (when categories table is empty).

### Seeded Data Summary

**Category Hierarchy** (3 main categories):
1. **Thịt & Hải Sản** (Meat & Seafood)
   - Subcategories: Gia Cầm (Poultry), Thịt Đỏ (Red Meat), Hải Sản (Seafood)
   - Sub-subcategories: 15 types (Thịt Gà, Thịt Vịt, Thịt Bò, Tôm, Cá, Mực, etc.)

2. **Rau Củ Quả** (Vegetables & Fruits)
   - Subcategories: Rau Ăn Lá (Leafy Greens), Củ Quả (Root Vegetables), Trái Cây (Fruits), Rau Gia Vị (Herbs)
   - Sub-subcategories: 20 types (Rau Muống, Cải Xanh, Cà Rốt, Khoai Tây, Xoài, Chuối, etc.)

3. **Sữa & Trứng** (Dairy & Eggs)
   - Subcategories: Sữa Tươi (Fresh Milk), Sản Phẩm Từ Sữa (Dairy Products), Trứng (Eggs), Đồ Uống Từ Sữa (Milk Drinks)
   - Sub-subcategories: 25 types (Sữa Tươi Không Đường, Bơ, Phô Mai, Trứng Gà, Sữa Đậu Nành, etc.)

**Total Seeded**:
- **14 subcategories** (Category with isSubCategory="Y")
- **60 sub-subcategories** (with avgShelfDays: 1-180 days)
- **150+ product generals** (Vietnamese fresh food products)
  - Meat: Gà Ta, Ức Gà Phi Lê, Đùi Gà, Vịt, Ngan, Chim Cút, Bò, Heo, Dê, Cừu, Xúc Xích
  - Seafood: Tôm Sú, Cá Hồi, Mực, Cua Ghẹ, Nghêu Sò
  - Vegetables: Rau Muống, Cải Xanh, Xà Lách, Cà Rốt, Khoai Tây, Củ Cải, Bắp
  - Fruits: Xoài, Chuối, Dưa Hấu, Ổi, Thanh Long
  - Herbs: Hành Lá, Tỏi, Gừng, Ớt, Sả
  - Dairy: Sữa Tươi, Bơ, Phô Mai, Trứng, Sữa Chua
- **6 sample users** (1 admin + 5 buyers)
- **5 buyers** (linked to users 2-6)
- **3 payment methods** (MoMo, ZaloPay, COD)

### DataSeeder Implementation

```java
@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final CategoryRepository categoryRepository;
    private final SubSubcategoryRepository subSubcategoryRepository;
    private final ProductGeneralRepository productGeneralRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    
    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            // Only seed if database is empty
            if (categoryRepository.count() > 0) {
                log.info("Database already contains data. Skipping seeding.");
                return;
            }
            
            log.info("Starting database seeding...");
            // Creates 3 main categories, 14 subcategories, 60 sub-subcategories
            // Creates 150+ product generals with Vietnamese names
            // Creates 6 users, 5 buyers, 3 payment methods
            log.info("Database seeding completed successfully!");
        };
    }
}
```

**Key Features**:
- Runs automatically on first startup via `@Bean CommandLineRunner`
- Checks if database is empty before seeding (prevents duplicates)
- Creates realistic Vietnamese fresh food products with:
  - Vietnamese names and descriptions
  - Appropriate units (KILOGRAM, GRAM, LITER)
  - Realistic unit quantities (500g, 1kg, 200g, etc.)
  - Tags for search optimization
  - Default product image URLs
  - avgShelfDays for each sub-subcategory (1-180 days)

## Security Architecture

**JWT-based Authentication** (portable security package from identity-service):

**Components**:
- `JwtAuthenticationFilter` - Validates JWT tokens from `Authorization: Bearer <token>` header
- `JwtTokenValidator` - Parses and validates JWT claims (userId, email, roles)
- `SecurityConfig` - Configures Spring Security filter chain
- `AuthenticatedUser` - Holds current user context (accessible via `@AuthenticationPrincipal` or `SecurityContextHolder`)
- `CustomAccessDeniedHandler` - Handles 403 Forbidden responses
- `CustomAuthenticationEntryPoint` - Handles 401 Unauthorized responses

**Security Configuration**:
```java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()  // ALL endpoints require JWT
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

**Authentication Status**: **ENABLED** - All endpoints require valid JWT token except OPTIONS requests (CORS preflight).

**No Public Endpoints**: Unlike identity-service, this service has no public registration/login endpoints. All requests must include JWT token obtained from identity-service.

**Getting Current User**:
```java
// In controllers
@AuthenticationPrincipal AuthenticatedUser user
// Returns: user.getId(), user.getEmail(), user.getRoles()

// Or via SecurityContextHolder
AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
    .getAuthentication().getPrincipal();
Long userId = user.getId();
```

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

**Key Entities** (17 total):
- `Category` - Main categories and subcategories (isSubCategory field)
- `SubSubcategory` - Finest category level (includes avgShelfDays for inventory tracking)
- `ProductGeneral` - Product catalog (linked to SubSubcategory)
- `User` - User accounts (synced from identity-service via Kafka)
- `Employee` - Order fulfillment staff (confirmedBy, packagedBy, shippedBy)
- `Buyer` - Customer accounts (synced from identity-service)
- `Order` - Order tracking with status workflow (PENDING → CONFIRMED → PACKING → SHIPPING → DELIVERED)
- `OrderItem` - Individual items in an order
- `Event` - General events
- `SaleEvent` - Promotional sale events
- `Provider` - Suppliers/vendors
- `EnterpriseStore` - Enterprise retail stores
- `PaymentMethod` - Payment options (MoMo, ZaloPay, COD, etc.)
- `CouponPolicy` - Discount coupon rules
- `PreorderPolicy` - Pre-order rules
- `DemandResponse` - Responses to product requests
- `ProductRequest` - Customer product requests

**Recent Entity Changes**:
- `SubSubcategory`: Added `avgShelfDays` field (Integer) - average shelf life in days
- `Order`: Added `packagingProgress` field (Integer 0-100%) - packaging completion percentage
- `Employee`: New entity for order processing workflow

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

## Repository Projections

**Custom SQL projections** for complex queries joining multiple tables.

### OrderInformation Projection

Joins `orders`, `users`, and `buyers` tables for admin order management views:

```java
public interface OrderInformation {
    @JsonProperty("order_id") String getOrderId();
    @JsonProperty("owned_by") String getBuyerId();
    @JsonProperty("status") OrderStatus getOrderStatus();
    @JsonProperty("total_price") Long getTotalPrice();
    @JsonProperty("packaging_progress") Integer getPackagingProgress();
    @JsonProperty("f_name") String getBuyerFName();
    @JsonProperty("l_name") String getBuyerLName();
    @JsonProperty("email") String getBuyerEmail();
    @JsonProperty("updated_at") LocalDateTime getLastUpdate();
}
```

Used by `OrderRepository.adminReadAll(status, packagedBy, shippedBy, orderId)` with native query for filtering orders by:
- Order status (PENDING, CONFIRMED, PACKING, SHIPPING, DELIVERED)
- Assigned packaging employee
- Assigned shipping employee
- Specific order ID

### BuyerUserProjection

Joins `buyers` and `users` tables for buyer management:

```java
public interface BuyerUserProjection {
    // Buyer and User fields combined
}
```

**Why Use Projections?**
- Reduces data transfer (only fetches needed fields)
- Avoids N+1 query problems
- Native SQL queries for complex joins
- Type-safe with interface-based projections

## Environment Variables

**Required in .env**:
```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Kafka
KAFKA_HOST=192.168.96.110
KAFKA_PORT=9092

# Cloudflare R2 (Object Storage)
R2_ACCOUNT_ID=youraccountid
R2_ACCESS_KEY=yourr2accesskey
R2_SECRET_KEY=yourr2privatekey
```

**Public bucket URLs** (configured in application.yaml):
- User avatars: `https://pub-954e99f131cf4cc896de1ad360338682.r2.dev`
- Product images: `https://pub-0365edd1781141cdb68675969c7cdb87.r2.dev`

**Default URLs**:
- Default user avatar: `https://pub-954e99f131cf4cc896de1ad360338682.r2.dev/128c271e-c0a6-433e-bcd1-f3bbc4243401-default-user-avt.png`
- Default product image: `https://pub-0365edd1781141cdb68675969c7cdb87.r2.dev/5fe95e6c-c064-49c4-8712-1c8f54472502.jpg`

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

**This service is a Kafka PRODUCER** (3 topics) - publishes events consumed by:
- **product_storage_service** - consumes:
  - `category-events` (CategoryCreatedEvent)
  - `subsubcategory-events` (SubSubcategoryCreatedEvent)
  - `product-general-events` (ProductGeneralCreatedEvent)

**This service is a Kafka CONSUMER** (4 topics) - consumes events from:
- **identity-service** - publishes:
  - `user-events` (UserCreatedEvent) → syncs users and creates buyers
- **ecommerce-service** - publishes:
  - `order-events` (OrderCreatedEvent) → creates orders in back-office
  - `order-confirmed-events` (OrderConfirmedEvent) → updates order status
  - `order-packaging-progress-update-events` (OrderUpdatePackagingProgressEvent) → updates packaging progress

**Event-Driven Architecture**: No direct HTTP dependencies on other services. All inter-service communication happens via Kafka events for loose coupling and scalability.

## REST API Controllers

**13 Controllers** providing comprehensive back-office management APIs. All endpoints require JWT authentication.

### Core Product Management

**CategoryController** (`/api/categories`):
- Manages 3-layer category hierarchy (see Category System Architecture section above)

**ProductGeneralController** (`/api/product-general`):
- `POST /api/product-general` - Create product (with image upload)
- `GET /api/product-general/{prodGenId}` - Get product by ID
- `GET /api/product-general` - Get all products (paginated)
- `PUT /api/product-general/{prodGenId}` - Update product (with image upload)
- `DELETE /api/product-general/{prodGenId}` - Delete product (deletes R2 image)
- Publishes `product-general-events` on creation

### Order & Employee Management

**OrderController** (`/api/order`):
- See "Order Management System" section above for full endpoint list

**EmployeeController** (`/api/employee`):
- `POST /api/employee` - Create employee
- `GET /api/employee/{empId}` - Get employee by ID
- `GET /api/employee` - Get all employees
- `PUT /api/employee/{empId}` - Update employee (status, etc.)
- `DELETE /api/employee/{empId}` - Delete employee

### User Management

**UserController** (`/api/user`):
- `POST /api/user` - Create user (usually synced via Kafka)
- `GET /api/user/{userId}` - Get user by ID
- `GET /api/user` - Get all users (paginated)
- `PUT /api/user/{userId}` - Update user (with avatar upload)
- `DELETE /api/user/{userId}` - Delete user (deletes R2 avatar)

**BuyerController** (`/api/buyer`):
- `POST /api/buyer` - Create buyer
- `GET /api/buyer/{buyerId}` - Get buyer by ID
- `GET /api/buyer` - Get all buyers (paginated)
- `GET /api/buyer/by-user/{userId}` - Get buyer by user ID
- `PUT /api/buyer/{buyerId}` - Update buyer
- `DELETE /api/buyer/{buyerId}` - Delete buyer

### Event Management

**EventController** (`/api/event`):
- `POST /api/event` - Create event
- `GET /api/event/{eventId}` - Get event by ID
- `GET /api/event` - Get all events
- `PUT /api/event/{eventId}` - Update event
- `DELETE /api/event/{eventId}` - Delete event

**SaleEventController** (`/api/sale-event`):
- `POST /api/sale-event` - Create sale event
- `GET /api/sale-event/{saleEventId}` - Get sale event by ID
- `GET /api/sale-event` - Get all sale events
- `PUT /api/sale-event/{saleEventId}` - Update sale event
- `DELETE /api/sale-event/{saleEventId}` - Delete sale event

### Business Rules

**PaymentMethodController** (`/api/payment-method`):
- `POST /api/payment-method` - Create payment method
- `GET /api/payment-method/{paymentMethodId}` - Get by ID
- `GET /api/payment-method` - Get all payment methods
- `PUT /api/payment-method/{paymentMethodId}` - Update payment method
- `DELETE /api/payment-method/{paymentMethodId}` - Delete payment method

**CouponPolicyController** (`/api/coupon-policy`):
- Standard CRUD for coupon policies

**PreorderPolicyController** (`/api/preorder-policy`):
- Standard CRUD for preorder policies

### Supplier Management

**ProviderController** (`/api/provider`):
- `POST /api/provider` - Create provider/supplier
- `GET /api/provider/{providerId}` - Get by ID
- `GET /api/provider` - Get all providers
- `PUT /api/provider/{providerId}` - Update provider
- `DELETE /api/provider/{providerId}` - Delete provider

**EnterpriseStoreController** (`/api/enterprise-store`):
- Standard CRUD for enterprise stores

### Product Requests

**DemandResponseController** (`/api/demand-response`):
- Manage responses to customer product requests
- Standard CRUD operations

**Common Controller Patterns**:
- All controllers use `ApiResponse<T>` wrapper for consistent response format
- Error handling via try-catch returning `ApiResponse.ERROR()`
- Success responses return `ApiResponse.SUCCESS()`
- Most use `@RequiredArgsConstructor` for constructor injection
- JWT authentication required for all endpoints

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
- When creating subcategory (`isSubCategory="Y"`), validates parent category exists via `CategoryRepository.findById()`
- When creating SubSubcategory, validates parent is a subcategory via `CategoryRepository.existsAsSubcategory()`
- avgShelfDays required for SubSubcategory (used by product_storage_service for expiry tracking)
- Cascading deletes NOT enabled - must delete in order: ProductGeneral → SubSubcategory → Subcategory → Category

### Product General Management
- ProductGeneral entities use manually assigned IDs (not auto-increment) - ID passed from service layer
- Linked to SubSubcategory (finest granularity) via `subSubcategoryId`
- Images stored in R2 `product-general-img` bucket, public URLs in database
- Tags stored as String[] for search optimization
- Publishes `product-general-events` when created (consumed by product_storage_service)
- Default image URL used during seeding: `https://pub-0365edd1781141cdb68675969c7cdb87.r2.dev/5fe95e6c-c064-49c4-8712-1c8f54472502.jpg`

### Order Processing Workflow
- Orders created via Kafka `order-events` from ecommerce-service
- Status progression: PENDING → CONFIRMED → PACKING → SHIPPING → DELIVERED
- Employee assignments tracked via `confirmedBy`, `packagedBy`, `shippedBy` fields
- Packaging progress tracked 0-100% via `packagingProgress` field
- Employees query their tasks via `/emp/packaging-tasks` and `/emp/delivering-tasks`
- Status updates synchronized back to ecommerce-service via Kafka

### User/Buyer Synchronization
- Users created in identity-service publish `user-events`
- `UserCreatedConsumer` creates both User and Buyer records in back-office
- Ensures user data consistency across services
- Buyer linked to User via `userId` foreign key

### Event Publishing Pattern
- All create operations publish Kafka events after successful database save
- Uses `@Transactional` to ensure atomicity
- Events published asynchronously with callback logging (`whenComplete()`)
- Failures logged but don't roll back transaction (event publishing is best-effort)
- Event key is entity ID (for Kafka partitioning and ordering)

### File Upload Pattern
- Multipart files accepted via controllers (`@RequestParam MultipartFile`)
- Uploaded to R2 via `R2UploadService.upload(file, bucketName)`
- Public URL returned and stored in entity (avtUrl, imgUrl fields)
- Old files deleted when entity updated/deleted via `R2UploadService.delete(key, bucketName)`
- Two buckets: `back-office-user-avts` (user avatars), `product-general-img` (product images)
- Default URLs used when no custom image uploaded

## Recent Changes & Features

Based on recent git commits (latest to oldest):

**c150cd1** - `feat: add avgShelfDays for subsubcategory`
- Added `avgShelfDays` Integer field to SubSubcategory entity
- Used by product_storage_service for inventory expiry tracking
- Seeded with realistic values (1-180 days) per product category

**334b5ea** - `feat: endpoint for delivering and packaging employee`
- Added `/emp/packaging-tasks` endpoint for packaging employees
- Added `/emp/delivering-tasks` endpoint for delivery employees
- Employee-specific task views based on authenticated user

**6794f87** - `feat: helper endpoint for order management`
- Added admin order management endpoints with filtering
- OrderInformation projection for rich order views with buyer data
- Query filtering by status, packagedBy, shippedBy, orderId

**311da47** - `feat: add employee entity`
- Created Employee entity with empId, userId, empStatus, hireDate
- EmployeeController with full CRUD operations
- Employee assignment to orders via confirmedBy, packagedBy, shippedBy

**9dd4742** - `feat: process order flow`
- Implemented full order status workflow
- Added /confirm, /package, /ship, /deliver endpoints
- Packaging progress tracking (0-100%)
- Order status: PENDING → CONFIRMED → PACKING → SHIPPING → DELIVERED

**7f0860e, a08eb95** - `feat: seeding data`
- Comprehensive Vietnamese fresh food sample data
- 3 main categories, 14 subcategories, 60 sub-subcategories
- 150+ product generals with Vietnamese names
- 6 users, 5 buyers, 3 payment methods

**ac67dec** - `feat: consume order from ecommerce`
- Added OrderCreatedConsumer for `order-events` topic
- Added OrderConfirmedConsumer for `order-confirmed-events` topic
- Added OrderUpdatePackagingProgressEventConsumer for progress updates
- User synchronization via UserCreatedConsumer

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
