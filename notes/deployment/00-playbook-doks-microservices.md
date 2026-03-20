# Deployment Playbook: Microservices on DigitalOcean Kubernetes (DOKS)

This playbook systematizes the end-to-end process of deploying the Cinemesh microservices platform to a managed Kubernetes environment. It transitions from local Docker Compose development to a production-grade, distributed architecture.

---

## 1. Financial & Resource Planning (FinOps)

In a professional DevOps environment, we never provision without a budget. Kubernetes requires a baseline of "System Overhead" (Kubelet, Proxy, CNI) before your apps even start.

### Tier 1: Development/Staging (The "Lean" Cluster)
*   **Worker Nodes:** 3x `s-2vcpu-4gb` ($24/ea) = **$72/mo**
*   **Networking:** 1x DO Node Balancer = **$12/mo**
*   **Registry:** Basic Container Registry = **$5/mo**
*   **Total:** **~$89.00 / month**
*   **Architect's Constraint:** Java apps **must** use `-Xmx300m` or lower. Kafka must be tuned for minimal memory.

### Tier 2: Production Baseline (High Traffic)
*   **Worker Nodes:** 3x `s-4vcpu-8gb` ($48/ea) = **$144/mo**
*   **Total:** **~$161.00 / month**
*   **Benefit:** Allows for JVM "Bursting" and handles Kafka rebalancing without CPU starvation.

---

## 2. Infrastructure Provisioning (The Foundation)

### Phase 1: Cluster Setup
1.  **Authentication:** `doctl auth init` with a secure API Token.
2.  **Creation:** 
    ```bash
    doctl kubernetes cluster create cinemesh-cluster --region sgp1 --size s-2vcpu-4gb --count 3
    ```
3.  **Governance (Expert Tip):** Immediately create a dedicated **Namespace**. Never deploy business logic to `default`.
    ```bash
    kubectl create namespace cinemesh-app
    ```

### Phase 2: Secure Registry Integration
To prevent `ImagePullBackOff` errors, the cluster must be "linked" to your private vault.
1.  **Registry:** `doctl registry create cinemesh-registry`
2.  **Integration:** `doctl kubernetes cluster registry integration cinemesh-cluster`

---

## 3. The Stateful Data Layer (Helm)

We use **Helm** to manage complex "Off-the-shelf" infrastructure. In banking, we prioritize **Persistence** over everything.

### Step 1: Databases & Cache
*   **PostgreSQL:**
    ```bash
    helm install postgres bitnami/postgresql -n cinemesh-app \
      --set auth.postgresPassword=secure_pass \
      --set primary.persistence.size=8Gi
    ```
*   **Redis (Distributed Lock):**
    ```bash
    helm install redis bitnami/redis -n cinemesh-app \
      --set auth.enabled=false \
      --set master.persistence.size=2Gi
    ```

### Step 2: Apache Kafka (KRaft Mode)
To save RAM, we use **KRaft** (Kafka Raft), which eliminates the need for Zookeeper.
```bash
helm install kafka bitnami/kafka -n cinemesh-app \
  --set kraft.enabled=true \
  --set zookeeper.enabled=false \
  --set persistence.size=10Gi
```

---

## 4. Ingress & Traffic Control (Kong)

We use **Kong** as our Ingress Controller to handle SSL termination, Rate Limiting, and Distributed Tracing.

1.  **Install Kong:**
    ```bash
    helm install kong kong/kong -n cinemesh-app --set proxy.type=LoadBalancer
    ```
2.  **Correlation ID (Tracing):** Apply a `KongPlugin` to ensure every request enters the system with a unique `X-Request-ID`. This is mandatory for debugging across 10+ pods.

---

## 5. Microservice Deployment (The CI/CD Flow)

### Step 1: Build & Push
The "Gold Standard" is a single immutable artifact pushed to the registry.
```bash
# From service directory
./mvnw clean package -DskipTests
docker build -t registry.digitalocean.com/cinemesh-registry/booking-service:v1 .
docker push registry.digitalocean.com/cinemesh-registry/booking-service:v1
```

### Step 2: Resource Constraints (Expert Tip)
In K8S, an "Unlimited" container is a "Suicide Note". Always define **Requests** and **Limits**.
```yaml
resources:
  requests:
    memory: "400Mi"
    cpu: "200m"
  limits:
    memory: "600Mi"
    cpu: "500m"
```

### Step 3: Probes & Health Checks
A service is not "Up" just because the process started. 
*   **Liveness Probe:** "Am I dead?" (Restarts the pod).
*   **Readiness Probe:** "Am I ready for traffic?" (Used during rolling updates).
*   **Path:** `/actuator/health` (Spring Boot).

---

## 6. Senior Architect's Post-Deployment Checklist

### 1. Database Schema Migrations
Never run `create_databases.sql` manually in production. Use **Flyway** or **Liquibase** as a `Job` or `InitContainer` to ensure the schema is updated *before* the app boots.

### 2. Secret Management
**Never** store passwords in `deployment.yaml`.
*   Use `kubectl create secret generic db-credentials --from-literal=password=...`
*   Reference the secret via `envFrom` in your pod spec.

### 3. Log Aggregation
`kubectl logs` is for developers. For Production, you need a "Centralized Brain":
*   Install **FluentBit** to ship logs to **Elasticsearch/Kibana** or **Loki**.
*   Ensure all logs are in **JSON format** for structured searching.

### 4. The "Zero-Downtime" Test
Verify your deployment strategy. Use `kubectl rollout status deployment/booking-service`. If a new version fails to start, K8S should automatically halt the rollout and keep the old version running.

---

## 7. Summary
This playbook ensures that Cinemesh isn't just "running in a container," but is operating as a **resilient, traceable, and secure** financial-grade platform. 
*   **Traffic** enters through **Kong**.
*   **State** is persisted in **DO Block Storage**.
*   **Logic** is orchestrated by **K8S Scheduler**.
*   **Observability** is guaranteed by **Correlation IDs**.
