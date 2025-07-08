# Vehicle OEM Server

A Spring Boot 3 application for managing digital keys in a Vehicle OEM ecosystem.

## Overview

This application provides APIs for:
- Tracking digital keys for vehicles
- Managing key lifecycle (suspend, resume, terminate)
- Vehicle registration and management
- Owner account management
- Integration with external services (Device OEM, Key Tracking Service, Vehicle Telematics)

## Architecture

The application follows a layered architecture:
- **Controllers**: REST API endpoints
- **Services**: Business logic layer
- **Repositories**: Data access layer
- **Models**: JPA entities
- **DTOs**: Data transfer objects
- **Clients**: Feign clients for external service integration
- **Security**: Cryptographic services using BouncyCastle

## Key Features

- **Digital Key Management**: Track and manage digital keys with full lifecycle support
- **Vehicle Management**: Register vehicles and manage subscriptions
- **Owner Account Management**: Create and manage vehicle owner accounts
- **External Service Integration**: Seamless integration with Device OEM, KTS, and Telematics services
- **Security**: Cryptographic operations using BouncyCastle for ECDSA and ECIES
- **Resilience**: Circuit breaker and retry patterns using Resilience4j
- **Async Processing**: Asynchronous event notifications

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **Spring Cloud OpenFeign**
- **PostgreSQL** (All environments)
- **Flyway** (Database migrations)
- **BouncyCastle** (Cryptography)
- **Resilience4j** (Circuit Breaker, Retry)
- **Maven**
- **Taskfile** (Task automation)
- **Podman** (Container management)

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper `./mvnw`)
- Podman (for database and mock services)
- Task (optional, for task automation) - [Installation Guide](https://taskfile.dev/installation/)

## Running the Application

### Quick Start with Taskfile (Recommended)
```bash
# Install Task (if not already installed)
# macOS: brew install go-task/tap/go-task
# Linux: sh -c "$(curl --location https://taskfile.dev/install.sh)" -- -d

# Start all services (database, mocks, application)
task start

# Stop all services
task stop

# Show service status
task status

# Show all available tasks
task --list
```

### Quick Start with Scripts
```bash
# Start all services
./start-services.sh

# Stop all services
./stop-services.sh

# Start in development mode
./start-services.sh dev
```

### Manual Development Mode (PostgreSQL)
```bash
# Start database and mocks first
task db:start
task mocks:start

# Run migrations
task db:migrate

# Start application
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Database Management
```bash
# Start database only
task db:start

# Run migrations
task db:migrate

# Clean database and re-run migrations
task db:clean

# Stop database
task db:stop
```

### Mock Services
```bash
# Start all mock services
task mocks:start

# Stop all mock services
task mocks:stop
```

## Configuration

### Database Setup

For production PostgreSQL setup:
```sql
CREATE DATABASE vehicle_oem_db;
CREATE USER vehicle_oem_user WITH PASSWORD 'vehicle_oem_pass';
GRANT ALL PRIVILEGES ON DATABASE vehicle_oem_db TO vehicle_oem_user;
```

### Environment Variables

- `DB_USERNAME`: Database username (default: vehicle_oem_user)
- `DB_PASSWORD`: Database password (default: vehicle_oem_pass)
- `ADMIN_PASSWORD`: Admin password for basic auth (default: admin123)
- `DEVICE_OEM_SERVER_URL`: Device OEM server URL (default: http://localhost:8081)
- `KTS_SERVER_URL`: Key Tracking Service URL (default: http://localhost:8082)
- `VEHICLE_TELEMATICS_URL`: Vehicle Telematics URL (default: http://localhost:8083)

## API Endpoints

### Digital Key Management
- `POST /api/v1/keys/track` - Track a new digital key
- `POST /api/v1/keys/manage` - Manage key lifecycle (suspend/resume/terminate)

### Key Sharing
- `POST /api/v1/key-sharing/share` - Share keys with friends
- `POST /api/v1/key-sharing/accept/{code}` - Accept key invitations
- `PUT /api/v1/key-sharing/permissions/{keyId}` - Update permissions
- `PUT /api/v1/key-sharing/restrictions/{keyId}` - Set usage restrictions

### Vehicle Management
- `POST /api/v1/vehicles` - Create a new vehicle (with validation)
- `POST /api/v1/vehicles/test-friendly` - Create a new vehicle (test-friendly, relaxed validation)
- `POST /api/v1/vehicles/{vin}/initialize-pairing` - Initialize vehicle pairing
- `PUT /api/v1/vehicles/{vin}/subscription` - Update vehicle subscription
- `PUT /api/v1/vehicles/{vin}/subscription-tier` - Update vehicle subscription tier (BASIC/PREMIUM/ENTERPRISE)

### Vehicle Pairing
- `POST /api/v1/pairing/initiate` - Start pairing process
- `POST /api/v1/pairing/complete` - Complete pairing
- `POST /api/v1/pairing/revoke/{sessionId}` - Revoke pairing

### Owner Account Management
- `POST /api/v1/accounts` - Create a new owner account
- `GET /api/v1/accounts/{accountId}` - Get owner account by ID
- `GET /api/v1/accounts/by-email/{email}` - Get owner account by email
- `POST /api/v1/accounts/{accountId}/verify-email` - Verify owner email (required for digital key operations)

## Authentication

The application uses HTTP Basic Authentication. Default credentials:
- Username: `admin`
- Password: `admin123` (configurable via `ADMIN_PASSWORD` environment variable)

## Testing

### Unit Tests
Run tests with:
```bash
mvn test
```

### API Testing
Use the provided HTTP test file:
```bash
# Open api-tests.http in your IDE (IntelliJ IDEA, VS Code with REST Client extension)
# Or use the test script
./test-endpoints.sh
```

### Postman Collection
Import `vehicle-oem-api.postman_collection.json` into Postman for comprehensive API testing.

## Monitoring

The application exposes actuator endpoints for monitoring:
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

## Documentation

Detailed documentation is available in the `docs/` directory:
- [Documentation Index](docs/INDEX.md) - Complete documentation overview
- [Deployment Guide](docs/DEPLOYMENT_GUIDE.md) - Podman and Docker deployment
- [API Testing Guide](docs/API_TESTING_GUIDE.md) - HTTP API testing instructions
- [Business Implementation Summary](docs/BUSINESS_IMPLEMENTATION_SUMMARY.md)
- [Test Documentation](docs/TEST_DOCUMENTATION.md)
- [Project Summary](docs/PROJECT_SUMMARY.md)
- [Database Configuration](docs/DATABASE_ISSUE_RESOLUTION_SUCCESS.md)

## External Service Integration

The application integrates with three external services:

1. **Device OEM Server**: For event notifications and key validation
2. **Key Tracking Service (KTS)**: For key registration and status updates
3. **Vehicle Telematics**: For sending commands to vehicles

Configure the URLs for these services in `application.yml` or via environment variables.

## Security Features

- ECDSA digital signatures using secp256r1 curve
- ECIES encryption/decryption
- Certificate generation and cross-signing
- Secure pairing password generation
- BouncyCastle cryptographic provider

## Resilience Features

- Circuit breaker pattern for external service calls
- Retry mechanism with exponential backoff
- Async processing for non-critical operations
- Graceful degradation on service failures

## Project Files

### Scripts
- `start-services.sh` - Start all services (database, mocks, application)
- `stop-services.sh` - Stop all services and cleanup
- `test-endpoints.sh` - Automated API testing script
- `build-and-run.sh` - Build and run application

### Configuration Files
- `Taskfile.yml` - Task automation configuration
- `api-tests.http` - HTTP client test file
- `src/main/resources/db/migration/` - Flyway database migration scripts

### Application Profiles
- `application.yml` - Default configuration (PostgreSQL with Flyway)
- `application-dev.yml` - Development configuration (PostgreSQL with enhanced logging)
- `application-test.properties` - Test configuration (Embedded PostgreSQL)
