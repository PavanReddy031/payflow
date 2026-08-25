# PayFlow Project — Context Prompt
## Copy this entire prompt and paste it at the start of any new Claude chat

---

## Who I am

My name is Pavan Katasani. I am a Software Engineer at Versovate, Bengaluru, with 2+ years of experience (Jun 2024 – present).

**Current skills:**
- Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate
- JWT authentication, RBAC, REST APIs
- Node.js, Express.js, Sequelize
- MySQL, PostgreSQL, MongoDB
- Docker, Docker Compose, Jenkins CI/CD, Nginx
- Azure VM, Azure Blob Storage, Linux server management
- Payment gateway integration (Authorize.Net)
- Third-party REST API integration (WhatsApp Business, OTP, vehicle verification)
- 300+ DSA problems solved (LeetCode, GFG, HackerRank)

**What I do NOT have yet (building through this project):**
- Apache Kafka / event-driven architecture
- Redis (caching, rate limiting, idempotency)
- Microservices with Spring Cloud (Gateway, Eureka, Config Server)
- AWS (EC2, RDS, ElastiCache, EKS, ECR, S3)
- Kubernetes (pods, deployments, services, ConfigMaps, HPA)
- Observability (Micrometer, Prometheus, Grafana, OpenTelemetry, Jaeger)
- Distributed tracing
- Integration testing with Testcontainers
- GitHub Actions CI/CD
- LLD design patterns in production code (Strategy, Factory, Builder, Outbox, Circuit Breaker)
- Spring AI / LLM integration

---

## The Project: PayFlow

A distributed payment processing platform — a simplified Razorpay/Stripe backend. Built with microservices architecture to deeply learn every skill missing from my resume.

**Goal:** This is NOT a tutorial project. Every design decision must be production-grade. I want to be able to answer the toughest interview questions about every component I build.

---

## Architecture — 5 Microservices

### 1. API Gateway Service (Spring Cloud Gateway)
- Routes all incoming requests to downstream services
- JWT authentication filter (validate token before forwarding)
- Rate limiting per merchant using Redis token bucket algorithm
- Request/response logging for audit trail

### 2. Payment Service (Core)
- Accepts payment initiation requests from merchants
- Validates request, creates Transaction record in PostgreSQL
- Implements idempotency keys (Redis TTL-based) to prevent duplicate charges
- Publishes `payment.initiated` event to Kafka
- Transaction states: INITIATED → PROCESSING → SUCCESS / FAILED / TIMEOUT

### 3. Transaction Processing Service
- Consumes `payment.initiated` events from Kafka
- Simulates payment processor (configurable success/failure/timeout rates)
- Publishes `payment.succeeded` or `payment.failed` to Kafka
- Retry logic with exponential backoff
- Dead Letter Queue (DLQ) for permanently failed transactions
- Resilience4j circuit breaker for downstream calls

### 4. Notification Service
- Consumes payment events from Kafka
- Sends email notifications (JavaMailSender) and webhook callbacks to merchants
- Implements outbox pattern to guarantee delivery
- Tracks notification delivery status

### 5. Analytics Service
- Consumes all payment events from Kafka
- Real-time aggregation using Redis (transactions per second, success rate, volume per merchant)
- Exposes dashboard API: success rate, avg processing time, total volume, top merchants
- Stores historical data in PostgreSQL for trend analysis

---

## Infrastructure Stack

| Layer | Technology |
|---|---|
| API Gateway | Spring Cloud Gateway |
| Service discovery | Eureka (Spring Cloud Netflix) |
| Config management | Spring Cloud Config Server |
| Message broker | Apache Kafka + Zookeeper |
| Cache / rate limit / idempotency | Redis |
| Database | PostgreSQL (one DB per service) |
| Containerization | Docker + Docker Compose (local) |
| Orchestration | Kubernetes — AWS EKS (prod) / minikube (local) |
| Cloud | AWS: EC2, RDS, ElastiCache, EKS, ECR, S3 |
| Metrics | Micrometer + Prometheus |
| Dashboards | Grafana |
| Distributed tracing | OpenTelemetry + Jaeger |
| CI/CD | GitHub Actions → ECR → EKS |
| Testing | JUnit 5 + Mockito + Testcontainers |
| AI feature | Spring AI + OpenAI API (fraud pattern detection) |

