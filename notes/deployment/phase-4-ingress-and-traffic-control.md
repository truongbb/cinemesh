# Phase 4: API Gateway & Observability

**Goal:** Deploy Kong Gateway as the secure entry point for the Cinemesh platform, configure global request tracing, and set up the Prometheus/Grafana monitoring stack.

## Architecture Decisions: Kong & Logging

### 1. DB-Less vs. DB-Backed Kong
Kong can run in two modes. We chose the **DB-Backed** mode for Cinemesh.
* **DB-Less (Declarative):** Kong stores its configuration purely in memory based on YAML files. It is lightweight and fast to deploy, but you cannot use the Kong Admin API to dynamically toggle plugins, and some advanced rate-limiting/auth plugins are restricted.
* **DB-Backed (PostgreSQL):** Kong connects to a database to store routes, consumers, and plugin configurations. We use this because it is the enterprise standard, allows for dynamic plugin management, and perfectly integrates with the Postgres instance we already have running in our cluster.

### 2. Logging Plugins: `file-log` vs. `http-log`
Kong offers multiple ways to handle traffic logs. We are using `file-log` for our K8s environment.
* **`http-log`:** Kong sends an HTTP POST request containing the log data to an external URL (like your company's `trace-service` or an ELK stack). Great for heavy enterprise analytics, but requires managing extra infrastructure.
* **`file-log` (Our Choice):** Kong writes the log data to a file path. By setting this path to `/dev/stdout`, we leverage the native Kubernetes logging engine. This allows us to instantly view formatted JSON logs directly inside the Lens Desktop app without standing up an ELK stack.

---

## Step 4.1: Database Preparation
Kong requires its own database and user inside our existing Postgres cluster to store its configuration.

**Action:**
1. Open Lens > Pods > find `postgres-postgresql-0`.
2. Open the Pod Terminal and run:
```bash
psql -U postgres
```
```sql
CREATE USER kong WITH PASSWORD 'kong_pass';
CREATE DATABASE kong OWNER kong;
\q
```

## Step 4.2: Install Kong (DB-Backed)
*Note: We strictly pin the version to `3.0.2` to avoid `404 Not Found` errors caused by incomplete "latest" image tags in the upstream Helm repository.*

**Action:**
```bash
helm install kong kong/kong \
  --version 3.0.2 \
  --namespace kong \
  --create-namespace \
  --set env.database=postgres \
  --set env.pg_host=postgres-postgresql.default.svc.cluster.local \
  --set env.pg_user=kong \
  --set env.pg_password=kong_pass \
  --set env.pg_database=kong \
  --set ingressController.installCRDs=false
```

**Verification:**
Watch the pods in Lens (`kubectl get pods -n kong -w`). You must wait for the `kong-migrations` pod to reach the **Completed** state before the main `kong-kong` pod will turn `Running`.

## Step 4.3: Retrieve Public IP & Test via cURL
DigitalOcean automatically provisions a Load Balancer for the Gateway.

**1. Get the IP:**
```bash
kubectl get svc kong-kong-proxy -n kong
```
*(Look for the `EXTERNAL-IP`. This is your public "Front Door".)*

**2. The cURL Test:**
Test that Kong is actively receiving traffic and protecting the cluster:
```bash
curl -i http://<YOUR_EXTERNAL_IP>
```
**Expected Output:** You should receive a `HTTP/1.1 404 Not Found` with the body `{"message":"no Route matched with those values"}`. This is a **success**—it means you reached Kong, but Kong rightfully blocked the request because no routing rules exist yet.

## Step 4.4: Global Tracing (Correlation ID)
Inject a unique trace ID into every request so it can be tracked across all Cinemesh microservices (Auth -> Booking -> Kafka).

**Action (`correlation-id.yaml`):**
```yaml
apiVersion: [configuration.konghq.com/v1](https://configuration.konghq.com/v1)
kind: KongClusterPlugin
metadata:
  name: global-correlation-id
  annotations:
    kubernetes.io/ingress.class: kong
  labels:
    global: "true"
config:
  header_name: X-Cinemesh-Trace-Id
  generator: uuid#counter
  echo_downstream: true
plugin: correlation-id
```
```bash
kubectl apply -f correlation-id.yaml
```

## Step 4.5: Detailed Logging to Stdout
Output all traffic data to the container console for easy debugging in Lens. This will automatically include the `X-Cinemesh-Trace-Id` we just created.

**Action (`kong-logs.yaml`):**
```yaml
apiVersion: [configuration.konghq.com/v1](https://configuration.konghq.com/v1)
kind: KongClusterPlugin
metadata:
  name: global-detailed-logging
  annotations:
    kubernetes.io/ingress.class: kong
  labels:
    global: "true"
config: 
  path: /dev/stdout
  reopen: false
plugin: file-log
```
```bash
kubectl apply -f kong-logs.yaml
```

**Developer Workflow:** Open Lens, go to the `kong-kong` pod logs, fire a Postman request, and search the log stream for your specific `X-Cinemesh-Trace-Id`.

## Step 4.6: Observability Stack (Prometheus/Grafana)
Provides real-time visibility into the health of Java services and infrastructure.

**Action:**
```bash
helm install monitoring prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace
```

**How to Access Grafana:**
1. Use **Lens** -> Network -> Services -> `monitoring-grafana`.
2. Port-forward `80` to a local port.
3. Default login: User: `admin` | Pass: `prom-operator`.

