#!/bin/bash

# Hotel Management Platform Smoke Test Script
# This script tests the complete flow: login -> reservation -> check-in -> payment -> report

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_EMAIL="admin@example.com"
ADMIN_PASSWORD="admin123"

echo "=== Hotel Management Platform Smoke Test ==="
echo "Base URL: $BASE_URL"
echo ""

# Step 1: Login as admin
echo "Step 1: Login as admin..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASSWORD\"}")

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo "ERROR: Login failed"
  echo "Response: $LOGIN_RESPONSE"
  exit 1
fi

echo "✓ Login successful. Token: ${TOKEN:0:20}..."
echo ""

# Step 2: Create reservation
echo "Step 2: Create reservation..."
CHECK_IN_DATE=$(date -d "+1 day" +%Y-%m-%d 2>/dev/null || date -v+1d +%Y-%m-%d)
CHECK_OUT_DATE=$(date -d "+3 days" +%Y-%m-%d 2>/dev/null || date -v+3d +%Y-%m-%d)

RESERVATION_RESPONSE=$(curl -s -X POST "$BASE_URL/api/reservations" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"guestName\": \"Asha Kumar\",
    \"guestEmail\": \"asha@example.com\",
    \"phone\": \"+919900112233\",
    \"roomTypeId\": 1,
    \"checkInDate\": \"$CHECK_IN_DATE\",
    \"checkOutDate\": \"$CHECK_OUT_DATE\"
  }")

RESERVATION_ID=$(echo $RESERVATION_RESPONSE | jq -r '.id')
if [ "$RESERVATION_ID" == "null" ] || [ -z "$RESERVATION_ID" ]; then
  echo "ERROR: Reservation creation failed"
  echo "Response: $RESERVATION_RESPONSE"
  exit 1
fi

echo "✓ Reservation created. ID: $RESERVATION_ID"
echo ""

# Step 3: Check in
echo "Step 3: Check in reservation..."
CHECKIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/stays/checkin" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"reservationId\": $RESERVATION_ID,
    \"roomId\": 1
  }")

STAY_ID=$(echo $CHECKIN_RESPONSE | jq -r '.id')
FOLIO_ID=$(echo $CHECKIN_RESPONSE | jq -r '.folioId')

if [ "$STAY_ID" == "null" ] || [ -z "$STAY_ID" ]; then
  echo "ERROR: Check-in failed"
  echo "Response: $CHECKIN_RESPONSE"
  exit 1
fi

echo "✓ Check-in successful. Stay ID: $STAY_ID, Folio ID: $FOLIO_ID"
echo ""

# Step 4: Add line item
echo "Step 4: Add line item to folio..."
LINE_ITEM_RESPONSE=$(curl -s -X POST "$BASE_URL/api/folios/$FOLIO_ID/line-items" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"type\": \"ROOM_CHARGE\",
    \"description\": \"Room charge for stay\",
    \"amount\": 3000.00
  }")

LINE_ITEM_ID=$(echo $LINE_ITEM_RESPONSE | jq -r '.id')
if [ "$LINE_ITEM_ID" == "null" ] || [ -z "$LINE_ITEM_ID" ]; then
  echo "ERROR: Line item creation failed"
  echo "Response: $LINE_ITEM_RESPONSE"
  exit 1
fi

echo "✓ Line item added. ID: $LINE_ITEM_ID"
echo ""

# Step 5: Record cash payment
echo "Step 5: Record cash payment..."
PAYMENT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/folios/$FOLIO_ID/payments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"amount\": 3000.00,
    \"method\": \"CASH\",
    \"reference\": \"Receipt #987\"
  }")

PAYMENT_ID=$(echo $PAYMENT_RESPONSE | jq -r '.id')
if [ "$PAYMENT_ID" == "null" ] || [ -z "$PAYMENT_ID" ]; then
  echo "ERROR: Payment recording failed"
  echo "Response: $PAYMENT_RESPONSE"
  exit 1
fi

echo "✓ Payment recorded. ID: $PAYMENT_ID"
echo ""

# Step 6: Get daily report
echo "Step 6: Get daily report..."
TODAY=$(date +%Y-%m-%d)
REPORT_RESPONSE=$(curl -s -X GET "$BASE_URL/api/reports/daily?date=$TODAY" \
  -H "Authorization: Bearer $TOKEN")

REVENUE=$(echo $REPORT_RESPONSE | jq -r '.totalCashRevenue')
if [ "$REVENUE" == "null" ]; then
  echo "ERROR: Report retrieval failed"
  echo "Response: $REPORT_RESPONSE"
  exit 1
fi

echo "✓ Daily report retrieved. Cash Revenue: $REVENUE"
echo ""

echo "=== Smoke Test Summary ==="
echo "Reservation ID: $RESERVATION_ID"
echo "Stay ID: $STAY_ID"
echo "Folio ID: $FOLIO_ID"
echo "Line Item ID: $LINE_ITEM_ID"
echo "Payment ID: $PAYMENT_ID"
echo "Daily Cash Revenue: $REVENUE"
echo ""
echo "✓ All smoke tests passed!"

