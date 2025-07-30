# Taskfile Deployment Guide

This guide explains how to deploy and manage the Vehicle OEM Server using the new Taskfile-based approach, which replaces the previous Docker/Podman Compose setup.

## Overview

The new deployment approach uses:
- **Taskfile** for task automation and service orchestration
- **Bash scripts** for simple start/stop operations
- **Podman** for running PostgreSQL database and mock services
- **Flyway** for database migrations
- **Maven** for building and running the Spring Boot application

## Prerequisites

### Required
- Java 17 or higher
- Maven 3.6+ (or use included `./mvnw`)
- Podman (for containers)

### Optional
- Task CLI tool for enhanced automation

### Installing Task (Recommended)

**macOS:**
```bash
brew install go-task/tap/go-task
```

**Linux:**
```bash
sh -c "$(curl --location https://taskfile.dev/install.sh)" -- -d
```

**Windows:**
```bash
choco install go-task
```

## Quick Start

### Using Taskfile (Recommended)

```bash
# Start all services
task start

# Stop all services
task stop

# Show service status
task status

# Show all available tasks
task --list
```

### Using Scripts (Alternative)

```bash
# Start all services
./start-services.sh

# Stop all services
./stop-services.sh

# Start in development mode
./start-services.sh dev
```

## Available Tasks

### Service Management

| Task | Description |
|------|-------------|
| `task start` | Start all services (database, mocks, application) |
| `task stop` | Stop all services |
| `task restart` | Restart all services |
| `task status` | Show status of all services |
| `task clean` | Clean everything (containers, volumes, build artifacts) |

### Database Management

| Task | Description |
|------|-------------|
| `task db:start` | Start PostgreSQL database |
| `task db:stop` | Stop PostgreSQL database |
| `task db:wait` | Wait for database to be ready |
| `task db:migrate` | Run database migrations |
| `task db:clean` | Clean database and re-run migrations |

### Mock Services

| Task | Description |
|------|-------------|
| `task mocks:start` | Start all mock services |
| `task mocks:stop` | Stop all mock services |
| `task mock:device-oem:start` | Start Device OEM mock service |
| `task mock:kts:start` | Start KTS mock service |
| `task mock:telematics:start` | Start Telematics mock service |

### Application Management

| Task | Description |
|------|-------------|
| `task app:build` | Build the application |
| `task app:test` | Run all tests |
| `task app:start` | Start the Spring Boot application |
| `task app:start:dev` | Start application in development mode |

### Health and Monitoring

| Task | Description |
|------|-------------|
| `task health` | Check application health |
| `task logs` | Show application logs |

## Service Configuration

### Database (PostgreSQL)
- **Port:** 5532
- **Database:** vehicle_oem_db
- **Username:** vehicle_oem_user
- **Password:** vehicle_oem_pass

### Mock Services
- **Device OEM Mock:** http://localhost:8081
- **KTS Mock:** http://localhost:8082
- **Telematics Mock:** http://localhost:8083

### Application
- **Port:** 8088
- **Health Check:** http://localhost:8088/actuator/health

## Development Workflow

### 1. Initial Setup
```bash
# Clone the repository
git clone <repository-url>
cd vos-r3-springboot

# Start all services
task start
```

### 2. Development Mode
```bash
# Start services in development mode
task app:start:dev

# Or use the script
./start-services.sh dev
```

### 3. Running Tests
```bash
# Run all tests
task app:test

# Or use Maven directly
./mvnw test
```

### 4. Database Operations
```bash
# Reset database with fresh migrations
task db:clean

# Run only migrations
task db:migrate
```

## Troubleshooting

### Common Issues

**1. Podman not found**
```bash
# Install Podman
# macOS: brew install podman
# Linux: Follow official Podman installation guide
```

**2. Database connection issues**
```bash
# Check if database is running
task status

# Restart database
task db:stop
task db:start
```

**3. Port conflicts**
```bash
# Check what's using the ports
lsof -i :8088  # Application
lsof -i :5532  # Database
lsof -i :8081  # Device OEM Mock
```

**4. Migration failures**
```bash
# Clean and re-run migrations
task db:clean
```

### Logs and Debugging

```bash
# Check service status
task status

# View application health
task health

# Check container logs
podman logs vehicle-oem-server-postgres
podman logs vehicle-oem-server-device-oem-mock
```

## Migration from Docker Compose

If you're migrating from the old Docker Compose setup:

1. **Stop old services:**
   ```bash
   docker-compose down -v
   # or
   podman-compose down -v
   ```

2. **Remove old files:**
   - `docker-compose.yml` (removed)
   - `podman-compose.yml` (removed)
   - `run-podman.sh` (removed)
   - `stop-podman.sh` (removed)

3. **Start with new approach:**
   ```bash
   task start
   ```

## Environment Variables

The following environment variables are automatically set by the tasks:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5532/vehicle_oem_db
DB_USERNAME=vehicle_oem_user
DB_PASSWORD=vehicle_oem_pass
DEVICE_OEM_SERVER_URL=http://localhost:8081
KTS_SERVER_URL=http://localhost:8082
VEHICLE_TELEMATICS_URL=http://localhost:8083
```

## Production Deployment

For production deployment, consider:

1. **External Database:** Use a managed PostgreSQL service
2. **Environment Variables:** Set production values
3. **Security:** Configure proper authentication and SSL
4. **Monitoring:** Set up application monitoring
5. **Backup:** Configure database backups

Example production configuration:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/vehicle_oem_db
export DB_USERNAME=prod_user
export DB_PASSWORD=secure_password
export SPRING_PROFILES_ACTIVE=prod

task app:start
```

## Benefits of New Approach

1. **Simplified Management:** Single command to start/stop all services
2. **Better Isolation:** Each service runs in its own container
3. **Flexible Development:** Easy to start individual services
4. **Database Migrations:** Proper version control with Flyway
5. **No Compose Dependencies:** Works with just Podman
6. **Task Automation:** Consistent commands across environments
7. **Better Documentation:** Self-documenting tasks with descriptions
