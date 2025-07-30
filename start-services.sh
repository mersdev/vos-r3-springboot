#!/bin/bash

# Vehicle OEM Server - Start All Services Script
# This script starts PostgreSQL, mock services, and the Spring Boot application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="vehicle-oem-server"
APP_PORT="8088"
DB_PORT="5532"
DB_NAME="vehicle_oem_db"
DB_USER="vehicle_oem_user"
DB_PASSWORD="vehicle_oem_pass"
MOCK_DEVICE_OEM_PORT="8081"
MOCK_KTS_PORT="8082"
MOCK_TELEMATICS_PORT="8083"

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_podman() {
    if ! command -v podman &> /dev/null; then
        print_error "Podman is not installed. Please install Podman first."
        exit 1
    fi
    print_success "Podman is available"
}

check_task() {
    if command -v task &> /dev/null; then
        print_success "Task is available - using Taskfile for service management"
        return 0
    else
        print_warning "Task not found - using direct commands"
        return 1
    fi
}

start_database() {
    print_status "Starting PostgreSQL database..."
    
    podman run -d \
        --name ${APP_NAME}-postgres \
        --replace \
        -e POSTGRES_DB=${DB_NAME} \
        -e POSTGRES_USER=${DB_USER} \
        -e POSTGRES_PASSWORD=${DB_PASSWORD} \
        -e POSTGRES_HOST_AUTH_METHOD=trust \
        -p ${DB_PORT}:5432 \
        -v ${APP_NAME}-postgres-data:/var/lib/postgresql/data:Z \
        --security-opt label=disable \
        --userns keep-id \
        docker.io/postgres:15

    print_status "Waiting for PostgreSQL to be ready..."
    for i in {1..30}; do
        if podman exec ${APP_NAME}-postgres pg_isready -U ${DB_USER} -d ${DB_NAME} &>/dev/null; then
            print_success "PostgreSQL is ready"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "PostgreSQL failed to start within 60 seconds"
            exit 1
        fi
        sleep 2
        echo -n "."
    done
}

start_mock_services() {
    print_status "Starting mock services..."
    
    # Device OEM Mock
    print_status "Starting Device OEM mock service..."
    podman run -d \
        --name ${APP_NAME}-device-oem-mock \
        --replace \
        -p ${MOCK_DEVICE_OEM_PORT}:1080 \
        -e MOCKSERVER_SERVER_PORT=1080 \
        --security-opt label=disable \
        --userns keep-id \
        docker.io/mockserver/mockserver:latest

    # KTS Mock
    print_status "Starting KTS mock service..."
    podman run -d \
        --name ${APP_NAME}-kts-mock \
        --replace \
        -p ${MOCK_KTS_PORT}:1080 \
        -e MOCKSERVER_SERVER_PORT=1080 \
        --security-opt label=disable \
        --userns keep-id \
        docker.io/mockserver/mockserver:latest

    # Telematics Mock
    print_status "Starting Telematics mock service..."
    podman run -d \
        --name ${APP_NAME}-telematics-mock \
        --replace \
        -p ${MOCK_TELEMATICS_PORT}:1080 \
        -e MOCKSERVER_SERVER_PORT=1080 \
        --security-opt label=disable \
        --userns keep-id \
        docker.io/mockserver/mockserver:latest

    print_success "All mock services started"
}

run_migrations() {
    print_status "Running database migrations..."
    ./mvnw flyway:migrate \
        -Dflyway.url=jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME} \
        -Dflyway.user=${DB_USER} \
        -Dflyway.password=${DB_PASSWORD}
    print_success "Database migrations completed"
}

start_application() {
    print_status "Starting Spring Boot application..."
    
    export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME}
    export DB_USERNAME=${DB_USER}
    export DB_PASSWORD=${DB_PASSWORD}
    export DEVICE_OEM_SERVER_URL=http://localhost:${MOCK_DEVICE_OEM_PORT}
    export KTS_SERVER_URL=http://localhost:${MOCK_KTS_PORT}
    export VEHICLE_TELEMATICS_URL=http://localhost:${MOCK_TELEMATICS_PORT}
    
    print_status "Environment variables set:"
    echo "  - Database: ${SPRING_DATASOURCE_URL}"
    echo "  - Device OEM: ${DEVICE_OEM_SERVER_URL}"
    echo "  - KTS: ${KTS_SERVER_URL}"
    echo "  - Telematics: ${VEHICLE_TELEMATICS_URL}"
    
    print_status "Starting application with Maven..."
    ./mvnw spring-boot:run
}

wait_for_application() {
    print_status "Waiting for application to be ready..."
    for i in {1..60}; do
        if curl -s -f http://localhost:${APP_PORT}/actuator/health &>/dev/null; then
            print_success "Application is ready and healthy"
            return 0
        fi
        if [ $i -eq 60 ]; then
            print_error "Application failed to start within 120 seconds"
            return 1
        fi
        sleep 2
        echo -n "."
    done
}

show_status() {
    print_status "Deployment Status:"
    echo ""
    echo "Services:"
    echo "  - Vehicle OEM Server: http://localhost:${APP_PORT}"
    echo "  - PostgreSQL Database: localhost:${DB_PORT}"
    echo "  - Device OEM Mock: http://localhost:${MOCK_DEVICE_OEM_PORT}"
    echo "  - KTS Mock: http://localhost:${MOCK_KTS_PORT}"
    echo "  - Telematics Mock: http://localhost:${MOCK_TELEMATICS_PORT}"
    echo ""
    echo "Health Check:"
    echo "  curl http://localhost:${APP_PORT}/actuator/health"
    echo ""
    echo "API Testing:"
    echo "  Use api-tests.http file or Postman collection"
    echo ""
    echo "Stop Services:"
    echo "  ./stop-services.sh"
    echo ""
    echo "Using Taskfile (if available):"
    echo "  task start    # Start all services"
    echo "  task stop     # Stop all services"
    echo "  task status   # Show service status"
}

main() {
    print_status "Starting Vehicle OEM Server deployment..."
    
    check_podman
    
    if check_task; then
        print_status "Using Taskfile for service management..."
        task start
    else
        print_status "Using direct commands for service management..."
        start_database
        start_mock_services
        run_migrations
        start_application
    fi
    
    show_status
    print_success "Vehicle OEM Server deployment completed!"
}

# Handle script arguments
case "${1:-}" in
    "db-only")
        print_status "Starting database only..."
        check_podman
        start_database
        run_migrations
        print_success "Database started and migrated"
        ;;
    "mocks-only")
        print_status "Starting mock services only..."
        check_podman
        start_mock_services
        print_success "Mock services started"
        ;;
    "app-only")
        print_status "Starting application only..."
        start_application
        ;;
    "dev")
        print_status "Starting in development mode..."
        check_podman
        start_database
        start_mock_services
        run_migrations
        export SPRING_PROFILES_ACTIVE=dev
        start_application
        ;;
    *)
        main
        ;;
esac
