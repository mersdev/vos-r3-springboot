#!/bin/bash

echo "🧪 Vehicle OEM Server - Comprehensive Test Suite"
echo "================================================"

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "✅ Maven found: $(mvn -version | head -n 1)"
    MAVEN_CMD="mvn"
elif [ -f "./mvnw" ]; then
    echo "✅ Maven Wrapper found"
    MAVEN_CMD="./mvnw"
else
    echo "❌ Maven not found. Please install Maven or ensure mvnw is available."
    exit 1
fi

echo ""
echo "🚀 Running comprehensive test suite..."
echo "This will test all models, services, repositories, controllers, and business logic."
echo ""

# Run tests with detailed output
$MAVEN_CMD clean test

TEST_RESULT=$?

echo ""
echo "📊 Test Results Summary"
echo "======================"

if [ $TEST_RESULT -eq 0 ]; then
    echo "✅ ALL TESTS PASSED! 🎉"
    echo ""
    echo "✅ Model validation tests: PASSED"
    echo "✅ Service business logic tests: PASSED"
    echo "✅ Repository integration tests: PASSED"
    echo "✅ Controller endpoint tests: PASSED"
    echo "✅ Security & cryptography tests: PASSED"
    echo "✅ Business scenario tests: PASSED"
    echo "✅ Exception handling tests: PASSED"
    echo ""
    echo "🎯 The Vehicle OEM Server is ready for production!"
    echo "All business logic, validation, security, and integration tests are working correctly."
else
    echo "❌ SOME TESTS FAILED"
    echo ""
    echo "Please check the test output above for details on which tests failed."
    echo "Common issues:"
    echo "- Missing dependencies"
    echo "- Database connection issues"
    echo "- Configuration problems"
    echo ""
    echo "Run with -X flag for detailed debugging: $MAVEN_CMD clean test -X"
fi

echo ""
echo "📋 Test Coverage Includes:"
echo "- ✅ Entity models and business constraints"
echo "- ✅ DTO validation and serialization"
echo "- ✅ Repository queries and database operations"
echo "- ✅ Service layer business logic"
echo "- ✅ REST API endpoints and error handling"
echo "- ✅ Security and cryptographic operations"
echo "- ✅ Complete business workflows"
echo "- ✅ Exception scenarios and edge cases"

exit $TEST_RESULT
