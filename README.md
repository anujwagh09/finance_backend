# Finance Backend API

A RESTful backend for a finance dashboard system built with Spring Boot.
Supports role-based access control, financial record management, and dashboard analytics.

---

## Tech Stack

- Java 21
- Spring Boot 3.2.5
- Spring Security + JWT (jjwt 0.11.5)
- Spring Data JPA + Hibernate
- H2 (development) / PostgreSQL (production)
- Lombok
- Maven

---

## Project Structure

```
src/main/java/com/fintech/financebackend/
├── config/          SecurityConfig.java
├── controller/      AuthController, UserController, RecordController, DashboardController
├── dto/
│   ├── request/     LoginRequest, RegisterRequest, CreateRecordRequest, etc.
│   └── response/    AuthResponse, RecordResponse, SummaryResponse, etc.
├── enums/           Role, RecordType, UserStatus
├── exception/       GlobalExceptionHandler + custom exceptions
├── model/           User, FinancialRecord
├── repository/      UserRepository, FinancialRecordRepository
├── security/        JwtUtil, JwtFilter, UserDetailsServiceImpl
└── service/         AuthService, UserService, RecordService, DashboardService
```

---

## Setup and Running

### Development (H2 in-memory)

```bash
git clone <your-repo-url>
cd finance_backend
mvn spring-boot:run
```

App runs at `http://localhost:8080`

H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password: (empty)

### Production (PostgreSQL)

Create a Postgres database and run with prod profile:

```bash
export DB_USER=your_db_user
export DB_PASS=your_db_password
mvn spring-boot:run -Dspring.profiles.active=prod
```

---

## Seeded Users

Three users are auto-created on startup for testing:

| Email | Password | Role |
|---|---|---|
| admin@finance.com | password123 | ADMIN |
| analyst@finance.com | password123 | ANALYST |
| viewer@finance.com | password123 | VIEWER |

---

## API Endpoints

### Auth (public)

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/auth/register | Create new user |
| POST | /api/auth/login | Login and get JWT token |

### Users (ADMIN only)

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/users | List all users |
| PATCH | /api/users/{id}/role | Update user role |
| PATCH | /api/users/{id}/status | Activate or deactivate user |

### Records

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | /api/records | ALL | List records with optional filters |
| GET | /api/records/{id} | ALL | Get single record |
| POST | /api/records | ADMIN | Create record |
| PATCH | /api/records/{id} | ADMIN | Update record |
| DELETE | /api/records/{id} | ADMIN | Soft delete record |

**Query params for GET /api/records:**
- `type` — INCOME or EXPENSE
- `category` — filter by category name
- `from` — start date (YYYY-MM-DD)
- `to` — end date (YYYY-MM-DD)
- `page` — page number (default 0)
- `size` — page size (default 10)

### Dashboard

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | /api/dashboard/summary | ALL | Total income, expense, net balance |
| GET | /api/dashboard/categories | ALL | Breakdown by category |
| GET | /api/dashboard/trends | ANALYST, ADMIN | Monthly income and expense trends |
| GET | /api/dashboard/recent | ALL | Recent transactions (default 10) |

**Query params for GET /api/dashboard/recent:**
- `limit` — number of records to return (default 10)

### Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

Get the token from `/api/auth/login`.

---

## Role Permissions

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| View records | YES | YES | YES |
| View dashboard summary | YES | YES | YES |
| View monthly trends | NO | YES | YES |
| Create records | NO | NO | YES |
| Update records | NO | NO | YES |
| Delete records | NO | NO | YES |
| Manage users | NO | NO | YES |

---

## Example Requests

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@finance.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "email": "admin@finance.com",
  "role": "ADMIN"
}
```

### Create a record
```http
POST /api/records
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 50000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2024-03-01",
  "notes": "March salary"
}
```

### Filter records
```http
GET /api/records?type=EXPENSE&category=Food&from=2024-01-01&to=2024-12-31&page=0&size=10
Authorization: Bearer <token>
```

### Dashboard summary response
```json
{
  "totalIncome": 50000.00,
  "totalExpense": 3500.00,
  "netBalance": 46500.00,
  "totalRecords": 3
}
```

### Validation error response
```json
{
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "amount": "Amount must be positive",
    "date": "Date cannot be in the future"
  },
  "timestamp": "2024-03-01T10:00:00"
}
```

---

## Error Responses

All errors return consistent JSON via `GlobalExceptionHandler`:

| Status | Scenario |
|---|---|
| 400 | Validation failed — missing or invalid fields |
| 401 | Wrong email or password |
| 403 | Insufficient role permissions |
| 403 | Inactive user account |
| 404 | Record or user not found |
| 409 | Email already registered |
| 500 | Unexpected server error |

---

## Design Decisions and Assumptions

**Soft delete** — Records are never physically removed from the database. A `deleted_at`
timestamp marks them as deleted. All queries filter `WHERE deleted_at IS NULL`. This
preserves data history and allows recovery if needed.

**BigDecimal for money** — Amount fields use `BigDecimal` with precision 15 and scale 2.
Using `double` or `float` for financial data causes floating point precision errors which
is unacceptable in finance systems.

**Role stored as STRING** — `@Enumerated(EnumType.STRING)` stores readable values like
`ADMIN` in the database instead of integers (0, 1, 2). This makes the database readable
and safe against enum reordering bugs.

**JWT is stateless** — No server-side sessions. Every request carries the token in the
Authorization header. Token expiry is set to 24 hours (configurable in
`application.properties`).

**Inactive users are blocked at login** — `UserDetailsServiceImpl` throws `DisabledException`
for INACTIVE users before authentication completes. They cannot obtain a token even with
the correct password.

**Dashboard aggregations done in SQL** — Totals and trends are computed using SQL `SUM`
and `GROUP BY` in the repository layer, not by loading all records into Java and iterating.
This is significantly more efficient and scales with data volume.

**Monthly trends use native SQL** — JPQL does not support the `TO_CHAR` function which
is required for Postgres date-to-month formatting. A native query is used here. This is
a documented tradeoff — the query works correctly on PostgreSQL in production. For H2
in development, this endpoint requires testing on Postgres.

**Viewers can read all records** — No record ownership filter is applied. All authenticated
users see all non-deleted records. This matches the assignment's role definition where
VIEWER means read-only access to the dashboard, not ownership-based filtering.

**Two Spring profiles** — `dev` profile uses H2 in-memory database for zero-setup local
development. `prod` profile uses PostgreSQL with credentials injected from environment
variables for security. Switch profiles using `spring.profiles.active`.

**DTOs separate API from entities** — Controllers never return `@Entity` objects directly.
All responses use dedicated DTO classes. This prevents accidental exposure of internal
fields like `passwordHash` and decouples the API contract from the database schema.

**Partial update on records** — `UpdateRecordRequest` has all nullable fields. Only
non-null fields are applied in the service layer. This allows updating a single field
without resending the entire record.

---

## Configuration

`application.properties`:
```properties
spring.profiles.active=dev
jwt.secret=your-very-secret-key-must-be-at-least-32-characters-long
jwt.expiration=86400000
```

`application-dev.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:financedb
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.jpa.defer-datasource-initialization=true
```

`application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/financedb
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.jpa.hibernate.ddl-auto=validate
```
