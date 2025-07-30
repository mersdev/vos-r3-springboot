# Deployment Guide

This guide covers deployment options for the Vehicle OEM Server using Podman and Docker.

## Prerequisites

### For Podman Deployment
- Podman 4.0+ installed
- podman-compose installed
- Java 17+ (for local development)

### For Docker Deployment
- Docker 20.0+ installed
- Docker Compose 2.0+ installed
- Java 17+ (for local development)

## Podman Deployment (Recommended)

### 1. Using Podman Compose

The project includes a `podman-compose.yml` file optimized for Podman:

```bash
# Start all services
podman-compose -f podman-compose.yml up -d

# View logs
podman-compose -f podman-compose.yml logs -f

# Stop services
podman-compose -f podman-compose.yml down
```

### 2. Using Individual Podman Commands

```bash
# Create network
podman network create vehicle-oem-network

# Start PostgreSQL
podman run -d \
  --name vehicle-oem-postgres \
  --network vehicle-oem-network \
  -p 5532:5432 \
  -e POSTGRES_DB=vehicle_oem_db \
  -e POSTGRES_USER=vehicle_oem_user \
  -e POSTGRES_PASSWORD=vehicle_oem_pass \
  -v postgres_data:/var/lib/postgresql/data:Z \
  --security-opt label=disable \
  --userns keep-id \
  docker.io/postgres:15

# Build application image
podman build -t vehicle-oem-server .

# Start application
podman run -d \
  --name vehicle-oem-server \
  --network vehicle-oem-network \
  -p 8088:8088 \
  -e DB_USERNAME=vehicle_oem_user \
  -e DB_PASSWORD=vehicle_oem_pass \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://vehicle-oem-postgres:5432/vehicle_oem_db \
  -e SPRING_PROFILES_ACTIVE=prod \
  --security-opt label=disable \
  --userns keep-id \
  vehicle-oem-server
```

### 3. Podman-Specific Configuration

The `podman-compose.yml` includes Podman-specific optimizations:

- `security_opt: label=disable` - Disables SELinux labeling for easier volume access
- `userns_mode: "keep-id"` - Maintains user ID mapping for file permissions
- Volume mounting with `:Z` flag for proper SELinux context

## Docker Deployment

### 1. Using Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### 2. Using Individual Docker Commands

```bash
# Create network
docker network create vehicle-oem-network

# Start PostgreSQL
docker run -d \
  --name vehicle-oem-postgres \
  --network vehicle-oem-network \
  -p 5532:5432 \
  -e POSTGRES_DB=vehicle_oem_db \
  -e POSTGRES_USER=vehicle_oem_user \
  -e POSTGRES_PASSWORD=vehicle_oem_pass \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15

# Build application image
docker build -t vehicle-oem-server .

# Start application
docker run -d \
  --name vehicle-oem-server \
  --network vehicle-oem-network \
  -p 8088:8088 \
  -e DB_USERNAME=vehicle_oem_user \
  -e DB_PASSWORD=vehicle_oem_pass \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://vehicle-oem-postgres:5432/vehicle_oem_db \
  -e SPRING_PROFILES_ACTIVE=prod \
  vehicle-oem-server
```

## Environment Configuration

### Database Configuration

The application supports multiple database configurations:

#### Production (PostgreSQL)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5532/vehicle_oem_db
    username: vehicle_oem_user
    password: vehicle_oem_pass
    driver-class-name: org.postgresql.Driver
```

#### Development (H2)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:vehicle_oem_db
    username: sa
    password:
    driver-class-name: org.h2.Driver
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` |
| `DB_USERNAME` | Database username | `vehicle_oem_user` |
| `DB_PASSWORD` | Database password | `vehicle_oem_pass` |
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:postgresql://postgres:5432/vehicle_oem_db` |
| `ADMIN_PASSWORD` | Admin password | `admin123` |
| `DEVICE_OEM_SERVER_URL` | Device OEM service URL | `http://device-oem-mock:1080` |
| `KTS_SERVER_URL` | Key Tracking service URL | `http://kts-mock:1080` |
| `VEHICLE_TELEMATICS_URL` | Telematics service URL | `http://telematics-mock:1080` |

## Service Ports

| Service | Port | Description |
|---------|------|-------------|
| Vehicle OEM Server | 8088 | Main application |
| PostgreSQL | 5532 | Database |
| Device OEM Mock | 8081 | Mock service |
| KTS Mock | 8082 | Mock service |
| Telematics Mock | 8083 | Mock service |

## Health Checks

### Application Health
```bash
curl http://localhost:8088/actuator/health
```

### Database Health
```bash
# PostgreSQL
podman exec vehicle-oem-postgres pg_isready -U vehicle_oem_user -d vehicle_oem_db
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 8088, 5532, 8081-8083 are available
2. **Permission issues**: Use `--userns keep-id` with Podman
3. **SELinux issues**: Use `--security-opt label=disable` or proper SELinux contexts
4. **Database connection**: Verify PostgreSQL is running and accessible

### Logs

```bash
# Application logs
podman logs vehicle-oem-server

# Database logs
podman logs vehicle-oem-postgres

# All services logs
podman-compose -f podman-compose.yml logs
```

## Production Considerations

1. **Security**: Change default passwords and use secrets management
2. **Persistence**: Use named volumes or bind mounts for data persistence
3. **Networking**: Configure proper firewall rules
4. **Monitoring**: Set up monitoring and alerting
5. **Backup**: Implement database backup strategies
6. **SSL/TLS**: Configure HTTPS for production deployments
