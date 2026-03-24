# Phase 3: The Stateful Data Layer & Event Streaming

**Goal:** Deploy PostgreSQL, Redis, and an enterprise-grade Apache Kafka cluster to Kubernetes and establish local connectivity.

## Step 3.1: Install Helm & Deploy Databases
We use Helm and the Bitnami repository to deploy foundational databases. These commands automatically provision DigitalOcean Block Storage (PVCs).

**1. Setup Helm:**
```bash
brew install helm
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
```

**2. Deploy PostgreSQL:**
```bash
helm install postgres bitnami/postgresql \
  --set auth.postgresPassword=root \
  --set primary.persistence.size=8Gi
```

Create user and pass for all services (short way, in prod, each service has separated user and pass)
```bash
kubectl exec -it svc/postgres-postgresql -n default -- psql -U postgres

CREATE USER cinemesh WITH PASSWORD 'cinemesh123!@#';
ALTER USER cinemesh CREATEDB;
ALTER USER cinemesh SUPERUSER;
```

**3. Deploy Redis:**
```bash
helm install redis bitnami/redis \
  --set auth.enabled=false \
  --set master.persistence.size=2Gi
```

---

## Step 3.2: The Kafka Pivot & Troubleshooting
*Architect's Note: Initially, we attempted the Bitnami Kafka chart. Due to upstream image issues, we pivoted to the industry-standard Strimzi Operator (Operator Pattern).*

### 🛠️ Debugging Cheat Sheet
If a pod is stuck in `ImagePullBackOff` or `Pending`:
```bash
kubectl describe pod <pod-name>
```
*Check the `Events` at the bottom. If it says `UnsupportedKafkaVersionException`, update your YAML version.*

### 🧹 The "Nuclear Cleanup"
If a stateful install fails, you **must** delete the Persistent Volume Claims (PVCs) manually before retrying, or the new install will inherit the old broken data.
```bash
helm uninstall <release-name>
kubectl delete pvc -l app.kubernetes.io/name=<app-name>
```

---

## Step 3.3: Deploying Strimzi Kafka (Enterprise Standard)
**1. Install the Operator:**
```bash
helm install strimzi-operator strimzi/strimzi-kafka-operator --namespace kafka --create-namespace
```

**2. Apply Cluster Config (`strimzi-kafka.yaml`):**
*Uses KRaft mode and 4.2.0 version to match the latest Strimzi Operator requirements.*
```yaml
apiVersion: kafka.strimzi.io/v1
kind: KafkaNodePool
metadata:
  name: dual-role
  namespace: kafka
  labels:
    strimzi.io/cluster: cinemesh-kafka
spec:
  replicas: 3
  roles: [controller, broker]
  storage:
    type: jbod
    volumes: [{ id: 0, type: persistent-claim, size: 10Gi, deleteClaim: false }]
  resources:
    requests: { memory: 512Mi, cpu: "250m" }
    limits: { memory: 1Gi, cpu: "1000m" }
---
apiVersion: kafka.strimzi.io/v1
kind: Kafka
metadata:
  name: cinemesh-kafka
  namespace: kafka
  annotations:
    strimzi.io/node-pools: enabled
    strimzi.io/kraft: enabled
spec:
  kafka:
    version: 4.2.0
    listeners:
      - name: plain
        port: 9092
        type: internal
        tls: false
    config:
      offsets.topic.replication.factor: 3
      transaction.state.log.replication.factor: 3
      transaction.state.log.min.isr: 2
  entityOperator:
    topicOperator: {}
    userOperator: {}
```

---

## Step 3.4: Monitoring & Local Connectivity (The "Cloud" Way)

### 🔭 Using Lens to Observe the Cluster
Lens is your "Mission Control." Instead of typing commands, use Lens to:
1.  **View Health:** Go to **Workloads > Pods**. Look for Green circles. If Red/Yellow, click the Pod and scroll down to "Events" to see the error.
2.  **Check Logs:** Click a Pod, and click the **"Lines" icon** at the top right to stream live logs (useful for debugging Java/Kafka startup).
3.  **Terminal Access:** Click the **"Terminal" icon** to `exec` into a container (e.g., to run `bash` inside a Postgres pod).

### 🔌 Connecting Local Tools (Port Forwarding)
Because the DBs are private, you must tunnel into the cluster to use TablePlus/DBeaver.

**Method A: Lens (Visual)**
* Go to **Network > Services**.
* Find `postgres-postgresql` or `redis-master`.
* Click the **"Forward"** link in the Ports section. Lens will map a random local port to the DB.

**Method B: CLI (Static)**
*Keep these terminals open while you work:*
```bash
# Connect to Postgres (localhost:5432)
kubectl port-forward svc/postgres-postgresql 5432:5432 -n default

# Connect to Redis (localhost:6379)
kubectl port-forward svc/redis-master 6379:6379 -n default
```

**JDBC URL for Local Development:**
`jdbc:postgresql://localhost:5432/postgres` (User: `postgres`, Pass: `root`)

---

## Step 3.5: Kafka UI & GitOps Topics
**1. Deploy UI:** Use `provectuslabs/kafka-ui`. Set `KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS` to `cinemesh-kafka-kafka-bootstrap.kafka.svc.cluster.local:9092`.

**2. Create Topics via YAML:**
*Apply this to create topics without using the UI or Java code:*
```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: booking-events
  namespace: kafka
  labels:
    strimzi.io/cluster: cinemesh-kafka
spec:
  partitions: 3
  replicas: 3
```