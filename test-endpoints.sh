#!/bin/bash

# Vehicle OEM Server API Test Script
# This script tests all available endpoints with proper authentication

BASE_URL="http://localhost:8082"
AUTH="admin:admin123"

echo "🚀 Vehicle OEM Server API Test Suite"
echo "======================================"
echo "Base URL: $BASE_URL"
echo "Authentication: Basic Auth (admin/admin123)"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to make HTTP requests and display results
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "${BLUE}Testing: $description${NC}"
    echo -e "${YELLOW}$method $endpoint${NC}"
    
    if [ -n "$data" ]; then
        echo "Request Body: $data"
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
            -X "$method" \
            -H "Content-Type: application/json" \
            -u "$AUTH" \
            -d "$data" \
            "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
            -X "$method" \
            -H "Content-Type: application/json" \
            -u "$AUTH" \
            "$BASE_URL$endpoint")
    fi
    
    # Extract HTTP status and body
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')
    
    if [[ $http_status -ge 200 && $http_status -lt 300 ]]; then
        echo -e "${GREEN}✅ Success (HTTP $http_status)${NC}"
    else
        echo -e "${RED}❌ Failed (HTTP $http_status)${NC}"
    fi
    
    echo "Response: $body"
    echo "----------------------------------------"
    echo ""
}

# Function to test with form data
make_form_request() {
    local method=$1
    local endpoint=$2
    local form_data=$3
    local description=$4
    
    echo -e "${BLUE}Testing: $description${NC}"
    echo -e "${YELLOW}$method $endpoint${NC}"
    echo "Form Data: $form_data"
    
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
        -X "$method" \
        -u "$AUTH" \
        $form_data \
        "$BASE_URL$endpoint")
    
    # Extract HTTP status and body
    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')
    
    if [[ $http_status -ge 200 && $http_status -lt 300 ]]; then
        echo -e "${GREEN}✅ Success (HTTP $http_status)${NC}"
    else
        echo -e "${RED}❌ Failed (HTTP $http_status)${NC}"
    fi
    
    echo "Response: $body"
    echo "----------------------------------------"
    echo ""
}

echo "🏥 Health Check"
echo "==============="
make_request "GET" "/actuator/health" "" "Application Health Check"

echo "👤 Owner Account Management"
echo "==========================="

# Create Owner Account (Form Data) - Use unique ID to avoid conflicts
TIMESTAMP=$(date +%s)
ACCOUNT_ID="ACC${TIMESTAMP}"
make_form_request "POST" "/api/v1/accounts" \
    "-d accountId=${ACCOUNT_ID} -d email=test${TIMESTAMP}@example.com -d firstName=Test -d lastName=User" \
    "Create Owner Account"

# Get Owner Account by ID
make_request "GET" "/api/v1/accounts/${ACCOUNT_ID}" "" "Get Owner Account by ID"

# Get Owner Account by Email
make_request "GET" "/api/v1/accounts/by-email/test${TIMESTAMP}@example.com" "" "Get Owner Account by Email"

echo "🚗 Vehicle Management"
echo "===================="

# Create Vehicle (Form Data) - Use unique VIN and link to created account
VIN="1HGBH41JXMN${TIMESTAMP: -6}"  # Use last 6 digits of timestamp for unique VIN (valid format)
make_form_request "POST" "/api/v1/vehicles/test-friendly" \
    "-d vin=${VIN} -d make=Honda -d model=Accord -d year=2023 -d ownerAccountId=${ACCOUNT_ID}" \
    "Create Vehicle (Test-Friendly)"

# Verify Owner Email (required for digital key tracking)
make_request "POST" "/api/v1/accounts/${ACCOUNT_ID}/verify-email" "" "Verify Owner Email"

# Update Vehicle Subscription to Premium (to allow friend keys)
make_request "PUT" "/api/v1/vehicles/${VIN}/subscription-tier?tier=PREMIUM" "" "Update Vehicle Subscription Tier to Premium"

echo "🔑 Digital Key Management"
echo "========================="

# Track Digital Key (now should work with verified email)
KEY_ID="OWNER-KEY-${TIMESTAMP}"
# Use compatible date command for macOS
EXPIRY_DATE=$(date -v+25d +"%Y-%m-%dT23:59:59")
make_request "POST" "/api/v1/keys/track" \
    "{\"keyId\":\"${KEY_ID}\",\"deviceId\":\"IPHONE-14-ABC123\",\"deviceOem\":\"Apple\",\"vehicleId\":\"${VIN}\",\"keyType\":\"OWNER\",\"publicKey\":\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE...\",\"expiresAt\":\"${EXPIRY_DATE}\"}" \
    "Track Digital Key (Owner Key)"

# Manage Digital Key
make_request "POST" "/api/v1/keys/manage" \
    "{\"keyId\":\"${KEY_ID}\",\"action\":\"SUSPEND\",\"reason\":\"Temporary suspension for testing\",\"requestedBy\":\"admin\"}" \
    "Manage Digital Key (Suspend)"

echo "🤝 Key Sharing"
echo "=============="

# Share Key (now should work with PREMIUM subscription)
make_request "POST" "/api/v1/key-sharing/share" \
    "{\"vehicleVin\":\"${VIN}\",\"friendEmail\":\"jane.doe@example.com\",\"friendName\":\"Jane Doe\",\"permissionLevel\":\"DRIVE_ONLY\",\"expiresAt\":\"${EXPIRY_DATE}\",\"sharedBy\":\"test${TIMESTAMP}@example.com\",\"message\":\"Sharing my car key with you\"}" \
    "Share Digital Key (Friend Key)"

echo "📊 Monitoring Endpoints"
echo "======================"

make_request "GET" "/actuator/info" "" "Application Info"
make_request "GET" "/actuator/metrics" "" "Application Metrics"

echo ""
echo "🎉 API Test Suite Completed!"
echo "============================="
echo "Check the results above for any failed tests."
echo "All endpoints have been tested with sample data."
