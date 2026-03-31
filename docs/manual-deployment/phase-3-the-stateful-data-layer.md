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

## Step 3.5: Building Kafka Connect with Debezium (Strimzi Native Build)
Instead of running manual `docker build` commands, we use Strimzi's internal Builder Pod to dynamically inject the Debezium Postgres plugin into Kafka Connect and push it to the DigitalOcean Container Registry.

**1. Create a DigitalOcean Push Token:**
Generate a Personal Access Token in DO with **BOTH Read and Write** scopes.
Create the Kubernetes secret to allow the Strimzi builder to push the image:
```bash
kubectl create secret docker-registry do-push-secret \
  --docker-server=registry.digitalocean.com \
  --docker-username="<YOUR_EMAIL>" \
  --docker-password="<YOUR_READ_WRITE_TOKEN>" \
  -n kafka
```

**2. Deploy KafkaConnect Resource:**
Apply this YAML to trigger the build. *Note: `use-connector-resources: "true"` explicitly disables the UI from creating connectors, enforcing a strict GitOps methodology.*
```yaml
---
apiVersion: kafka.strimzi.io/v1
kind: KafkaConnect
metadata:
  name: cinemesh-kafka-connect
  namespace: kafka
  annotations:
    strimzi.io/use-connector-resources: "true"
spec:
  version: 4.2.0
  replicas: 1
  bootstrapServers: cinemesh-kafka-kafka-bootstrap.kafka.svc.cluster.local:9092

  build:
    output:
      type: docker
      image: registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:kafka-connect-debezium-v2
      pushSecret: do-push-secret
    plugins:
      - name: debezium-postgres-connector
        artifacts:
          - type: tgz
            url: https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.4.0.Final/debezium-connector-postgres-2.4.0.Final-plugin.tar.gz

  template:
    pod:
      imagePullSecrets:
        - name: cinemesh-registry-truongbb

  groupId: cinemesh-connect-cluster
  offsetStorageTopic: cinemesh-connect-cluster-offsets
  configStorageTopic: cinemesh-connect-cluster-configs
  statusStorageTopic: cinemesh-connect-cluster-status

  config:
    config.storage.replication.factor: -1
    offset.storage.replication.factor: -1
    status.storage.replication.factor: -1
```

---

## Step 3.6: Deploying the Debezium Connector (GitOps)
Connectors are deployed as Kubernetes Custom Resources (`KafkaConnector`), **not** via the UI or REST API.

**Apply `auth-db-connector.yml`:**
```yaml
---
apiVersion: kafka.strimzi.io/v1
kind: KafkaConnector
metadata:
  name: auth-service-postgres-connector
  namespace: kafka
  labels:
    # 🌟 MUST perfectly match the metadata.name of your KafkaConnect cluster pod configured in kafka-connect-build.yml
    strimzi.io/cluster: cinemesh-kafka-connect
spec:
  class: io.debezium.connector.postgresql.PostgresConnector
  tasksMax: 1
  config:
    # ==========================================
    # 1. DATABASE CONNECTION & CORE SETTINGS
    # ==========================================
    plugin.name: pgoutput
    slot.name: "auth_service_slot"
#    database.server.name: auth-service-postgres-connector
    topic.prefix: auth-service-postgres-connector

    # Cross-Namespace Routing: Connect is in 'kafka', Postgres is in 'default'
    database.hostname: postgres-postgresql.default.svc.cluster.local
    database.port: "5432"
    database.user: cinemesh
    database.password: "cinemesh123!@#"
    database.dbname: cinemesh_auth_db

    # ==========================================
    # 2. TARGET TABLES
    # ==========================================
    # Only capture changes from these specific tables to save bandwidth
    table.include.list: public.user_logs

    # ==========================================
    # 3. PAYLOAD FORMATTING
    # ==========================================
    # Setting schemas.enable to 'false' prevents Debezium from wrapping every single
    # JSON message with a massive schema definition, keeping the payload tiny and fast.
    key.converter.schemas.enable: false
    value.converter.schemas.enable: false
    key.converter: org.apache.kafka.connect.storage.StringConverter
    value.converter: org.apache.kafka.connect.json.JsonConverter

    # ==========================================
    # 4. TOPIC ROUTING (TRANSFORMS)
    # ==========================================
    # By default, Debezium names topics like "serverName.schemaName.tableName"
    # We use a RegexRouter to cleanly rename the output to match our custom topic.
    transforms: routeUserLogs
    transforms.routeUserLogs.type: org.apache.kafka.connect.transforms.RegexRouter
    transforms.routeUserLogs.regex: (.*).user_logs
    transforms.routeUserLogs.replacement: cinemesh.auth-service.user-logs

    # ==========================================
    # 5. ENTERPRISE SAFETY NET (THE HEARTBEAT)
    # ==========================================
    # Forces a database write every 60 seconds. This advances the LSN (Log Sequence Number)
    # and prevents PostgreSQL from holding onto old WAL files and running out of disk space.
    heartbeat.topics.prefix: debezium-heartbeat
    heartbeat.interval.ms: "60000"
    heartbeat.action.query: "INSERT INTO public.kafka_connect_heartbeats (created_date_time, connector_name) VALUES (now(), 'auth-service-postgres-connector')"
    # connector_name value must be matched with metadata.name and database.server.name/topic.prefix in the begining of this file

```

