# CommerceFlow

A production-style e-commerce platform built using Spring Boot Microservices.

## Tech Stack

- Java 21
- Spring Boot
- Spring Cloud (Eureka, Gateway)
- PostgreSQL
- Kafka (transactional outbox pattern)
- Docker / Docker Compose
- Kubernetes (k3s, manifests in `k8s/`)
- GitHub Actions CI/CD (build + push images to GHCR, deploy to EC2 and k3s)

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

## Running on Kubernetes (k3s)

Manifests live in `k8s/`. The namespace is `commerceflow`.

### DB configuration: ConfigMap + Secret per service

Each service that connects to PostgreSQL has its datasource split into two parts:

- A **ConfigMap** (`<service>-config.yml`) holding the non-secret datasource values
  (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`), wired into the deployment via `envFrom`.
- A **Secret** (`<service>-db-secret`) holding `SPRING_DATASOURCE_PASSWORD`, wired in via
  `env` → `secretKeyRef`.

This applies to `product`, `inventory`, `order`, `payment`, and `user` services.

The secrets are **not** checked into the repo — create them out-of-band before deploying:

```bash
for SVC in product inventory order payment user; do
  kubectl create secret generic ${SVC}-db-secret \
    -n commerceflow \
    --from-literal=SPRING_DATASOURCE_PASSWORD=root
done
```

### Deploy

```bash
kubectl apply -f k8s/ -n commerceflow
```

Apply the ConfigMaps (and create the secrets above) before the deployments roll, otherwise
pods stay in `CreateContainerConfigError` waiting on the missing `configMapRef`/`secretKeyRef`.

The gateway is exposed via a `NodePort` Service and a Traefik `Ingress` (`k8s/gateway-ingres.yml`,
path `/`). Reach the API through the gateway's NodePort, e.g.:

```bash
curl http://localhost:<nodeport>/api/v1/products
```

### CI/CD

`.github/workflows/ci.yml` builds and tests all services against an ephemeral Postgres, and on
push to `main` pushes images to GHCR. Two deploy jobs then run (both `needs: build`):

- `deploy` — SSHes to the EC2 host and runs `docker compose pull && up -d`.
- `deploy-kubernetes` — SSHes to the k3s EC2 host, `git pull`s, `kubectl apply -f k8s/`, and waits
  on `rollout status` for each deployment.
