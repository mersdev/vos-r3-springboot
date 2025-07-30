# API Testing Guide

This guide covers testing the Vehicle OEM Server APIs using HTTP client files and other testing tools.

## HTTP Client Testing

### Using the .http File

The project includes `api-tests.http` file with comprehensive API tests. This file can be used with:

- **IntelliJ IDEA**: Built-in HTTP client
- **VS Code**: REST Client extension
- **Postman**: Import the HTTP file
- **curl**: Convert requests to curl commands

### Prerequisites

1. **Start the Application**:
   ```bash
   # Development mode (H2 database)
   mvn spring-boot:run -Dspring.profiles.active=dev
   
   # Production mode (PostgreSQL with Podman)
   podman-compose -f podman-compose.yml up -d
   ```

2. **Verify Application is Running**:
   ```bash
   curl http://localhost:8088/actuator/health
   ```

### Test Scenarios

The `api-tests.http` file includes the following test scenarios:

#### 1. Health and Monitoring
- Health check endpoint
- Application info
- Metrics endpoint

#### 2. Owner Account Management
- Create owner account
- Get account by ID
- Get account by email

#### 3. Vehicle Management
- Create vehicle
- Associate with owner

#### 4. Digital Key Management
- Track owner key
- Track friend key
- Suspend key
- Resume key
- Terminate key

#### 5. Key Sharing
- Share key with friend
- Accept key invitation
- Update permissions
- Set restrictions
- Get pending invitations
- Get shared keys
- Revoke shared key

#### 6. Vehicle Pairing
- Initiate pairing
- Complete pairing
- Revoke pairing

### Running Tests in IntelliJ IDEA

1. Open `api-tests.http` file
2. Click the green arrow next to any request
3. View response in the HTTP client window
4. Use variables for different environments

### Running Tests in VS Code

1. Install "REST Client" extension
2. Open `api-tests.http` file
3. Click "Send Request" above any request
4. View response in split pane

### Authentication

All API endpoints require Basic Authentication:
- **Username**: `admin`
- **Password**: `admin123` (configurable via `ADMIN_PASSWORD` env var)

## Postman Collection

The project also includes a Postman collection: `vehicle-oem-api.postman_collection.json`

### Importing to Postman

1. Open Postman
2. Click "Import"
3. Select `vehicle-oem-api.postman_collection.json`
4. Configure environment variables:
   - `baseUrl`: `http://localhost:8088`
   - `username`: `admin`
   - `password`: `admin123`

## Test Data Setup

### Minimal Test Data

For basic testing, create this minimal dataset:

```http
### 1. Create Owner Account
POST http://localhost:8088/api/v1/accounts
Authorization: Basic admin admin123
Content-Type: application/x-www-form-urlencoded

accountId=TEST-OWNER&email=test@example.com&firstName=Test&lastName=User

### 2. Create Vehicle
POST http://localhost:8088/api/v1/vehicles
Authorization: Basic admin admin123
Content-Type: application/x-www-form-urlencoded

vin=TEST123456789&make=Tesla&model=Model3&year=2023&ownerAccountId=TEST-OWNER

### 3. Track Key
POST http://localhost:8088/api/v1/keys/track
Authorization: Basic admin admin123
Content-Type: application/json

{
  "keyId": "TEST-KEY-001",
  "deviceId": "TEST-DEVICE-001",
  "deviceOem": "Apple",
  "vehicleId": "TEST123456789",
  "keyType": "OWNER"
}
```

### Business Scenario Testing

For comprehensive business scenario testing:

1. **Family Car Sharing**:
   - Create family owner account
   - Create vehicle
   - Share keys with family members
   - Set time restrictions for teenage driver

2. **Corporate Fleet**:
   - Create corporate accounts
   - Register multiple vehicles
   - Share keys with employees
   - Set usage restrictions

3. **Car Rental**:
   - Create rental company account
   - Register rental vehicles
   - Share temporary keys with customers
   - Revoke keys after rental period

## Error Testing

### Common Error Scenarios

1. **Invalid Authentication**:
   ```http
   GET http://localhost:8088/api/v1/accounts/TEST-OWNER
   Authorization: Basic invalid credentials
   ```

2. **Missing Required Fields**:
   ```http
   POST http://localhost:8088/api/v1/accounts
   Authorization: Basic admin admin123
   Content-Type: application/json
   
   {
     "accountId": "INCOMPLETE"
     // Missing required fields
   }
   ```

3. **Duplicate Resources**:
   ```http
   POST http://localhost:8088/api/v1/accounts
   Authorization: Basic admin admin123
   Content-Type: application/x-www-form-urlencoded
   
   accountId=EXISTING-ID&email=existing@example.com&firstName=Test&lastName=User
   ```

4. **Resource Not Found**:
   ```http
   GET http://localhost:8088/api/v1/accounts/NON-EXISTENT
   Authorization: Basic admin admin123
   ```

## Performance Testing

### Load Testing with curl

```bash
# Simple load test
for i in {1..100}; do
  curl -s -w "%{http_code}\n" \
    -u admin:admin123 \
    http://localhost:8088/actuator/health > /dev/null
done
```

### Using Apache Bench

```bash
# Test health endpoint
ab -n 1000 -c 10 -A admin:admin123 http://localhost:8088/actuator/health

# Test API endpoint
ab -n 100 -c 5 -A admin:admin123 http://localhost:8088/api/v1/accounts/TEST-OWNER
```

## Automated Testing

### Shell Script Testing

The project includes `test-endpoints.sh` for automated testing:

```bash
./test-endpoints.sh
```

### Integration with CI/CD

Example GitHub Actions workflow:

```yaml
name: API Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Start Application
        run: mvn spring-boot:run -Dspring.profiles.active=dev &
      - name: Wait for Application
        run: sleep 30
      - name: Run API Tests
        run: ./test-endpoints.sh
```

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure application is running on correct port
2. **Authentication Failed**: Verify username/password
3. **404 Not Found**: Check endpoint URLs and context path
4. **500 Internal Server Error**: Check application logs

### Debugging

1. **Enable Debug Logging**:
   ```yaml
   logging:
     level:
       com.vehicleoem: DEBUG
   ```

2. **Check Application Logs**:
   ```bash
   # Local development
   tail -f logs/application.log
   
   # Podman container
   podman logs -f vehicle-oem-server
   ```

3. **Database Inspection**:
   ```bash
   # H2 Console (dev mode)
   http://localhost:8082/h2-console
   
   # PostgreSQL (production)
   podman exec -it vehicle-oem-postgres psql -U vehicle_oem_user -d vehicle_oem_db
   ```

## Best Practices

1. **Test Order**: Run tests in logical order (create dependencies first)
2. **Clean State**: Reset database between test runs if needed
3. **Error Handling**: Test both success and error scenarios
4. **Documentation**: Keep test documentation updated
5. **Automation**: Automate repetitive tests
6. **Monitoring**: Monitor test execution times and success rates
