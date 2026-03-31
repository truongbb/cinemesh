<div align="center">
  <h1>🎬 Cinemesh</h1>
  <p><b>An Enterprise-Grade, Event-Driven Movie Ticketing Microservices Architecture</b></p>

  <a href="https://github.com/truongbb/cinemesh/actions"><img src="https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?style=for-the-badge&logo=githubactions"></a>
  <a href="#"><img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java"></a>
  <a href="#"><img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot"></a>
  <a href="#"><img src="https://img.shields.io/badge/Kubernetes-Ready-326ce5?style=for-the-badge&logo=kubernetes"></a>
  <a href="#"><img src="https://img.shields.io/badge/Kafka-Event%20Driven-black?style=for-the-badge&logo=apachekafka"></a>
</div>

<br/>

## 📖 Introduction

**Cinemesh** is a production-ready microservices system designed to handle high-concurrency movie ticket bookings. Built as a reference architecture, it demonstrates modern backend engineering practices including distributed transactions, event-driven communication, Change Data Capture (CDC), and zero-downtime Kubernetes deployments.

Whether you are here to review the codebase or learn how to string together a cloud-native architecture, this repository serves as a complete blueprint from local development to a fully automated GitOps pipeline.

## 🌱 Branching Strategy

To maintain high development velocity while ensuring production stability, we use a dual-branch strategy:

*   **`master` branch**: The **Development** branch. All new features, experimental code, and active development occur here. If you are a contributor, this is where you branch from.
*   **`main` branch**: The **Production/Deployment** branch. This branch represents the stable, production-ready state of the project. It triggers the automated CI/CD pipeline.

> [!IMPORTANT]
> **Always follow the `main` branch** for the most stable documentation, setup guides, and production-ready configurations.

## ✨ Core Features

* **🔐 Distributed Authentication:** Centralized user identity management.
* **🎟️ High-Concurrency Booking:** Distributed caching with Redis to prevent seat double-booking.
* **💳 Payment Gateway Integration:** Seamless webhook processing for VNPay IPN callbacks.
* **📬 Asynchronous Notifications:** Fault-tolerant email delivery via Kafka event streaming.
* **🔄 Change Data Capture (CDC):** Debezium captures database changes in real-time to synchronize microservice states.

## 🏗️ Architecture Overview

*(Recommendation: Add a link to an architecture diagram image here, e.g., `![Architecture Diagram](docs/images/architecture.png)`)*

Cinemesh breaks down the monolithic approach into strictly bounded contexts:

* **Auth Service:** Manages user registration, login, and JWT token issuance.
* **Booking Service:** Handles movie schedules, seat reservations, and interfaces with the caching layer.
* **Movie Service:** Manages movie metadata, genres, and durations.
* **Theater Service:** Manages theater rooms, seats, and layouts.
* **Payment Service:** Processes transactions and listens for external webhook confirmations.
* **Notification Service:** A consumer-only service that listens to Kafka topics to dispatch emails/SMS.

## 📂 Repository Structure

This project follows a Monorepo structure for ease of development and CI/CD orchestration.

```text
cinemesh/
├── .github/workflows/      # CI/CD Pipelines (Gatekeeper, Factory, Deploy)
├── common/                 # Shared libraries and utilities (DTOs, Exceptions)
├── config-files/           # Centralized Spring Cloud Config repository
├── infrastructure/         # Core system services
│   ├── config-server/      # Externalized configuration management
│   ├── discovery-server/   # Netflix Eureka service registry
│   └── cinemesh-gateway/   # Spring Cloud Gateway (Entry point)
├── services/               # The Microservices Source Code
│   ├── auth-service/       
│   ├── booking-service/    
│   ├── movie-service/
│   ├── theater-service/
│   ├── payment-service/    
│   └── notification-service/
├── docs/                   # Full Documentation Suite
│   ├── ci-cd/              # Pipeline breakdowns (Phases 1-3)
│   ├── development/        # In-depth architectural & integration guides
│   └── manual-deployment/  # Step-by-step Kubernetes/Infrastructure playbooks
└── docker-compose.yml      # Local infrastructure setup (Postgres, Kafka, Redis)
```

## 🚀 Getting Started (Local Development)

To run Cinemesh locally, follow this specific startup order to ensure all services can register and find their configuration.

### Prerequisites
* Docker & Docker Compose
* Java 17 (JDK)
* Maven 3.8+
* [Ngrok](https://ngrok.com/) (Only for testing VNPay webhooks locally)

### 1. Start the Shared Infrastructure
Navigate to the root directory and spin up PostgreSQL, Redis, Kafka, and Zookeeper:
```bash
docker-compose up -d
```

### 2. Start Infrastructure Services (The Backbone)
You must start these in order and wait for each to be fully healthy:
1.  **Config Server**: `cd infrastructure/config-server && mvn spring-boot:run` (Port 8888)
2.  **Discovery Server**: `cd infrastructure/discovery-server && mvn spring-boot:run` (Port 8761)
3.  **Cinemesh Gateway**: `cd infrastructure/cinemesh-gateway && mvn spring-boot:run` (Port 8080)

### 3. Run Business Microservices
Start any business service you need. For example:
```bash
cd services/auth-service && mvn spring-boot:run
```

### 🔬 Specialized Setup Guides
*   **Event Streaming (Kafka & Redis)**: We use Kafka for async communication and Redis for distributed locking. See the [Async vs Kafka Guide](docs/development/async-vs-kafka-architecture.md) and [Distributed Seat Locking](docs/development/distributed-seat-locking-strategies.md).
*   **CDC (Change Data Capture)**: We use Debezium to sync user logs. See the [Debezium Setup Guide](docs/development/kafka-connect-debezium-guide.md).
*   **VNPay Integration**: To test payments, you must expose your local IPN endpoint using Ngrok. Follow the [VNPay Integration Guide](docs/development/vnpay-integration-guide.md) for sandbox credentials and Ngrok setup.

## 🚢 CI/CD & Deployment

This repository implements a strict, 3-Phase CI/CD pipeline using GitHub Actions, triggered by pushes to the **`main` branch**:

1.  **[The Gatekeeper (CI)](docs/ci-cd/phase-1-gatekeeper.md):** Runs `mvn clean test` on an in-memory H2 database.
2.  **[The Factory](docs/ci-cd/phase-2-artifact-factory.md):** Builds Docker images, tags them with Git SHA, and pushes to DigitalOcean Container Registry.
3.  **[The Deployment](docs/ci-cd/phase-3-continuous-deployment.md):** Triggers a zero-downtime rolling update on the Kubernetes (DOKS) cluster.

*For manual Kubernetes setup, refer to the [Manual Deployment Playbook](docs/manual-deployment/00-playbook-doks-microservices.md).*

## 🤝 Contributing

This project is actively used as a training environment. If you find a bug or want to suggest an improvement:

1. Fork the Project from the `master` branch.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request to the `master` branch.

---
*Designed and built for scale.*
