#!/bin/bash

echo "=== Vehicle OEM Server Build and Run Script ==="
echo ""

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "✓ Maven found"
    BUILD_CMD="mvn"
elif [ -f "./mvnw" ]; then
    echo "✓ Maven wrapper found"
    BUILD_CMD="./mvnw"
else
    echo "✗ Maven not found. Please install Maven or create Maven wrapper."
    echo "To create Maven wrapper, run: mvn wrapper:wrapper"
    exit 1
fi

echo ""
echo "=== Building the application ==="
$BUILD_CMD clean compile

if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "✗ Build failed"
    exit 1
fi

echo ""
echo "=== Running tests ==="
$BUILD_CMD test

if [ $? -eq 0 ]; then
    echo "✓ Tests passed"
else
    echo "✗ Tests failed"
    exit 1
fi

echo ""
echo "=== Packaging application ==="
$BUILD_CMD package -DskipTests

if [ $? -eq 0 ]; then
    echo "✓ Packaging successful"
    echo ""
    echo "=== Application ready to run ==="
    echo "To start the application:"
    echo "  java -jar target/vehicle-oem-server-1.0.0.jar"
    echo ""
    echo "Or use Spring Boot Maven plugin:"
    echo "  $BUILD_CMD spring-boot:run"
    echo ""
    echo "Or use Docker Compose:"
    echo "  docker-compose up"
else
    echo "✗ Packaging failed"
    exit 1
fi