---

## Key Design Patterns to implement (LLD)

- **Outbox pattern** — atomic DB write + Kafka publish without distributed transaction
- **Idempotency pattern** — Redis-based idempotency keys with TTL
- **Token bucket** — Redis-based rate limiting at API Gateway
- **Strategy pattern** — pluggable payment processor (mock vs real)
- **Factory pattern** — notification type creation (email, webhook, SMS)
- **Builder pattern** — complex Transaction object construction
- **Circuit breaker** — Resilience4j wrapping payment processor calls
- **Saga pattern** — managing distributed transaction across services via Kafka events
- **Dead letter queue** — handling permanently failed Kafka messages
- **Database per service** — each microservice owns its own PostgreSQL schema

---

## Kafka Topic Design

| Topic | Producer | Consumer | Partitioned by |
|---|---|---|---|
| `payment.initiated` | Payment Service | Transaction Processing Service | merchant_id |
| `payment.succeeded` | Transaction Processing Service | Notification Service, Analytics Service | merchant_id |
| `payment.failed` | Transaction Processing Service | Notification Service, Analytics Service | merchant_id |
| `payment.initiated.DLQ` | Transaction Processing Service | Manual review / alerting | - |
| `notification.sent` | Notification Service | Analytics Service | merchant_id |

---

## Database Schema (high level)

**Payment Service DB:**
- `transactions` (id, merchant_id, amount, currency, status, idempotency_key, created_at, updated_at)
- `merchants` (id, name, api_key, webhook_url, rate_limit_per_min)

**Transaction Processing Service DB:**
- `processing_attempts` (id, transaction_id, attempt_number, status, error_message, processed_at)

**Notification Service DB:**
- `notifications` (id, transaction_id, type, status, payload, sent_at, retry_count)
- `outbox_events` (id, aggregate_id, event_type, payload, processed, created_at)

**Analytics Service DB:**
- `merchant_metrics` (id, merchant_id, date, total_volume, success_count, failure_count, avg_processing_ms)

---

## Build Order (week by week)

| Week | Focus |
|---|---|
| Week 1 | Payment Service alone — REST API, PostgreSQL, JUnit tests, idempotency keys in Redis |
| Week 2 | Kafka integration — Payment Service publishes, Transaction Processing Service consumes |
| Week 3 | API Gateway + rate limiting (Redis token bucket) + Notification Service |
| Week 4 | Analytics Service + Redis aggregation + Docker Compose (all services together) |
| Week 5–6 | Observability — Micrometer, Prometheus, Grafana, OpenTelemetry + Jaeger |
| Week 7–8 | AWS deployment — EKS, RDS, ElastiCache, GitHub Actions CI/CD |
| Week 9+ | Spring AI fraud detection feature + README + architecture diagram + polish |

---

## What I want from Claude in this project

1. **Don't give me basic explanations.** I want production-grade code and reasoning.
2. **Explain the "why" behind every design decision** — I need to defend these in interviews.
3. **When I ask how to implement something**, give me the actual code, not pseudocode.
4. **Point out when I am doing something wrong or non-production-grade** — don't let me build bad habits.
5. **For every pattern I implement**, explain what interview questions it will help me answer.
6. **Help me think like a senior engineer**, not just a developer who follows tutorials.

---

## Current status
- [ ] Week 1 not started yet
- Starting from scratch today
- IDE: IntelliJ IDEA
- Build tool: Maven
- Java version: 17
- Spring Boot version: 3.x
