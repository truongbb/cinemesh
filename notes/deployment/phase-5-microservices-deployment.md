# Phase 5: Microservices Deployment (The CI/CD Flow)

**Goal:** Package your Spring Boot applications as immutable Docker images and deploy them to the Kubernetes cluster using best-practice resource limits.

---

## Step 5.1: Build & Tag Docker Images
We use the standard Maven wrapper and Docker to build our artifacts.

1.  **Package JAR:**
    ```bash
    cd services/booking-service
    ./mvnw clean package -DskipTests
    ```
2.  **Build Image:**
    *Tag the image with the full DigitalOcean registry URL.*
    ```bash
    docker build -t registry.digitalocean.com/cinemesh-registry/booking-service:v1 .
    ```
3.  **Push Image:**
    ```bash
    doctl registry login
    docker push registry.digitalocean.com/cinemesh-registry/booking-service:v1
    ```

---

## Step 5.2: Update Configuration for Kubernetes (DNS)
Inside the cluster, services do not use `localhost`. We use K8S Internal DNS to resolve services by their name.

**Internal DB URLs:**
*   **PostgreSQL:** `jdbc:postgresql://postgres-postgresql.default.svc.cluster.local:5432/postgres`
*   **Redis:** `redis-master.default.svc.cluster.local:6379`
*   **Kafka:** `cinemesh-kafka-kafka-bootstrap.kafka.svc.cluster.local:9092`

---

## Step 5.3: Apply Kubernetes Deployment Manifests
For each service, create a `deployment.yaml` and a `service.yaml`.

**The "Safety Rule": Resource Limits**
*Architect's Note: On our 4GB nodes, if one service consumes too much RAM, the OS will "OOM-Kill" the pod, potentially taking Kafka down with it. Always define `requests` and `limits`.*

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: booking-service
spec:
  replicas: 2
  template:
    spec:
      containers:
        - name: booking-service
          image: registry.digitalocean.com/cinemesh-registry/booking-service:v1
          resources:
            requests: { memory: "400Mi", cpu: "200m" }
            limits: { memory: "600Mi", cpu: "500m" }
```

---

## Step 5.4: Professional Checklist: The Actuator Rule
Never deploy a Java app without the **Spring Boot Actuator**. We use its endpoints to communicate with K8S.

1.  **Liveness Probe:** `/actuator/health/liveness`. (If this fails, K8S restarts the Pod).
2.  **Readiness Probe:** `/actuator/health/readiness`. (K8S stops traffic if the service is booting or overloaded).

**Expert Tip:** In banking, we use **Rolling Updates**. By having `replicas: 2`, K8S can update `pod-1` to the new version while `pod-2` keeps handling traffic, resulting in **Zero Downtime**.
