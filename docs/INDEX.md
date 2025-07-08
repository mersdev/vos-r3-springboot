# Vehicle OEM Server Documentation

Welcome to the Vehicle OEM Server documentation. This directory contains comprehensive documentation for the project.

## 📚 Documentation Structure

### Core Documentation
- **[README.md](README.md)** - Main project documentation and setup instructions
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - High-level project overview and architecture

### Business Implementation
- **[BUSINESS_IMPLEMENTATION_SUMMARY.md](BUSINESS_IMPLEMENTATION_SUMMARY.md)** - Business logic and real-world scenarios
- **[intro.md](intro.md)** - Project introduction and context

### Testing Documentation
- **[TEST_DOCUMENTATION.md](TEST_DOCUMENTATION.md)** - Comprehensive test suite documentation
- **[COMPREHENSIVE_TEST_SUITE_SUMMARY.md](COMPREHENSIVE_TEST_SUITE_SUMMARY.md)** - Test coverage summary

### Issue Resolution & Success Stories
- **[DATABASE_ISSUE_RESOLUTION_SUCCESS.md](DATABASE_ISSUE_RESOLUTION_SUCCESS.md)** - Database configuration resolution
- **[ISSUE_RESOLUTION_SUCCESS.md](ISSUE_RESOLUTION_SUCCESS.md)** - General issue resolution documentation

## 🚀 Quick Start

1. **Setup**: Start with [README.md](README.md) for initial setup
2. **Architecture**: Review [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) for system overview
3. **Business Logic**: Check [BUSINESS_IMPLEMENTATION_SUMMARY.md](BUSINESS_IMPLEMENTATION_SUMMARY.md) for business scenarios
4. **Testing**: See [TEST_DOCUMENTATION.md](TEST_DOCUMENTATION.md) for testing guidelines

## 🔧 Development

### Running with Podman
```bash
# Start services with Podman
podman-compose up -d

# Or use the provided script
./run-podman.sh
```

### Database Configuration
The application now uses PostgreSQL by default. See [DATABASE_ISSUE_RESOLUTION_SUCCESS.md](DATABASE_ISSUE_RESOLUTION_SUCCESS.md) for configuration details.

### API Testing
Use the provided HTTP client scripts in the project root to test all endpoints. See [API_TESTING_GUIDE.md](API_TESTING_GUIDE.md) for detailed instructions.

## 📖 Additional Resources

- **API Endpoints**: Documented in [README.md](README.md) and [../README.md](../README.md)
- **Business Scenarios**: Detailed in [BUSINESS_IMPLEMENTATION_SUMMARY.md](BUSINESS_IMPLEMENTATION_SUMMARY.md)
- **Test Coverage**: Comprehensive coverage documented in [TEST_DOCUMENTATION.md](TEST_DOCUMENTATION.md)
- **Deployment**: Podman and Docker instructions in [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)
