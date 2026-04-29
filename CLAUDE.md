# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Service Overview

**back-office-service** manages product catalog, 3-layer category hierarchy, order processing, employee management, user/buyer sync, and back-office operations. Runs on port 9100 with PostgreSQL (`back_office_db`), publishes/consumes Kafka events, and integrates with Cloudflare R2 for file storage.

**Stack**: Spring Boot 4.0.2, Java 25, Spring Security (JWT), Spring Data JPA, Kafka, AWS SDK S3 (Cloudflare R2)

## Build & Run

```bash
# Run (skip tests)
./mvnw clean install -DskipTests && ./mvnw spring-boot:run

# Tests
./mvnw test
./mvnw test -Dtest=CategoryServiceTest       # single class
./mvnw test -Dtest=CategoryServiceTest#methodName  # single method

# Docker (from /whole-system/)
./service.sh rebuild back-office
./service.sh logs back-office
```

## Package Structure

```
edu.hcmut.datn.back_office_service/
├── common/enums/     # OrderStatus, Unit, EmployeeStatus, Gender, PaymentType, etc.
├── config/           # R2Config, WebConfig (CORS), DataSeeder
├── controller/       # 13 REST controllers
├── dao/              # 17 JPA entities
├── dto/
│   ├── request/      # *CreateRequest, *UpdateRequest (per entity)
│   └── response/     # ApiResponse<T>
├── exception/        # {Entity}NotFoundException, {Entity}AlreadyExistsException
├── messaging/        # Kafka producers & consumers
│   ├── category/     # CategoryProducer
│   ├── order/        # OrderEventProducer + 3 consumers
│   ├── productgeneral/ # ProductGeneralProducer
│   └── user/         # UserCreatedConsumer
├── repository/
│   └── projection/   # OrderInformation, BuyerUserProjection
├── security/portable/ # JWT filter, SecurityConfig, AuthenticatedUser
├── service/
│   ├── impl/
│   └── scheduler/    # DynamicSchedulerService
└── util/
```

## Architecture Pattern

**Controller → Service (interface + impl) → Repository → Entity**

- Services use `@RequiredArgsConstructor` (Lombok constructor injection)
- All endpoints return `ApiResponse<T>` — a class (not record) with fields:
  - `type` (`ApiResponseType`: GOOD, ERROR, WARN, SKIP_AS_GOOD)
  - `code` (HTTP status string)
  - `message` (human-readable)
  - `detail` (payload)
  - `timestamp`
- Factory methods: `ApiResponse.SUCCESS()`, `ApiResponse.ERROR()`, `ApiResponse.WARN()`, `ApiResponse.SKIP_AS_GOOD()`
- Error handling in controllers: `try/catch` returning `ApiResponse.ERROR()`

## Category System

**3-Layer Hierarchy**: Category → Subcategory → SubSubcategory

- `Category` table stores both main categories (`isSubCategory="N"`) and subcategories (`isSubCategory="Y"`, has `belongToCategory` parent ID)
- `SubSubcategory` is a separate table referencing a subcategory; contains `avgShelfDays` (Integer) used by product_storage_service for expiry tracking
- Cascading deletes are NOT enabled — delete in order: ProductGeneral → SubSubcategory → Subcategory → Category

**CategoryController endpoints** (`/api/categories`): full CRUD for categories, subcategories, and subsubcategories.

## Kafka Topics

**Published by this service**:
| Topic | Producer | Event |
|---|---|---|
| `category-events` | CategoryProducer | CategoryCreatedEvent |
| `subsubcategory-events` | CategoryProducer | SubSubcategoryCreatedEvent |
| `product-general-events` | ProductGeneralProducer | ProductGeneralCreatedEvent |
| `order-delivering-events` | OrderEventProducer | OrderDeliveringEvent |
| `order-delivered-events` | OrderEventProducer | OrderDeliveredEvent |

**Consumed by this service**:
| Topic | Consumer | Action |
|---|---|---|
| `user-events` | UserCreatedConsumer | Creates User + Buyer records |
| `order-events` | OrderCreatedConsumer | Creates Order record |
| `order-confirmed-events` | OrderConfirmedConsumer | Sets status=CONFIRMED, confirmedBy |
| `order-packaging-progress-update-events` | OrderUpdatePackagingProgressEventConsumer | Updates packagingProgress (0-100%) |
| `provider-create-events` | ProviderCreatedConsumer | Creates Provider (and User if isFreshAccount=true) |

Consumer group: `back-office-group`. Failures are logged but don't retry.

## Order Processing Workflow

```
PENDING → CONFIRMED → PACKING → SHIPPING → DELIVERED
         /confirm    /package  /ship      /deliver
```

Employee ID for assignment comes from the authenticated JWT user (`getCurrentUserId()`). Packaging progress (0-100%) is updated via `PUT /api/order/{orderId}/progress`.

**Employee task views**:
- `GET /api/order/emp/packaging-tasks` — orders with status=PACKING assigned to current employee
- `GET /api/order/emp/delivering-tasks` — orders with status=SHIPPING assigned to current employee

`OrderInformation` projection joins `orders + users + buyers` for admin views (`GET /api/order/admin`).

## Entity Conventions

