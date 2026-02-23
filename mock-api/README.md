# Cashi Mock API (Ktor Server)

A Kotlin Ktor server that mocks the backend for the Cashi Payment App.

## Setup & Run

```bash
cd mock-api
./gradlew run
```

Server runs on `http://localhost:3000`.

## Endpoints

### `POST /payments`

**Request body:**
```json
{
  "recipientEmail": "user@example.com",
  "amount": 50.00,
  "currency": "USD",
  "senderName": "John Doe"
}
```

**Success (201):**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "recipientEmail": "user@example.com",
    "amount": 50.00,
    "currency": "USD",
    "senderName": "John Doe",
    "status": "completed",
    "timestamp": "2026-02-23T20:00:00Z"
  }
}
```

**Validation Error (400):**
```json
{
  "success": false,
  "errors": ["amount must be a positive number"]
}
```

### `GET /transactions`
Returns all processed transactions.

### `GET /health`
Health check endpoint.
