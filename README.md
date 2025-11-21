# Hotel Management Platform

College Hotel Management MVP (Cash-only) - Version 1.1

A complete Java Spring Boot application for managing hotel operations including reservations, stays, folios, cash payments, housekeeping, and reporting.

## Features

- **Desktop GUI**: Native JavaFX desktop application with role-based interfaces
- **REST API**: Complete REST API for all operations (can be used independently)
- **Authentication & Authorization**: JWT-based authentication with role-based access control (ADMIN, FRONTDESK, HOUSEKEEPING)
- **Reservation Management**: Create, modify, and cancel reservations with availability checks
- **Stay Management**: Check-in and check-out functionality with automatic folio creation
- **Folio & Payments**: Track charges and record cash payments
- **Housekeeping**: Manage housekeeping tasks and room status
- **Reporting**: Daily occupancy and revenue reports
- **Audit Logging**: Complete audit trail of all operations

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **Data Access**: Spring JDBC (JdbcTemplate)
- **Migrations**: Flyway
- **Security**: Spring Security with JWT
- **Desktop GUI**: JavaFX 21
- **Testing**: JUnit 5, Testcontainers
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use Testcontainers for tests)
- Docker (optional, for Testcontainers)

## Setup Instructions

### 1. Database Configuration

Create a PostgreSQL database and configure the connection:

```bash
# Create database
createdb hotel_db

# Or using psql
psql -U postgres
CREATE DATABASE hotel_db;
```

### 2. Environment Variables

Copy `.env.example` and set your database credentials:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=hotel_db
export DB_USER=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key-here-minimum-32-characters-long-for-security
export APP_PORT=8080
export SPRING_PROFILES_ACTIVE=dev
```

Or create a `.env` file and source it:

```bash
source .env
```

### 3. Build and Run

#### Option A: Desktop GUI Application (Recommended)

**One-command launch (Windows):**
```powershell
.\run-desktop.ps1
```

**One-command launch (Linux/Mac):**
```bash
chmod +x run-desktop.sh
./run-desktop.sh
```

The launcher script automatically loads environment variables and starts the application!

**Manual steps (if needed):**
```bash
# Build the project (first time only)
mvn clean install

# Set environment variables and run
. .\set-env.ps1  # Windows PowerShell
mvn exec:java -Dexec.mainClass="com.hotel.desktop.DesktopLauncher"
```

The desktop GUI will launch with a login screen. Use the default credentials:
- Email: `admin@example.com`
- Password: `admin123`

#### Option B: REST API Server Only

```bash
# Build the project
mvn clean install

# Set environment variables
. .\set-env.ps1  # Windows PowerShell

# Run the REST API server
mvn spring-boot:run
```

The REST API will be available on `http://localhost:8080` (or the port specified in `APP_PORT`).

Flyway migrations will run automatically on startup, creating the database schema and seeding initial data.

## Default Credentials

The following users are created by the seed migration:

| Email | Password | Role |
|-------|----------|------|
| admin@example.com | admin123 | ADMIN |
| frontdesk@example.com | frontdesk123 | FRONTDESK |
| housekeeping@example.com | housekeeping123 | HOUSEKEEPING |