- IDs: `@GeneratedValue(strategy = GenerationType.IDENTITY)` except `ProductGeneral` (manually assigned)
- Timestamps: managed via `@PrePersist`/`@PreUpdate` on `createdAt`/`updatedAt`
- Lombok: `@Getter`/`@Setter` on individual fields, not class-level
- Table names: snake_case plural (`categories`, `product_generals`, `sub_subcategories`)

## Security

All endpoints require a valid JWT (`Authorization: Bearer <token>`) — no public endpoints. JWT validated by `JwtAuthenticationFilter` in `security/portable/`. Current user retrieved via `@AuthenticationPrincipal AuthenticatedUser` or `SecurityContextHolder`.

## Provider Verification System

Providers submit food-safety evidence for manual review by back-office staff.

**New enums** (`common/enums/`):
- `CertificateType` — VIETGAP, VIETGAP_LIVESTOCK, GLOBALGAP, HACCP, ISO_22000, OCOP, ATTP_MOH, ATTP_MARD
- `VideoType` — WORKING_ENVIRONMENT, GARDEN_FARM, MEAT_PROCESSING, VEGETABLE_HARVEST, STORAGE_FACILITY, OTHER
- `ReviewStatus` — PENDING, APPROVED, REJECTED (separate from `VerificationStatus`)

**New entities** (`dao/`):
- `ProviderCertificate` — certificateId, providerId, certificateType, certificateNumber, issuingAuthority, issuedDate, expiryDate (nullable), documentUrl, status (ReviewStatus), reviewedBy, reviewNote, reviewedAt, createdAt, updatedAt
- `ProviderVerificationVideo` — videoId, providerId, videoType, videoUrl (nullable until file uploaded), description, status (ReviewStatus), reviewedBy, reviewNote, reviewedAt, createdAt, updatedAt

**New Kafka consumer**:
- Topic: `provider-create-events` → `ProviderCreatedConsumer`
- If `isFreshAccount=true`: creates both User and Provider records
- If `isFreshAccount=false`: creates only Provider record

**Provider self-service endpoints** (all use `principal.getId()` → resolved to providerId internally):
| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/provider/me` | Check if current user has a provider account; returns SKIP_AS_GOOD if not |
| `GET` | `/api/provider/my-status` | Provider's verification status + all certs + all videos |
| `POST` | `/api/provider/certificates` | Upload a certificate (multipart: type, number, authority, dates, file) |
| `GET` | `/api/provider/certificates` | List own certificates |
| `GET` | `/api/provider/certificates/{id}` | Get a certificate |
| `DELETE` | `/api/provider/certificates/{id}` | Delete own certificate + R2 file |
| `POST` | `/api/provider/videos` | Create video record (JSON: videoType, description); videoUrl=null |
| `POST` | `/api/provider/videos/{videoId}/upload` | Upload the video file (multipart); updates videoUrl |
| `GET` | `/api/provider/videos` | List own videos |
| `GET` | `/api/provider/videos/{id}` | Get a video |
| `DELETE` | `/api/provider/videos/{id}` | Delete own video + R2 file |

**Admin endpoints**:
| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/provider?status=PENDING` | List providers filtered by verificationStatus |
| `GET` | `/api/provider/{providerId}` | Provider profile merged with user info (email, name, etc.) |
| `GET` | `/api/provider/{providerId}/certificates` | All certs for a provider |
| `GET` | `/api/provider/{providerId}/videos` | All videos for a provider |
| `PUT` | `/api/provider/certificates/{id}/review` | Approve/reject a certificate |
| `PUT` | `/api/provider/videos/{id}/review` | Approve/reject a video |
| `PUT` | `/api/provider/{providerId}` | Update provider verificationStatus / other fields |

`GET /api/provider/{providerId}` returns `ProviderDetailResponse` — a merged DTO with both Provider and User fields assembled via `ProviderDetailResponse.from(Provider, User)`.

Review is manual only — no auto-approval. Reviewer identity taken from JWT (not request body).

**UI guides**: `provider.md` (provider onboarding flow) and `provider-management.md` (admin review flow).

## Cloudflare R2

Four buckets: `back-office-user-avts` (user avatars), `product-general-img` (product images), `provider-certificates` (provider cert documents), `provider-verification-videos` (provider videos). `R2UploadService` wraps the AWS S3 SDK. Old files are deleted on entity update/delete. Max upload size: **500MB** (raised globally for video uploads; configured in `application.yaml` under `spring.servlet.multipart`).

## Environment Variables

```env
DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD   # PostgreSQL → back_office_db
KAFKA_HOST, KAFKA_PORT
R2_ACCOUNT_ID, R2_ACCESS_KEY, R2_SECRET_KEY
```

Optional service-to-service URLs (currently configured but not actively used for HTTP calls — inter-service comms go through Kafka):
```env
IDENTITY_HOST/PORT (default localhost:9000)
PRODUCT_STORAGE_HOST/PORT (default localhost:9200)
ECOMMERCE_HOST/PORT (default localhost:9300)
```

## Data Seeder

Runs on startup if `categories` table is empty. Seeds 3 main categories, 14 subcategories, 60 sub-subcategories with `avgShelfDays`, 150+ Vietnamese fresh food product generals, 6 users, 5 buyers, 3 payment methods. Implemented as a `CommandLineRunner` bean in `config/DataSeeder.java`.
