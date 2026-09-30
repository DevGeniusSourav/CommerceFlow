# CommerceFlow

A production-style e-commerce platform built using Spring Boot Microservices.

## Tech Stack

- Java 21
- Spring Boot
- Spring Cloud (Eureka, Gateway)
- PostgreSQL
- Kafka (transactional outbox pattern)
- Docker / Docker Compose

## Services

| Service | Port | Gateway route | Status |
|---|---|---|---|
| Discovery Server (Eureka) | 8761 | — | Implemented |
| API Gateway | 8080 | — | Implemented |
| User Service | 8084 | not routed | Implemented (register only) |
| Product Service | 8081 | `/api/v1/products/**` | Implemented |
| Inventory Service | 8083 | `/api/v1/inventory/**` | Implemented |
| Order Service | 8082 | `/api/v1/orders/**` | Implemented |
| Payment Service | 8084* | `/api/v1/payments/**` | Implemented (simulated gateway) |

\* Payment Service and User Service both currently declare `server.port: 8084`. This only avoids colliding because Docker Compose runs each in its own container — fix before running them outside Compose.

Not yet built: Config Server, Auth Service, Cart Service, Notification Service, Redis.

## Architecture notes

- **No authentication.** None of the services have Spring Security, JWT, or API keys configured. Every endpoint is open. Do not expose the gateway port publicly without adding auth.
- **Order flow**: `POST /api/v1/orders` synchronously prices items via Product Service and reserves stock via Inventory Service's internal API, then creates a payment record via Payment Service. `POST /api/v1/orders/{id}/pay` triggers the (simulated) payment authorization.
- **Transactional outbox + Kafka**: on payment success, Order Service writes a `PaymentSucceededEvent` to its outbox table. A scheduled publisher (5s fixed delay) pushes pending outbox rows to the `order-events` Kafka topic. Inventory Service consumes it, confirms the reservation idempotently (tracked via a processed-events table), and writes its own outbox event (`InventoryConfirmedEvent`) back onto `order-events`. Order Service consumes that and marks the order `PAID`.
- **Compensation**: if payment or inventory reservation fails during order creation, previously reserved stock is released and the order is cancelled.
- **Internal-only endpoints** (`/api/v1/internal/inventory/*`, `/api/v1/internal/products/*`) are for service-to-service calls only — not routed through the gateway.

## Running locally

```bash
docker compose up -d --build
```

All requests should go through the gateway on `localhost:8080`, except User Service which is only reachable directly on `localhost:8084` (no gateway route configured yet).