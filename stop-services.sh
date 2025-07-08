#!/bin/bash

# Vehicle OEM Server - Stop All Services Script
# This script stops all Vehicle OEM Server services running in Podman

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="vehicle-oem-server"

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

# Container names
CONTAINERS=(
    "${APP_NAME}-postgres"
    "${APP_NAME}-device-oem-mock"
    "${APP_NAME}-kts-mock"
    "${APP_NAME}-telematics-mock"
)

check_task() {
    if command -v task &> /dev/null; then
        print_success "Task is available - using Taskfile for service management"
        return 0
    else
        print_warning "Task not found - using direct commands"
        return 1
    fi
}

stop_application() {
    print_status "Stopping Spring Boot application..."
    
    # Try to stop gracefully first
    pkill -f "spring-boot:run" || true
    pkill -f "${APP_NAME}" || true
    
    # Wait a moment for graceful shutdown
    sleep 2
    
    # Force kill if still running
    pkill -9 -f "spring-boot:run" || true
    pkill -9 -f "${APP_NAME}" || true
    
    print_success "Application stopped"
}

stop_containers() {
    print_status "Stopping containers..."
    
    for container in "${CONTAINERS[@]}"; do
        if podman ps --format "{{.Names}}" | grep -q "^${container}$"; then
            print_status "Stopping container: $container"
            podman stop $container
            print_success "Stopped: $container"
        else
            print_warning "Container not running: $container"
        fi
    done
}

remove_containers() {
    print_status "Removing containers..."
    
    for container in "${CONTAINERS[@]}"; do
        if podman ps -a --format "{{.Names}}" | grep -q "^${container}$"; then
            print_status "Removing container: $container"
            podman rm $container
            print_success "Removed: $container"
        fi
    done
}

remove_volumes() {
    print_status "Removing volumes..."
    
    # List and remove project-related volumes
    volumes=$(podman volume ls --format "{{.Name}}" | grep -E "(${APP_NAME}|postgres|vehicle)" || true)
    
    if [ -n "$volumes" ]; then
        for volume in $volumes; do
            print_status "Removing volume: $volume"
            podman volume rm $volume
            print_success "Removed volume: $volume"
        done
    else
        print_warning "No project volumes found"
    fi
}

show_status() {
    print_status "Current status:"
    echo ""
    
    echo "Running containers:"
    running_containers=$(podman ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(${APP_NAME}|postgres|mock)" || echo "None")
    echo "$running_containers"
    echo ""
    
    echo "All project containers:"
    all_containers=$(podman ps -a --format "table {{.Names}}\t{{.Status}}" | grep -E "(${APP_NAME}|postgres|mock)" || echo "None")
    echo "$all_containers"
    echo ""
    
    echo "Project volumes:"
    volumes=$(podman volume ls --format "table {{.Name}}\t{{.Driver}}" | grep -E "(${APP_NAME}|postgres|vehicle)" || echo "None")
    echo "$volumes"
}

main() {
    print_status "Stopping Vehicle OEM Server services..."
    
    if check_task; then
        print_status "Using Taskfile for service management..."
        task stop
    else
        print_status "Using direct commands for service management..."
        stop_application
        stop_containers
        print_success "All services stopped"
    fi
    
    show_status
}

# Handle script arguments
case "${1:-}" in
    "clean")
        print_status "Performing complete cleanup..."
        stop_application
        stop_containers
        remove_containers
        print_warning "Volumes preserved. Use 'clean-all' to remove volumes too."
        print_success "Cleanup completed"
        show_status
        ;;
    "clean-all")
        print_warning "This will remove ALL containers and volumes!"
        read -p "Are you sure? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            stop_application
            stop_containers
            remove_containers
            remove_volumes
            print_success "Complete cleanup finished"
        else
            print_status "Cleanup cancelled"
        fi
        show_status
        ;;
    "app-only")
        print_status "Stopping application only..."
        stop_application
        ;;
    "containers-only")
        print_status "Stopping containers only..."
        stop_containers
        ;;
    "status")
        show_status
        ;;
    *)
        main
        ;;
esac
