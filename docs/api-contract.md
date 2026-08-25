# PayFlow — Payment Service API Contract

Base URL: /api/v1
Auth: Bearer JWT token in Authorization header
Idempotency: POST requests require Idempotency-Key header

---

## 1. Initiate Payment

POST /api/v1/payments

Headers:
  Authorization: Bearer <jwt_token>
  Idempotency-Key: <uuid>        ← required, client generates this
  Content-Type: application/json

Request Body:
{
  "merchantId": "uuid",
  "amount": 50000,               ← in paise (50000 = ₹500)
  "currency": "INR",
  "customerEmail": "customer@example.com",
  "customerPhone": "9876543210",
  "description": "Order #1234",
  "metadata": {                  ← optional, any extra data
    "orderId": "ORD-1234",
    "productName": "Premium Plan"
  }
}

Success Response: 202 Accepted
{
  "transactionId": "uuid",
  "status": "INITIATED",
  "amount": 50000,
  "currency": "INR",
  "merchantId": "uuid",
  "createdAt": "2026-08-25T10:30:00Z",
  "message": "Payment initiated successfully"
}

Duplicate Request (same Idempotency-Key): 200 OK
{
  "transactionId": "uuid",       ← same as original
  "status": "INITIATED",         ← whatever status it's at now
  "amount": 50000,
  "currency": "INR",
  "merchantId": "uuid",
  "createdAt": "2026-08-25T10:30:00Z",
  "message": "Duplicate request — returning existing transaction"
}

Error Responses:
  400 Bad Request     ← validation failed (amount <= 0, missing fields)
  401 Unauthorized    ← invalid or missing JWT
  404 Not Found       ← merchantId doesn't exist
  429 Too Many Requests ← rate limit exceeded
  500 Internal Server Error

Error Response Body (all errors):
{
  "error": "VALIDATION_FAILED",
  "message": "Amount must be greater than zero",
  "timestamp": "2026-08-25T10:30:00Z",
  "path": "/api/v1/payments"
}

---

## 2. Get Payment by ID

GET /api/v1/payments/{transactionId}

Headers:
  Authorization: Bearer <jwt_token>

Success Response: 200 OK
{
  "transactionId": "uuid",
  "status": "SUCCESS",           ← INITIATED / PROCESSING / SUCCESS / FAILED / TIMEOUT
  "amount": 50000,
  "currency": "INR",
  "merchantId": "uuid",
  "customerEmail": "customer@example.com",
  "description": "Order #1234",
  "metadata": { "orderId": "ORD-1234" },
  "createdAt": "2026-08-25T10:30:00Z",
  "updatedAt": "2026-08-25T10:30:05Z"
}

Error Responses:
  401 Unauthorized
  403 Forbidden       ← transaction belongs to a different merchant
  404 Not Found       ← transactionId doesn't exist

---

## 3. List Payments

GET /api/v1/payments?merchantId=uuid&status=SUCCESS&page=0&size=20

Headers:
  Authorization: Bearer <jwt_token>

Query Parameters:
  merchantId  (required)
  status      (optional) ← INITIATED / PROCESSING / SUCCESS / FAILED / TIMEOUT
  page        (optional, default 0)
  size        (optional, default 20, max 100)
  startDate   (optional) ← ISO 8601
  endDate     (optional) ← ISO 8601

Success Response: 200 OK
{
  "content": [
    {
      "transactionId": "uuid",
      "status": "SUCCESS",
      "amount": 50000,
      "currency": "INR",
      "createdAt": "2026-08-25T10:30:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "hasNext": true
}

---

## 4. Get Payment Status (lightweight)

GET /api/v1/payments/{transactionId}/status

Headers:
  Authorization: Bearer <jwt_token>

Success Response: 200 OK
{
  "transactionId": "uuid",
  "status": "SUCCESS",
  "updatedAt": "2026-08-25T10:30:05Z"
}

← Merchant polls this endpoint to check status
← Lightweight — no full transaction details
← This is what they call while waiting for webhook

---

## Transaction Status State Machine

INITIATED → PROCESSING → SUCCESS
                    ↓
                  FAILED
                    ↓
                 TIMEOUT

Rules:
- INITIATED can only go to PROCESSING
- PROCESSING can go to SUCCESS, FAILED, or TIMEOUT
- SUCCESS, FAILED, TIMEOUT are terminal — no further transitions allowed