**⚠️ Important**: Change these passwords in production!

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/register` - Register new user (ADMIN only)

### Rooms
- `GET /api/rooms` - List all rooms
- `POST /api/room-types` - Create room type (ADMIN)
- `POST /api/rooms` - Create room (ADMIN)
- `PATCH /api/rooms/:id/status` - Update room status

### Reservations
- `POST /api/reservations` - Create reservation
- `GET /api/reservations` - List reservations (with filters)
- `GET /api/reservations/:id` - Get reservation
- `PATCH /api/reservations/:id` - Update reservation
- `PATCH /api/reservations/:id/cancel` - Cancel reservation

### Stays
- `POST /api/stays/checkin` - Check in reservation
- `POST /api/stays/:id/checkout` - Check out stay

### Folios & Payments
- `GET /api/folios/:id` - Get folio details
- `POST /api/folios/:id/line-items` - Add line item
- `POST /api/folios/:id/payments` - Record cash payment

### Housekeeping
- `GET /api/housekeeping/tasks` - List tasks
- `POST /api/housekeeping/tasks` - Create task
- `PATCH /api/housekeeping/tasks/:id` - Update task

### Reports
- `GET /api/reports/daily?date=YYYY-MM-DD` - Get daily report

### Audit
- `GET /api/audit` - Query audit logs (ADMIN only)

See `sample_curl_commands.txt` for complete API examples.

## Running Tests

### Unit Tests
```bash
mvn test
```

### Integration Tests
Integration tests use Testcontainers and will automatically start a PostgreSQL container:

```bash
mvn test
```

## Smoke Tests

Run the smoke test script to verify the complete flow:

```bash
# Make script executable
chmod +x smoke-test.sh

# Run smoke test
./smoke-test.sh

# Or with custom base URL
BASE_URL=http://localhost:8080 ./smoke-test.sh
```

The smoke test verifies:
1. Login as admin
2. Create reservation
3. Check in
4. Add line item
5. Record cash payment
6. Get daily report

## Project Structure

```
src/
├── main/
│   ├── java/com/hotel/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access (JdbcTemplate)
│   │   ├── model/          # Domain models
│   │   ├── dto/            # Data transfer objects
│   │   ├── security/       # Security configuration
│   │   └── exception/      # Exception handlers
│   └── resources/
│       ├── db/migration/   # Flyway migrations
│       └── application.yml  # Application configuration
└── test/
    └── java/com/hotel/     # Tests
```

## Database Schema

The application uses the following main tables:
- `users` - System users
- `room_types` - Room type definitions
- `rooms` - Physical rooms
- `reservations` - Booking reservations
- `stays` - Active stays
- `folios` - Guest folios
- `folio_line_items` - Charges on folios
- `payments` - Cash payments
- `housekeeping_tasks` - Housekeeping tasks
- `audit_logs` - Audit trail

## Security

- All endpoints except `/api/auth/login` require JWT authentication
- JWT tokens are passed in the `Authorization: Bearer <token>` header
- Role-based access control:
  - `ADMIN`: Full access
  - `FRONTDESK`: Reservations, stays, folios
  - `HOUSEKEEPING`: Room status, housekeeping tasks

## Transaction Management

Critical operations use `@Transactional` to ensure data consistency:
- Reservation creation with availability checks
- Check-in (creates stay, folio, updates room status)
- Check-out (finalizes stay, updates room status)

## Concurrency Control

Availability checks use `SELECT FOR UPDATE` to prevent race conditions when multiple users try to book the same room type simultaneously.

## Error Handling

The application includes a global exception handler that returns appropriate HTTP status codes:
- `400 Bad Request` - Validation errors
- `401 Unauthorized` - Missing or invalid JWT
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Booking conflicts (no availability)

## Development

### Adding New Features

1. Create domain model in `model/` package
2. Create repository in `repository/` package using JdbcTemplate
3. Create service in `service/` package with business logic
4. Create controller in `controller/` package for REST endpoints
5. Add DTOs in `dto/` package for request/response objects
6. Write tests in `test/` package

### Database Migrations

Add new migrations in `src/main/resources/db/migration/` following Flyway naming convention:
- `V{version}__{description}.sql`

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in environment variables
- Ensure database exists

### Migration Errors
- Check Flyway logs in application output
- Verify migration SQL syntax
- Ensure database user has necessary permissions

### JWT Token Issues
- Verify `JWT_SECRET` is set and at least 32 characters
- Check token expiration (default: 24 hours)
- Ensure token is passed in `Authorization: Bearer <token>` header

## License

This project is for educational purposes.

## Support

For issues or questions, please refer to the project documentation or contact the development team.