---

## Step 3.7: Kafka UI (Stable ConfigMap Implementation)
*Architect's Note: Do not use the `latest` Docker tag for Kafka UI, as it contains bleeding-edge UI bugs. Furthermore, Spring Boot struggles to parse complex nested array environment variables (like `KAFKA_CLUSTERS_0_KAFKACONNECT...`). We bypass this by injecting a pure `ConfigMap`.*

**Deploy `kafka-ui.yml`:**
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka-ui
  namespace: kafka
spec:
  replicas: 1
  selector:
    matchLabels:
      app: kafka-ui
  template:
    metadata:
      labels:
        app: kafka-ui
    spec:
      containers:
        - name: kafka-ui
          # 🌟 THE FIX: We are abandoning the buggy 'latest' branch for a stable release.
          image: provectuslabs/kafka-ui:v0.7.2
          ports:
            - containerPort: 8080
          resources:
            requests:
              memory: 256Mi
              cpu: 100m
            limits:
              memory: 512Mi
              cpu: 500m
          env:
            - name: KAFKA_CLUSTERS_0_NAME
              value: "Cinemesh-Cluster"
            - name: KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS
              value: "cinemesh-kafka-kafka-bootstrap:9092"

            # The foolproof environment variables:
            - name: KAFKA_CLUSTERS_0_KAFKACONNECT_0_NAME
              value: "cinemesh-connect"
            - name: KAFKA_CLUSTERS_0_KAFKACONNECT_0_ADDRESS
              value: "http://cinemesh-kafka-connect-connect-api:8083"

            - name: DYNAMIC_CONFIG_ENABLED
              value: "true"
---
apiVersion: v1
kind: Service
metadata:
  name: kafka-ui-service
  namespace: kafka
spec:
  type: ClusterIP
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: kafka-ui

```

---

## Step 3.8: Common Architecture Bugs & Fixes (The "Ghost" Traps)

1.  **Strimzi Builder Pod Fails (`401 Unauthorized`):**
    * *Cause:* The DigitalOcean token used in the `pushSecret` lacks `Write` permissions.
    * *Fix:* Generate a new token with Read/Write scopes, recreate the Kubernetes secret, and increment the image tag version (e.g., `v3` to `v4`) in your YAML to force the operator to trigger a new build.
2.  **Connector Rejected by Strimzi (Silent Failure):**
    * *Cause:* Debezium 2.x introduced a breaking change. If your YAML uses `database.server.name`, the API rejects it with a `400 Bad Request`.
    * *Fix:* Replace `database.server.name` with `topic.prefix`. Verify rejection reasons via: `kubectl get kafkaconnector <name> -n kafka -o yaml | tail -n 20`.
3.  **Data Not Flowing to Topic:**
    * *Cause:* Typos in `table.include.list`. Postgres uses `snake_case` (e.g., `user_logs`), but configs often mistakenly use hyphens (`user-logs`).
    * *Fix:* Ensure exact matching with the database table name.
4.  **Kafka UI Fails to Display "Kafka Connect" Tab:**
    * *Cause:* The UI pod fails to connect to the backend API due to caching (`DYNAMIC_CONFIG_ENABLED: "true"`), Alpine Linux DNS resolution issues, or bugs in the `latest` image.
    * *Fix:*
        * Pin image to `v0.7.2`.
        * Use a `ConfigMap` for configuration instead of environment variables.
        * Use short-name DNS (e.g., `http://cinemesh-kafka-connect-connect-api:8083`) instead of full `cluster.local` paths.
        * Verify API health directly from the terminal bypassing the UI entirely:
          `kubectl port-forward svc/cinemesh-kafka-connect-connect-api 8083:8083 -n kafka` -> `curl -s http://localhost:8083/connectors`.

