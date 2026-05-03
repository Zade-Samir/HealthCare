# 🏥 Healthcare Management System — Microservices Architecture

> A full-stack, production-grade **Healthcare Management Platform** built with **Spring Boot**, **Spring Cloud**, **JWT Security**, and **PostgreSQL** using a clean Microservices Architecture. Designed to manage patients, doctors, appointments, medical records, and billing through independent, loosely-coupled services.

---

## 📑 Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Architecture Overview](#3-architecture-overview)
4. [System Design Diagram](#4-system-design-diagram)
5. [Microservices Breakdown](#5-microservices-breakdown)
6. [Database Design (ER Diagram)](#6-database-design-er-diagram)
7. [Class Diagram](#7-class-diagram)
8. [Use Case Diagram](#8-use-case-diagram)
9. [API Reference](#9-api-reference)
10. [Security & Authentication Flow](#10-security--authentication-flow)
11. [Inter-Service Communication](#11-inter-service-communication)
12. [Resilience & Fault Tolerance](#12-resilience--fault-tolerance)
13. [Data Flow — End-to-End Scenarios](#13-data-flow--end-to-end-scenarios)
14. [Service Port Registry](#14-service-port-registry)
15. [Setup & Running the Project](#15-setup--running-the-project)
16. [Design Patterns Used](#16-design-patterns-used)
17. [Roles & Permissions Matrix](#17-roles--permissions-matrix)
18. [Project Structure](#18-project-structure)

---

## 1. Project Overview

The **Healthcare Management System** is a backend microservices application that models a real-world hospital/clinic workflow. It supports three user roles — **Admin**, **Doctor**, and **Patient** — each with distinct permissions and access rights.

### Core Capabilities

| Capability | Description |
|---|---|
| **Authentication & Authorization** | JWT-based login/register with role-based access control |
| **Patient Management** | Register, update, view, and soft-delete patients |
| **Doctor Management** | Manage doctors, filter by specialization and availability |
| **Appointment Booking** | Book, cancel, and complete appointments with status transitions |
| **Medical Records** | Doctors create diagnoses/prescriptions; full patient history available |
| **Billing & Payments** | Generate bills tied to appointments, track payment status and method |
| **Service Discovery** | Eureka-based dynamic service registration and discovery |
| **API Gateway** | Single entry point with JWT validation and internal secret routing |
| **Circuit Breaking** | Resilience4j circuit breakers prevent cascade failures |

---

## 2. Tech Stack

### Backend Framework
- **Java 17+**
- **Spring Boot 4.0.6** — Core application framework
- **Spring Cloud** — Microservices orchestration suite

### Spring Cloud Components
| Component | Purpose |
|---|---|
| `spring-cloud-starter-gateway` | Reactive API Gateway |
| `spring-cloud-starter-netflix-eureka-server` | Service Registry (Eureka Server) |
| `spring-cloud-starter-netflix-eureka-client` | Service discovery client for each microservice |
| `spring-cloud-starter-openfeign` | Declarative REST client for inter-service calls |
| `spring-cloud-starter-circuitbreaker-resilience4j` | Fault tolerance and circuit breaking |

### Security
- **Spring Security** — Method-level and filter-chain security
- **JJWT 0.13.0** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) — JWT token generation and validation

### Database
- **PostgreSQL** — Each service has its own isolated database
- **Spring Data JPA / Hibernate** — ORM layer

### Utilities
- **Lombok** — Boilerplate elimination (`@Getter`, `@Setter`, `@AllArgsConstructor`, etc.)
- **MapStruct** — Type-safe DTO ↔ Entity mapping at compile time
- **Jakarta Validation** — Request body validation (`@Valid`, `@NotBlank`, etc.)

---

## 3. Architecture Overview

The system follows a **Microservices Architecture** with the following core architectural principles:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CLIENT (Postman / Browser)                     │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │ HTTP Requests
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY  (Port: 8080)                            │
│  ┌──────────────────────┐    ┌────────────────────────────────────────┐ │
│  │   RouteValidator     │    │       AuthenticationFilter             │ │
│  │  (Open vs Secured    │    │  (JWT Validation + Bearer extraction)  │ │
│  │      routes)         │    │                                        │ │
│  └──────────────────────┘    └────────────────────────────────────────┘ │
│                    + AddRequestHeader: X-Internal-Secret                │
└───────┬───────────────────────────────────────────────────────┬─────────┘
        │ Load Balanced Routing (lb://)                         │
        ▼                                                       ▼
┌───────────────────┐                               ┌──────────────────────┐
│  EUREKA SERVER    │ ◄──── Service Registration ───│   All Microservices  │
│  (Port: 8761)     │       & Discovery             │                      │
└───────────────────┘                               └──────────────────────┘
        │
        ▼ Routes traffic to:
┌──────────────────────────────────────────────────────────────────────────┐
│                         MICROSERVICES LAYER                              │
│                                                                          │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │ Auth        │  │ Patient      │  │ Doctor       │  │ Appointment │  │
│  │ Service     │  │ Service      │  │ Service      │  │ Service     │  │
│  │ :9000       │  │ :9001        │  │ :9002        │  │ :9003       │  │
│  └──────┬──────┘  └──────┬───────┘  └──────┬───────┘  └──────┬──────┘  │
│         │                │                  │                  │         │
│  ┌──────┴──────┐  ┌──────┴───────┐  ┌──────┴───────┐  ┌──────┴──────┐  │
│  │  authDB     │  │  patientDB   │  │  doctorDB    │  │  apptDB     │  │
│  │ (PostgreSQL)│  │ (PostgreSQL) │  │ (PostgreSQL) │  │ (PostgreSQL)│  │
│  └─────────────┘  └──────────────┘  └──────────────┘  └─────────────┘  │
│                                                                          │
│  ┌─────────────────────┐              ┌──────────────────────────────┐  │
│  │   Billing Service   │              │  Medical Record Service      │  │
│  │   :9004             │              │  :9006                       │  │
│  └──────────┬──────────┘              └──────────────┬───────────────┘  │
│             │                                        │                  │
│  ┌──────────┴──────────┐              ┌──────────────┴───────────────┐  │
│  │  billingDB          │              │  medicalRecordsDB            │  │
│  │  (PostgreSQL)       │              │  (PostgreSQL)                │  │
│  └─────────────────────┘              └──────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

1. **Database per Service** — Each microservice owns its own PostgreSQL database. No shared schema, ensuring loose coupling and independent deployability.
2. **Single Entry Point** — All external traffic passes through the API Gateway which handles JWT validation before forwarding to downstream services.
3. **Internal Secret Header** — After gateway JWT validation, an internal secret header (`X-Internal-Secret: HealthProject2026`) is injected so downstream services know the call is from a trusted internal source.
4. **Service Mesh via Eureka** — All services register with Eureka, enabling client-side load balancing and service discovery with `lb://` URIs.
5. **Feign + Circuit Breakers** — Inter-service HTTP calls use OpenFeign with Resilience4j circuit breakers to prevent cascading failures.

---

## 4. System Design Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              HEALTHCARE SYSTEM DESIGN                               │
│                                                                                     │
│   ┌─────────┐     POST /auth/register                                               │
│   │         │ ──────────────────────────► ┌─────────────────────────────────────┐  │
│   │  CLIENT │     POST /auth/login         │          AUTH SERVICE               │  │
│   │         │ ──────────────────────────► │  - Validates credentials            │  │
│   │         │ ◄── JWT Token ───────────── │  - Checks security codes for roles  │  │
│   └────┬────┘                             │  - Generates signed JWT             │  │
│        │                                  └─────────────────────────────────────┘  │
│        │ All subsequent requests: Bearer <JWT>                                      │
│        ▼                                                                            │
│   ┌────────────────────────────────────────────────────────────────────────────┐   │
│   │                         API GATEWAY (:8080)                                │   │
│   │                                                                            │   │
│   │   RouteValidator checks if path is in open list:                          │   │
│   │     OPEN: /auth/register, /auth/login, /eureka                            │   │
│   │     SECURED: everything else                                               │   │
│   │                                                                            │   │
│   │   AuthenticationFilter (for secured routes):                              │   │
│   │     1. Extract Authorization header                                        │   │
│   │     2. Strip "Bearer " prefix                                              │   │
│   │     3. Validate JWT using shared secret key                               │   │
│   │     4. Forward request + inject X-Internal-Secret header                  │   │
│   │                                                                            │   │
│   │   Route Table:                                                             │   │
│   │     /auth/**        → AUTH-SERVICE     (no filter)                        │   │
│   │     /doctors/**     → DOCTOR-SERVICE   (+ AuthFilter)                     │   │
│   │     /patients/**    → PATIENT-SERVICE  (+ AuthFilter)                     │   │
│   │     /appointments/**→ APPT-SERVICE     (+ AuthFilter)                     │   │
│   │     /records/**     → MEDICAL-SERVICE  (+ AuthFilter)                     │   │
│   │     /billing/**     → BILLING-SERVICE  (+ AuthFilter)                     │   │
│   └───────────────────────────┬────────────────────────────────────────────────┘  │
│                               │ lb:// Load Balanced via Eureka                     │
│           ┌───────────────────┼────────────────────────────────┐                  │
│           ▼                   ▼                                ▼                  │
│   ┌──────────────┐   ┌────────────────┐            ┌─────────────────────┐        │
│   │ Patient Svc  │   │  Doctor Svc    │            │  Appointment Svc    │        │
│   │ JwtFilter    │   │  JwtFilter     │            │  JwtFilter          │        │
│   │ (validates   │   │  (validates    │ ◄──Feign── │  - Validates patient│        │
│   │  JWT again + │   │   JWT again +  │ ──Feign──► │  - Validates doctor │        │
│   │  checks role)│   │   checks role) │            │  - Creates appt     │        │
│   └──────────────┘   └────────────────┘            └─────────────────────┘        │
│                                                              │ Feign               │
│                                                              ▼                     │
│                                                  ┌─────────────────────┐          │
│                                                  │   Billing Service   │          │
│                                                  │  - Validates appt   │          │
│                                                  │  - Validates patient│          │
│                                                  │  - Stores payment   │          │
│                                                  └─────────────────────┘          │
│                                                                                    │
│                  ┌─────────────────────────────────────────────────────┐          │
│                  │             EUREKA SERVER (:8761)                   │          │
│                  │  All services register → Enables lb:// routing      │          │
│                  └─────────────────────────────────────────────────────┘          │
└────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Microservices Breakdown

### 5.1 Eureka Server (Port: 8761)
The **Service Registry**. All other microservices register themselves here on startup. The API Gateway uses Eureka to resolve `lb://SERVICE-NAME` URIs to actual host:port addresses for load-balanced routing.

- Does **not** register itself (`register-with-eureka: false`)
- Accessible at `http://localhost:8761` (Eureka Dashboard)

---

### 5.2 API Gateway (Port: 8080)
The **single entry point** for all client requests.

**Responsibilities:**
- Route requests to the appropriate downstream service
- Validate JWT tokens on all secured routes (via `AuthenticationFilter`)
- Inject the `X-Internal-Secret` header after successful validation
- Delegate service discovery to Eureka

**Open Routes (no JWT required):**
- `POST /auth/register`
- `POST /auth/login`
- `/eureka/**`

---

### 5.3 Auth Service (Port: 9000)

**Responsibilities:** User registration and JWT login.

**Role Assignment Logic:**
```
if role == "ADMIN"   → require adminCode  == "SUPER_SECRET_ADMIN_99"  → assign ADMIN
if role == "DOCTOR"  → require doctorCode == "DOCTOR_CODE_123"        → assign DOCTOR
else                 →                                                  assign PATIENT
```

**Key Classes:**
- `User` — Entity with `id`, `username`, `email`, `password`, `Role`
- `Role` — Enum: `ADMIN`, `DOCTOR`, `PATIENT`
- `AuthServiceImpl` — Handles registration and login
- `JwtUtil` — Generates and validates JWT tokens using HS256 + shared secret key
- `CustomUserDetailsService` — Loads user by username or email for Spring Security

---

### 5.4 Patient Service (Port: 9001)

**Responsibilities:** Full CRUD for patient records.

**Entity Fields:** `id`, `name`, `email` (unique), `phone`, `age`, `gender`, `address`, `createdAt`, `isDeleted`

**Features:**
- Soft delete (sets `isDeleted = true`, never hard-deletes)
- `@PrePersist` auto-sets `createdAt`
- Role-based access enforced at method level

---

### 5.5 Doctor Service (Port: 9002)

**Responsibilities:** Manage doctor profiles.

**Entity Fields:** `id`, `name`, `email` (unique), `specialization`, `experience`, `isAvailable`, `rating`, `isDeleted`

**Features:**
- Filter doctors by specialization
- Filter by availability (`isAvailable = true`)
- Soft delete
- Only `ADMIN` can add/delete doctors; `DOCTOR` can update their own profile

---

### 5.6 Appointment Service (Port: 9003)

**Responsibilities:** Manages the lifecycle of appointments.

**Entity Fields:** `id`, `patientId`, `doctorId`, `appointmentDate`, `status`, `reason`

**Status State Machine:**
```
              ┌──────────┐
    [BOOK]    │  BOOKED  │    [CANCEL]    ──► CANCELLED
   ──────────►│          │
              └──────────┘    [COMPLETE]  ──► COMPLETED
```
- Only `BOOKED` appointments can be cancelled or completed (enforced by `InvalidStatusTransitionException`)
- On booking: validates both Patient and Doctor via **Feign clients**
- Feign calls are protected by **Resilience4j Circuit Breaker** with `appointmentFallback`

---

### 5.7 Medical Record Service (Port: 9006)

**Responsibilities:** Stores patient diagnoses and prescriptions after appointment completion.

**Entity Fields:** `id`, `patientId`, `doctorId`, `diagnosis` (TEXT), `prescription` (TEXT), `createdAt`, `isDeleted`

**Features:**
- Only `DOCTOR` can create records
- Soft delete (Admin only)
- Validates patient and doctor via Feign before saving
- Full patient history retrieval

---

### 5.8 Billing Service (Port: 9004)

**Responsibilities:** Processes payments for completed appointments.

**Entity Fields:** `id`, `appointmentId` (unique), `patientId`, `amount` (BigDecimal), `paymentStatus`, `paymentMethod`, `transactionId`, `createdAt`

**Enums:**
- `PaymentStatus`: `PAID`, `PENDING`
- `PaymentMethod`: `UPI`, `CARD`, `CASH`, `NONE`

**Business Validation:**
- Fetches appointment details via Feign
- Verifies that `appointment.patientId == billing.patientId` (fraud check)
- Validates patient via Feign
- Uses `BigDecimal` for amount (avoids floating-point precision errors)

---

## 6. Database Design (ER Diagram)

Each service has its own independent PostgreSQL database. The relationships between services are maintained through **foreign key IDs** (not actual DB foreign keys, since databases are separate).

```
┌──────────────────────────────────────────────────────────────────────┐
│                    DATABASE PER SERVICE (Polyglot Persistence)        │
└──────────────────────────────────────────────────────────────────────┘

authDB
┌─────────────────────────────────────┐
│               users                 │
├──────────────┬──────────────────────┤
│ id           │ BIGSERIAL PK         │
│ username     │ VARCHAR UNIQUE       │
│ email        │ VARCHAR UNIQUE       │
│ password     │ VARCHAR (BCrypt)     │
│ role         │ ENUM(ADMIN,DOCTOR,   │
│              │      PATIENT)        │
└─────────────────────────────────────┘

patientDB
┌─────────────────────────────────────┐
│               patient               │
├──────────────┬──────────────────────┤
│ id           │ BIGSERIAL PK         │
│ name         │ VARCHAR              │
│ email        │ VARCHAR UNIQUE       │
│ phone        │ VARCHAR              │
│ age          │ INTEGER              │
│ gender       │ VARCHAR              │
│ address      │ VARCHAR              │
│ created_at   │ TIMESTAMP            │
│ is_deleted   │ BOOLEAN DEFAULT false│
└─────────────────────────────────────┘

doctorDB
┌─────────────────────────────────────┐
│               doctor                │
├──────────────┬──────────────────────┤
│ id           │ BIGSERIAL PK         │
│ name         │ VARCHAR              │
│ email        │ VARCHAR UNIQUE       │
│ specialization│ VARCHAR             │
│ experience   │ INTEGER              │
│ is_available │ BOOLEAN              │
│ rating       │ DOUBLE               │
│ is_deleted   │ BOOLEAN DEFAULT false│
└─────────────────────────────────────┘

appointmentDB
┌─────────────────────────────────────┐
│             appointment             │
├──────────────┬──────────────────────┤
│ id           │ BIGSERIAL PK         │
│ patient_id   │ BIGINT (FK→patientDB)│
│ doctor_id    │ BIGINT (FK→doctorDB) │
│ appointment_date│ TIMESTAMP         │
│ status       │ ENUM(BOOKED,         │
│              │  CANCELLED,COMPLETED)│
│ reason       │ VARCHAR              │
└─────────────────────────────────────┘

medicalRecordsDB
┌─────────────────────────────────────┐
│           medical_records           │
├──────────────┬──────────────────────┤
│ id           │ BIGSERIAL PK         │
│ patient_id   │ BIGINT (FK→patientDB)│
│ doctor_id    │ BIGINT (FK→doctorDB) │
│ diagnosis    │ TEXT                 │
│ prescription │ TEXT                 │
│ created_at   │ TIMESTAMP            │
│ is_deleted   │ BOOLEAN DEFAULT false│
└─────────────────────────────────────┘

billingDB
┌─────────────────────────────────────┐
│               billing               │
├──────────────┬──────────────────────┤
│ id           │ BIGSERIAL PK         │
│ appointment_id│ BIGINT UNIQUE NN    │
│ patient_id   │ BIGINT NN            │
│ amount       │ DECIMAL NN           │
│ payment_status│ENUM(PAID,PENDING)   │
│ payment_method│ENUM(UPI,CARD,       │
│              │  CASH,NONE)          │
│ transaction_id│VARCHAR              │
│ created_at   │ TIMESTAMP            │
└─────────────────────────────────────┘
```

**Cross-Service Relationships (logical, not physical FK):**
```
patient.id  ←──── appointment.patient_id
doctor.id   ←──── appointment.doctor_id
patient.id  ←──── medical_records.patient_id
doctor.id   ←──── medical_records.doctor_id
patient.id  ←──── billing.patient_id
appointment.id ←─ billing.appointment_id
```

---

## 7. Class Diagram

### Auth Service
```
┌─────────────────────────────┐
│        AuthController       │
├─────────────────────────────┤
│ + register(AuthRequestDTO)  │
│ + login(LoginRequestDTO)    │
└──────────────┬──────────────┘
               │ uses
┌──────────────▼──────────────┐     ┌─────────────────────┐
│       AuthService (I)       │     │       User           │
├─────────────────────────────┤     ├─────────────────────┤
│ + register(AuthRequestDTO)  │     │ - id: Long          │
│ + login(LoginRequestDTO)    │     │ - username: String  │
└──────────────┬──────────────┘     │ - email: String     │
               │ implements         │ - password: String  │
┌──────────────▼──────────────┐     │ - role: Role        │
│     AuthServiceImpl         │     └──────────┬──────────┘
├─────────────────────────────┤                │
│ - userRepository            │     ┌──────────▼──────────┐
│ - passwordEncoder           │     │    Role (enum)       │
│ - authenticationManager     │     ├─────────────────────┤
│ - jwtUtil                   │     │ ADMIN               │
│ - doctorCode: String        │     │ DOCTOR              │
│ - adminCode: String         │     │ PATIENT             │
└─────────────────────────────┘     └─────────────────────┘

┌──────────────────────────────────┐
│           JwtUtil                │
├──────────────────────────────────┤
│ - secretKey: String              │
│ + generateToken(username, role)  │
│ + validateToken(token)           │
│ + extractUsername(token)         │
└──────────────────────────────────┘
```

### Appointment Service
```
┌───────────────────────────────────────┐
│         AppointmentController         │
├───────────────────────────────────────┤
│ + book(AppointmentRequestDTO)         │  [ADMIN, PATIENT]
│ + getAppointment(id)                  │  [ADMIN, DOCTOR, PATIENT]
│ + getAllAppByPatientId(patientId)      │  [ADMIN, PATIENT]
│ + getAllAppByDoctorId(doctorId)        │  [ADMIN, DOCTOR]
│ + cancelAppointment(id)               │  [ADMIN, DOCTOR, PATIENT]
│ + completeAppointment(id)             │  [ADMIN, DOCTOR]
│ + appointmentFallback(...)            │  (Circuit Breaker)
└──────────────┬────────────────────────┘
               │
┌──────────────▼────────────────────────┐
│       AppointmentServiceImpl          │
├───────────────────────────────────────┤
│ - appointmentRepository               │
│ - appointmentMapper                   │
│ - patientClient (Feign)               │
│ - doctorClient  (Feign)               │
└───────────────────────────────────────┘

┌───────────────────────────────────────┐
│           Appointment (Entity)        │
├───────────────────────────────────────┤
│ - id: Long                            │
│ - patientId: Long                     │
│ - doctorId: Long                      │
│ - appointmentDate: LocalDateTime      │
│ - status: AppointmentStatus           │
│ - reason: String                      │
└───────────────────────────────────────┘

┌───────────────────────┐
│  AppointmentStatus    │
├───────────────────────┤
│ BOOKED                │
│ CANCELLED             │
│ COMPLETED             │
└───────────────────────┘
```

### Common Pattern (All Services)
```
Controller
    │ @PreAuthorize (role check)
    │ calls
    ▼
ServiceInterface
    │ implemented by
    ▼
ServiceImpl
    │ uses
    ├──► Repository (Spring Data JPA)
    ├──► Mapper (MapStruct: DTO ↔ Entity)
    └──► FeignClient (inter-service calls, where applicable)

DTOs:
  XxxRequestDTO  ──MapStruct──► Entity  ──MapStruct──► XxxResponseDTO

Exception Hierarchy:
  XxxNotFoundException (404)
  InvalidStatusTransitionException (400)
  GlobalExceptionHandler (@RestControllerAdvice)
```

---

## 8. Use Case Diagram

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                        HEALTHCARE SYSTEM — USE CASES                             │
└──────────────────────────────────────────────────────────────────────────────────┘

 ┌──────────┐    ┌──────────────┐    ┌──────────────────────────────────────────┐
 │          │    │              │    │                  SYSTEM                  │
 │  ADMIN   │    │   DOCTOR     │    │                                          │
 │          │    │              │    │  ┌─────────────────────────────────────┐ │
 └────┬─────┘    └──────┬───────┘    │  │            AUTH                     │ │
      │                 │            │  │  ○ Register (with optional role code)│ │
      │     ┌───────────┘            │  │  ○ Login → Receive JWT              │ │
      │     │                        │  └─────────────────────────────────────┘ │
      │     │    ┌──────────┐        │  ┌─────────────────────────────────────┐ │
      │     │    │          │        │  │          PATIENT MGMT               │ │
      │     │    │ PATIENT  │        │  │  ○ Create Patient   [ADMIN,PATIENT] │ │
      │     │    │          │        │  │  ○ Get Patient      [ALL]           │ │
      │     │    └────┬─────┘        │  │  ○ Get All Patients [ADMIN]         │ │
      │     │         │              │  │  ○ Update Patient   [ADMIN,PATIENT] │ │
      │     │         │              │  │  ○ Soft Delete      [ADMIN]         │ │
      │     │         │              │  └─────────────────────────────────────┘ │
      │     │         │              │  ┌─────────────────────────────────────┐ │
      │     │         │              │  │          DOCTOR MGMT                │ │
      │     │         │              │  │  ○ Add Doctor       [ADMIN]         │ │
      │     │         │              │  │  ○ Get Doctor       [ALL]           │ │
      │     │         │              │  │  ○ Get All Doctors  [ALL]           │ │
      │     │         │              │  │  ○ Filter Available [ALL]           │ │
      │     │         │              │  │  ○ Filter by Spec.  [ALL]           │ │
      │     │         │              │  │  ○ Update Doctor    [ADMIN,DOCTOR]  │ │
      │     │         │              │  │  ○ Soft Delete      [ADMIN]         │ │
      │     │         │              │  └─────────────────────────────────────┘ │
      │     │         │              │  ┌─────────────────────────────────────┐ │
      │     │         │              │  │        APPOINTMENT MGMT             │ │
      │     │         │              │  │  ○ Book Appointment [ADMIN,PATIENT] │ │
      │     │         │              │  │  ○ Get Appointment  [ALL]           │ │
      │     │         │              │  │  ○ By Patient ID    [ADMIN,PATIENT] │ │
      │     │         │              │  │  ○ By Doctor ID     [ADMIN,DOCTOR]  │ │
      │     │         │              │  │  ○ Cancel           [ALL]           │ │
      │     │         │              │  │  ○ Complete         [ADMIN,DOCTOR]  │ │
      │     │         │              │  └─────────────────────────────────────┘ │
      │     │         │              │  ┌─────────────────────────────────────┐ │
      │     │         │              │  │       MEDICAL RECORDS               │ │
      │     │         │              │  │  ○ Create Record    [DOCTOR]        │ │
      │     │         │              │  │  ○ Get Record       [ALL]           │ │
      │     │         │              │  │  ○ Patient History  [ALL]           │ │
      │     │         │              │  │  ○ Soft Delete      [ADMIN]         │ │
      │     │         │              │  └─────────────────────────────────────┘ │
      │     │         │              │  ┌─────────────────────────────────────┐ │
      │     │         │              │  │           BILLING                   │ │
      │     │         │              │  │  ○ Pay Bill         [ADMIN,PATIENT] │ │
      │     │         │              │  │  ○ Get Bill by ID   [ADMIN,PATIENT] │ │
      │     │         │              │  │  ○ Bill for Appt    [ALL]           │ │
      │     │         │              │  └─────────────────────────────────────┘ │
      └─────┴─────────┘              └──────────────────────────────────────────┘
```

---

## 9. API Reference

### Auth Service — `POST /auth/**`

| Method | Endpoint | Body | Auth | Description |
|---|---|---|---|---|
| POST | `/auth/register` | `{username, email, password, role, securityCode}` | None | Register user |
| POST | `/auth/login` | `{identity, password}` | None | Login, get JWT |

---

### Patient Service — `GET/POST/PUT/DELETE /patients/**`

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| POST | `/patients` | ADMIN, PATIENT | Create patient |
| GET | `/patients/{id}` | ALL | Get patient by ID |
| GET | `/patients` | ADMIN | Get all patients |
| PUT | `/patients/{id}` | ADMIN, PATIENT | Update patient |
| DELETE | `/patients/{id}` | ADMIN | Soft delete patient |

---

### Doctor Service — `GET/POST/PUT/DELETE /doctors/**`

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| POST | `/doctors` | ADMIN | Add doctor |
| GET | `/doctors/{id}` | ALL | Get doctor by ID |
| GET | `/doctors` | ALL | Get all doctors |
| GET | `/doctors/available` | ALL | Get available doctors |
| GET | `/doctors/specialization/{type}` | ALL | Filter by specialization |
| PUT | `/doctors/{id}` | ADMIN, DOCTOR | Update doctor |
| DELETE | `/doctors/{id}` | ADMIN | Soft delete doctor |

---

### Appointment Service — `/appointments/**`

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| POST | `/appointments` | ADMIN, PATIENT | Book appointment |
| GET | `/appointments/{id}` | ALL | Get appointment |
| GET | `/appointments/patient/{patientId}` | ADMIN, PATIENT | By patient |
| GET | `/appointments/doctor/{doctorId}` | ADMIN, DOCTOR | By doctor |
| PUT | `/appointments/{id}/cancel` | ALL | Cancel appointment |
| PUT | `/appointments/{id}/complete` | ADMIN, DOCTOR | Complete appointment |

---

### Medical Record Service — `/records/**`

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| POST | `/records` | DOCTOR | Create medical record |
| GET | `/records/{id}` | ALL | Get record |
| GET | `/records/patient/{patientId}` | ALL | Patient history |
| DELETE | `/records/{id}` | ADMIN | Soft delete record |

---

### Billing Service — `/billing/**`

| Method | Endpoint | Roles | Description |
|---|---|---|---|
| POST | `/billing/pay` | ADMIN, PATIENT | Process payment |
| GET | `/billing/{id}` | ADMIN, PATIENT | Get bill by ID |
| GET | `/billing/appointment/{appointmentId}` | ALL | Bill for appointment |

---

### Standard API Response Format

All endpoints return a unified `ApiResponse<T>` wrapper:

```json
{
  "success": true,
  "message": "Appointment is fetched successfully!",
  "data": {
    "id": 1,
    "patientId": 10,
    "doctorId": 3,
    "appointmentDate": "2026-05-10T10:00:00",
    "status": "BOOKED",
    "reason": "Routine check-up"
  }
}
```

---

## 10. Security & Authentication Flow

### JWT Token Structure
```
Header:  { alg: "HS256", typ: "JWT" }
Payload: { sub: "username", role: "ROLE_PATIENT", iat: ..., exp: ... }
Signature: HMAC-SHA256(base64(header) + "." + base64(payload), secretKey)
```

### Shared Secret Key
```
5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```
This key is shared across: `auth-service`, `api-gateway`, `patient-service`, `doctor-service`, `appointment-service`, `billing-service`, `medical-record-service`.

### Authentication Flow

```
1. CLIENT → POST /auth/login { identity: "john", password: "pass" }

2. API GATEWAY → Passes through (login is in open routes list)

3. AUTH SERVICE:
   a. AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)
   b. CustomUserDetailsService.loadUserByUsername(identity)
      → queries users table by username OR email
   c. BCrypt.matches(rawPassword, storedHash)
   d. On success → fetch user → get role
   e. JwtUtil.generateToken(username, "ROLE_PATIENT")
   f. Return JWT token

4. CLIENT stores token

5. CLIENT → GET /patients/1
   Authorization: Bearer eyJhbGc...

6. API GATEWAY (AuthenticationFilter):
   a. RouteValidator.isSecured → true (not in open list)
   b. Extract Authorization header
   c. JwtUtil.validateToken(token) → valid
   d. Inject: X-Internal-Secret: HealthProject2026
   e. Forward to PATIENT-SERVICE via Eureka lb://PATIENT-SERVICE

7. PATIENT SERVICE (JwtFilter):
   a. Extract JWT from Authorization header
   b. Validate JWT again (defence in depth)
   c. Set SecurityContext with username + authorities
   d. @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')") → PASSES
   e. Execute business logic
   f. Return response
```

### Security Architecture — Defence in Depth
```
Layer 1: API Gateway JwtUtil.validateToken()    → Validates token signature
Layer 2: Per-service JwtFilter                  → Re-validates + sets SecurityContext
Layer 3: @PreAuthorize annotations              → Role-based method access control
Layer 4: X-Internal-Secret header              → Ensures requests came through gateway
```

---

## 11. Inter-Service Communication

The following Feign client relationships exist:

```
Appointment Service ──Feign──► Patient Service  (validate patient exists)
Appointment Service ──Feign──► Doctor Service   (validate doctor exists)

Medical Record Svc  ──Feign──► Patient Service  (validate patient exists)
Medical Record Svc  ──Feign──► Doctor Service   (validate doctor exists)

Billing Service     ──Feign──► Appointment Svc  (fetch appointment details)
Billing Service     ──Feign──► Patient Service  (validate patient exists)
```

### Feign Client Configuration
All Feign clients use `FeignClientConfig` which injects the `X-Internal-Secret` header on every inter-service HTTP request, ensuring downstream services can trust that the call is internal.

```java
// Example Feign call flow:
AppointmentServiceImpl.book()
  → patientClient.getPatientById(patientId)    // validates patient exists
  → doctorClient.getDoctorById(doctorId)       // validates doctor exists
  → save appointment
```

---

## 12. Resilience & Fault Tolerance

**Resilience4j Circuit Breakers** are applied to all operations that depend on external service calls:

| Service | Circuit Breaker Name | Fallback Method |
|---|---|---|
| Appointment Service | `appointmentService` | `appointmentFallback` |
| Medical Record Service | `medicalRecordService` | `medicalRecordFallback` |
| Billing Service | `billingService` | `billingFallback` |

### Circuit Breaker Behavior
```
Normal State (CLOSED):
  Request → Downstream Service → Response ✓

When failures exceed threshold (OPEN):
  Request → Circuit Breaker → Fallback Response (503 Service Unavailable)
  Fallback: { success: false, message: "Service is currently busy or down. Please try again later." }

After timeout (HALF-OPEN):
  Limited requests → Test if service recovered → CLOSE or stay OPEN
```

This prevents cascade failures — if Patient Service goes down, Appointment Service degrades gracefully rather than hanging all requests.

---

## 13. Data Flow — End-to-End Scenarios

### Scenario 1: Patient Books an Appointment

```
1. Patient registers:  POST /auth/register  { role: "PATIENT" }
                       → JWT issued

2. Patient logs in:    POST /auth/login
                       → JWT Token returned

3. Create profile:     POST /patients  { name, email, phone, age, gender, address }
                       + Authorization: Bearer <JWT>
                       → Patient record saved (id: 5)

4. View doctors:       GET /doctors/available
                       → List of available doctors returned (id: 2)

5. Book appointment:   POST /appointments
                       { patientId: 5, doctorId: 2, appointmentDate: "2026-05-10T10:00", reason: "Fever" }
                       → Appointment Service:
                            ├── Feign → Patient Service: validate patient 5 ✓
                            ├── Feign → Doctor Service:  validate doctor 2 ✓
                            └── Save appointment { status: BOOKED, id: 12 }

6. Pay bill:           POST /billing/pay
                       { appointmentId: 12, patientId: 5, amount: 500, paymentMethod: "UPI" }
                       → Billing Service:
                            ├── Feign → Appointment Service: fetch appointment 12
                            ├── Verify appointment.patientId == 5 ✓ (fraud check)
                            ├── Feign → Patient Service: validate patient 5 ✓
                            └── Save billing { paymentStatus: PAID }
```

### Scenario 2: Doctor Completes Appointment & Creates Medical Record

```
1. Doctor logs in:     POST /auth/login { identity: "dr.smith", password: "..." }
                       → JWT with ROLE_DOCTOR

2. View appointments:  GET /appointments/doctor/2
                       → List of appointments for doctor 2

3. Complete appt:      PUT /appointments/12/complete
                       → Status: BOOKED → COMPLETED

4. Create record:      POST /records
                       { patientId: 5, doctorId: 2,
                         diagnosis: "Viral fever", prescription: "Paracetamol 500mg" }
                       → Medical Record Service:
                            ├── Feign → Patient Service: validate patient 5 ✓
                            ├── Feign → Doctor Service:  validate doctor 2 ✓
                            └── Save medical record (soft-deletable)
```

---

## 14. Service Port Registry

| Service | Port | Database |
|---|---|---|
| **Eureka Server** | 8761 | — |
| **API Gateway** | 8080 | — |
| **Auth Service** | 9000 | `authDB` |
| **Patient Service** | 9001 | `patientDB` |
| **Doctor Service** | 9002 | `doctorDB` |
| **Appointment Service** | 9003 | `appointmentDB` |
| **Billing Service** | 9004 | `billingDB` |
| **Medical Record Service** | 9006 | `medicalRecordsDB` |

---

## 15. Setup & Running the Project

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+ (running locally on port 5432)
- IDE: IntelliJ IDEA (recommended)

### Step 1: Database Setup

Create the following PostgreSQL databases:

```sql
CREATE DATABASE "authDB";
CREATE DATABASE "patientDB";
CREATE DATABASE "doctorDB";
CREATE DATABASE "appointmentDB";
CREATE DATABASE "billingDB";
CREATE DATABASE "medicalRecordsDB";
```

Default credentials used across all services:
```
username: postgres
password: root
```

### Step 2: Start Services in Order

Services must be started in this exact order due to dependencies:

```
1. eureka-server         ← Must be first; all others register here
2. api-gateway           ← Depends on Eureka
3. auth-service          ← No inter-service Feign calls
4. patient-service       ← No inter-service Feign calls
5. doctor-service        ← No inter-service Feign calls
6. appointment-service   ← Feign: patient + doctor
7. billing-service       ← Feign: appointment + patient
8. medical-record-service← Feign: patient + doctor
```

### Step 3: Run Each Service

```bash
cd eureka-server
./mvnw spring-boot:run

cd ../api-gateway
./mvnw spring-boot:run

# ... repeat for all services
```

### Step 4: Verify Eureka Dashboard

Open `http://localhost:8761` — all 6 services should appear as registered instances.

### Step 5: Test via API Gateway

All requests go through port `8080`:

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@test.com","password":"pass123","role":"PATIENT"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identity":"alice","password":"pass123"}'

# Use the token from login response
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Create patient profile
curl -X POST http://localhost:8080/patients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@test.com","phone":"9876543210","age":28,"gender":"Female","address":"Pune"}'
```

---

## 16. Design Patterns Used

| Pattern | Where Used | Purpose |
|---|---|---|
| **Service Layer Pattern** | All services | Separation of business logic from controllers |
| **Repository Pattern** | All services | Data access abstraction via Spring Data JPA |
| **DTO Pattern** | All services | Decouple API contracts from internal entities |
| **Mapper Pattern** (MapStruct) | All services | Type-safe compile-time DTO ↔ Entity conversion |
| **Gateway Pattern** | API Gateway | Single entry point for all client requests |
| **Circuit Breaker Pattern** | Appointment, Billing, Medical | Fault tolerance for inter-service calls |
| **Soft Delete Pattern** | Patient, Doctor, Medical Record | Data recovery; never hard-delete records |
| **Facade Pattern** | Service interfaces | Hide implementation complexity behind interfaces |
| **State Machine** | Appointment status | Enforce valid transitions (BOOKED→CANCELLED/COMPLETED) |
| **Decorator Pattern** | JwtFilter, AuthFilter | Add cross-cutting security concerns to requests |
| **Service Discovery** | Eureka + Feign | Dynamic service lookup without hardcoded URLs |

---

## 17. Roles & Permissions Matrix

| Operation | ADMIN | DOCTOR | PATIENT |
|---|:---:|:---:|:---:|
| Register | ✅ (with adminCode) | ✅ (with doctorCode) | ✅ |
| Login | ✅ | ✅ | ✅ |
| **PATIENTS** | | | |
| Create Patient | ✅ | ❌ | ✅ |
| Get Patient | ✅ | ✅ | ✅ |
| Get All Patients | ✅ | ❌ | ❌ |
| Update Patient | ✅ | ❌ | ✅ |
| Delete Patient | ✅ | ❌ | ❌ |
| **DOCTORS** | | | |
| Add Doctor | ✅ | ❌ | ❌ |
| Get Doctor | ✅ | ✅ | ✅ |
| Get All / Available | ✅ | ✅ | ✅ |
| Filter by Spec. | ✅ | ✅ | ✅ |
| Update Doctor | ✅ | ✅ | ❌ |
| Delete Doctor | ✅ | ❌ | ❌ |
| **APPOINTMENTS** | | | |
| Book | ✅ | ❌ | ✅ |
| View | ✅ | ✅ | ✅ |
| By Patient ID | ✅ | ❌ | ✅ |
| By Doctor ID | ✅ | ✅ | ❌ |
| Cancel | ✅ | ✅ | ✅ |
| Complete | ✅ | ✅ | ❌ |
| **MEDICAL RECORDS** | | | |
| Create Record | ❌ | ✅ | ❌ |
| View Record | ✅ | ✅ | ✅ |
| Patient History | ✅ | ✅ | ✅ |
| Delete Record | ✅ | ❌ | ❌ |
| **BILLING** | | | |
| Pay Bill | ✅ | ❌ | ✅ |
| Get Bill | ✅ | ❌ | ✅ |
| Bill for Appointment | ✅ | ✅ | ✅ |

---

## 18. Project Structure

```
Chatgpt-HealthCare-Project/
│
├── eureka-server/
│   └── src/main/java/com/healthcare/eureka_server/
│       └── EurekaServerApplication.java        (@EnableEurekaServer)
│
├── api-gateway/
│   └── src/main/java/com/healthcare/api_gateway/
│       ├── ApiGatewayApplication.java
│       ├── filter/
│       │   ├── AuthenticationFilter.java       (JWT validation at gateway)
│       │   └── RouteValidator.java             (Open vs secured routes)
│       └── util/JwtUtil.java
│
├── auth-service/
│   └── src/main/java/com/healthcare/auth_service/
│       ├── controller/AuthController.java
│       ├── service/AuthService.java + impl/AuthServiceImpl.java
│       ├── entity/User.java + Role.java
│       ├── dto/AuthRequestDTO, AuthResponseDTO, LoginRequestDTO
│       ├── config/SecurityConfig, JwtFilter, CustomUserDetailsService
│       └── util/JwtUtil.java
│
├── patient-service/
│   └── src/main/java/com/healthcare/patient_service/
│       ├── controller/PatientController.java
│       ├── service/PatientService.java + impl/
│       ├── entity/Patient.java
│       ├── dto/, mapper/, repository/
│       ├── config/SecurityConfig.java + JwtFilter.java
│       └── exception/GlobalExceptionHandler, PatientNotFoundException
│
├── doctor-service/
│   └── src/main/java/com/healthcare/doctor_service/
│       ├── controller/DoctorController.java
│       ├── service/DoctorService.java + impl/
│       ├── entity/Doctor.java
│       ├── dto/, mapper/, repository/
│       └── config/, exception/
│
├── appointment-service/
│   └── src/main/java/com/healthcare/appointment_service/
│       ├── controller/AppointmentController.java  (@CircuitBreaker)
│       ├── service/AppointmentService.java + impl/
│       ├── entity/Appointment.java
│       ├── dto/AppointmentStatus.java              (BOOKED/CANCELLED/COMPLETED)
│       ├── client/PatientClient.java + DoctorClient.java  (Feign)
│       └── config/, exception/
│
├── medical-record-service/
│   └── src/main/java/com/healthcare/medical_record_service/
│       ├── controller/MedicalRecordController.java  (@CircuitBreaker)
│       ├── service/MedicalRecordService.java + impl/
│       ├── entity/MedicalRecord.java
│       ├── client/PatientClient.java + DoctorClient.java  (Feign)
│       └── config/, exception/
│
└── billing-service/
    └── src/main/java/com/healthcare/billing_service/
        ├── controller/BillingController.java        (@CircuitBreaker)
        ├── service/BillingService.java + impl/
        ├── entity/Billing.java + PaymentStatus + PaymentMethod
        ├── client/AppointmentClient.java + PatientClient.java  (Feign)
        └── config/, exception/
```

---

## Additional Notes

### Why `BigDecimal` for Amount?
The `Billing.amount` field uses `BigDecimal` instead of `double`. In Java, `double` suffers from IEEE 754 floating-point rounding errors (e.g., `0.1 + 0.2 ≠ 0.3`). For financial values, `BigDecimal` ensures exact precision — critical for billing systems.

### Why Soft Deletes?
Patient, Doctor, and Medical Record entities use a boolean `isDeleted` flag rather than hard database deletion. This supports:
- **Audit trails** — historical data remains intact
- **Data recovery** — accidental deletions can be reversed
- **Referential integrity** — appointments and records still refer to valid IDs

### Internal Secret Header
The `X-Internal-Secret: HealthProject2026` header is injected by the API Gateway after JWT validation. Services can optionally check for this header to reject any direct calls that bypass the gateway — providing an additional trust layer for internal communication.

### JWT Validation at Two Layers
JWT is validated at both the **API Gateway** and again within each **microservice's JwtFilter**. This is a deliberate "defence in depth" approach. If someone were to bypass the gateway and call a service directly, the service-level filter would still reject unauthorized requests.

---

*Built with ❤️ using Spring Boot Microservices — May 2026*
