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
- **PostgreSQL**
- **BouncyCastle** (Cryptography)
- **Resilience4j** (Circuit Breaker, Retry)
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Configuration

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE vehicle_oem_db;
CREATE USER vehicle_oem_user WITH PASSWORD 'vehicle_oem_pass';
GRANT ALL PRIVILEGES ON DATABASE vehicle_oem_db TO vehicle_oem_user;
```

2. Update `application.yml` with your database credentials if different from defaults.

### Environment Variables

- `DB_USERNAME`: Database username (default: vehicle_oem_user)
- `DB_PASSWORD`: Database password (default: vehicle_oem_pass)
- `ADMIN_PASSWORD`: Admin password for basic auth (default: admin123)
- `DEVICE_OEM_SERVER_URL`: Device OEM server URL (default: http://localhost:8081)
- `KTS_SERVER_URL`: Key Tracking Service URL (default: http://localhost:8082)
- `VEHICLE_TELEMATICS_URL`: Vehicle Telematics URL (default: http://localhost:8083)

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

### Swagger UI
The application includes comprehensive OpenAPI 3.0 documentation with Swagger UI:
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/api-docs

### API Endpoints

#### Digital Key Management
- `POST /api/v1/keys/track` - Track a new digital key
- `POST /api/v1/keys/manage` - Manage key lifecycle (suspend/resume/terminate)

#### Vehicle Management
- `POST /api/v1/vehicles` - Create a new vehicle (with validation)
- `POST /api/v1/vehicles/test-friendly` - Create a new vehicle (test-friendly, relaxed validation)
- `POST /api/v1/vehicles/{vin}/initialize-pairing` - Initialize vehicle pairing
- `PUT /api/v1/vehicles/{vin}/subscription` - Update vehicle subscription
- `PUT /api/v1/vehicles/{vin}/subscription-tier` - Update vehicle subscription tier (BASIC/PREMIUM/ENTERPRISE)

#### Owner Account Management
- `POST /api/v1/accounts` - Create a new owner account
- `GET /api/v1/accounts/{accountId}` - Get owner account by ID
- `GET /api/v1/accounts/by-email/{email}` - Get owner account by email
- `POST /api/v1/accounts/{accountId}/verify-email` - Verify owner email (required for digital key operations)

## Authentication

The application uses HTTP Basic Authentication. Default credentials:
- Username: `admin`
- Password: `admin123` (configurable via `ADMIN_PASSWORD` environment variable)

## Testing

Run tests with:
```bash
mvn test
```

## Monitoring

The application exposes actuator endpoints for monitoring:
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

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
