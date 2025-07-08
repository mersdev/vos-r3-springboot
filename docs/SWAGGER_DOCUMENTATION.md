# OpenAPI/Swagger Documentation

## Overview

The Vehicle OEM Server includes comprehensive OpenAPI 3.0 documentation with Swagger UI integration, providing interactive API documentation for all endpoints.

## Features

### 🔍 **Interactive API Documentation**
- **Swagger UI**: Interactive web interface for exploring and testing APIs
- **Try It Out**: Execute API calls directly from the documentation
- **Request/Response Examples**: Comprehensive examples for all endpoints
- **Schema Validation**: Real-time validation of request payloads

### 📋 **Comprehensive Coverage**
- **All Endpoints**: Complete documentation for all REST endpoints
- **Request/Response Models**: Detailed schema definitions with examples
- **Error Responses**: Documented error scenarios with example responses
- **Authentication**: Security requirements and authentication methods

### 🎯 **Developer-Friendly**
- **Code Examples**: Multiple request/response examples per endpoint
- **Business Logic**: Detailed descriptions of business rules and constraints
- **Parameter Validation**: Clear validation rules and constraints
- **Status Codes**: Comprehensive HTTP status code documentation

## Access Points

### Development Environment
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/api-docs

### Production Environment
- **Swagger UI**: https://api.vehicleoem.com/swagger-ui.html
- **OpenAPI JSON**: https://api.vehicleoem.com/api-docs

## API Structure

### Digital Key Management
The core digital key management APIs with comprehensive examples:

#### Track Digital Key
- **Endpoint**: `POST /api/v1/keys/track`
- **Purpose**: Create and track new digital keys
- **Examples**: Owner keys, friend keys, with various configurations
- **Validation**: Key ID format, device information, vehicle validation

#### Manage Digital Key Lifecycle
- **Endpoint**: `POST /api/v1/keys/manage`
- **Purpose**: Manage key lifecycle (suspend, resume, terminate)
- **Examples**: Security actions, administrative operations
- **Business Rules**: State transition validation, authorization checks

## Implementation Details

### Architecture
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Swagger UI    │    │   OpenAPI Spec   │    │   Controller    │
│                 │    │                  │    │   Interface     │
│ - Interactive   │◄──►│ - Comprehensive  │◄──►│                 │
│ - Try It Out    │    │ - Examples       │    │ - Clean API     │
│ - Validation    │    │ - Schemas        │    │ - Documentation │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### Key Components

#### 1. OpenAPI Configuration (`OpenApiConfig.java`)
- Global API information and metadata
- Security scheme definitions
- Server configurations for different environments
- Contact information and licensing

#### 2. API Interface (`DigitalKeyApi.java`)
- Centralized OpenAPI annotations
- Comprehensive operation documentation
- Request/response examples
- Parameter descriptions and validation rules

#### 3. Controller Implementation (`DigitalKeyController.java`)
- Clean implementation without documentation clutter
- Implements the documented API interface
- Focuses on business logic

#### 4. DTO Schema Annotations
- Detailed field descriptions
- Validation constraints
- Example values
- Format specifications

## Configuration

### Application Properties
```yaml
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: method
    tags-sorter: alpha
    display-request-duration: true
    show-extensions: true
    show-common-extensions: true
  show-actuator: false
  default-consumes-media-type: application/json
  default-produces-media-type: application/json
```

### Security Integration
- HTTP Basic Authentication documented
- Security requirements specified per endpoint
- Authentication examples provided

## Usage Examples

### Digital Key Tracking
```json
{
  "keyId": "OWNER-KEY-12345678",
  "deviceId": "IPHONE-14-ABC123",
  "deviceOem": "Apple",
  "vehicleId": "1HGBH41JXMN109186",
  "keyType": "OWNER",
  "publicKey": "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...",
  "uiBundle": "com.apple.carkey.ui.bundle",
  "vehicleMobilizationData": "encrypted_mobilization_data_here",
  "expiresAt": "2025-12-31T23:59:59"
}
```

### Key Management
```json
{
  "keyId": "OWNER-KEY-12345678",
  "action": "SUSPEND",
  "reason": "Suspicious activity detected",
  "requestedBy": "security-admin"
}
```

## Benefits

### For Developers
- **Faster Integration**: Clear examples and documentation
- **Reduced Errors**: Schema validation and examples
- **Better Understanding**: Business rules and constraints documented
- **Testing**: Interactive testing capabilities

### For API Consumers
- **Self-Service**: Complete documentation without external resources
- **Validation**: Real-time request validation
- **Examples**: Working examples for all scenarios
- **Error Handling**: Clear error response documentation

### For Operations
- **Monitoring**: API usage and performance insights
- **Debugging**: Clear request/response examples
- **Maintenance**: Centralized documentation updates
- **Compliance**: Complete API specification for audits

## Best Practices

### Documentation Maintenance
1. **Keep Examples Current**: Update examples with real-world scenarios
2. **Validate Schemas**: Ensure schema definitions match implementation
3. **Business Rules**: Document all business logic and constraints
4. **Error Scenarios**: Include comprehensive error response examples

### API Design
1. **Consistent Patterns**: Follow established patterns across endpoints
2. **Clear Naming**: Use descriptive operation and parameter names
3. **Comprehensive Examples**: Provide multiple examples per endpoint
4. **Validation Rules**: Document all validation constraints clearly

## Future Enhancements

### Planned Features
- **API Versioning**: Support for multiple API versions
- **Rate Limiting**: Documentation of rate limiting policies
- **Webhooks**: Documentation for webhook endpoints
- **SDK Generation**: Auto-generated client SDKs

### Integration Opportunities
- **Postman Collections**: Auto-generated Postman collections
- **Testing Frameworks**: Integration with automated testing
- **CI/CD**: Documentation validation in build pipelines
- **Monitoring**: API usage analytics and monitoring
