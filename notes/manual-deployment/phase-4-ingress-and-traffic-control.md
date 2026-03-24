# Phase 4: API Gateway (Kong) & Traffic Management

**Objective:** Deploy a DB-backed Kong API Gateway into the Kubernetes cluster, integrate it with our existing PostgreSQL infrastructure, and configure visual traffic routing via Kong Manager.

### 1. Installing DB-Backed Kong (Shared Infrastructure)
To optimize resources, we do not spin up a second PostgreSQL pod for Kong. Instead, we create a dedicated logical database inside the existing `postgres-postgresql` pod.

**Step 1.1: Prepare the Database**
Access the existing PostgreSQL pod to create Kong's schema:
```bash
kubectl exec -it svc/postgres-postgresql -n default -- psql -U postgres
```
Run the SQL commands:
```sql
CREATE USER kong WITH PASSWORD 'kong_pass';
CREATE DATABASE kong OWNER kong;
\q
```

**Step 1.2: Helm Installation (The `default` Namespace Strategy)**
We install Kong directly into the `default` namespace alongside the microservices. This eliminates cross-namespace DNS complexity.

```bash
helm install kong kong/kong -n default \
  --set postgresql.enabled=false \
  --set env.database=postgres \
  --set env.pg_host=postgres-postgresql \
  --set env.pg_port=5432 \
  --set env.pg_user=kong \
  --set env.pg_password=kong_pass \
  --set env.pg_database=kong \
  --set admin.enabled=true \
  --set admin.http.enabled=true \
  --set admin.type=ClusterIP
```
*Wait for `kong-kong-init-migrations` to complete and the proxy pods to show as `Running`.*

---

### 2. Accessing Kong Manager (UI) & Admin API
Kong Manager is a frontend React app that relies entirely on the backend Admin API. You must open tunnels to both simultaneously.

**Terminal 1 (The UI):**
```bash
kubectl port-forward svc/kong-kong-manager 8002:8002 -n default
```
**Terminal 2 (The Admin API - "The Brain"):**
```bash
kubectl port-forward svc/kong-kong-admin 8001:8001 -n default
```
Access the UI via your browser at: `http://localhost:8002`

---

### 3. Configuring Services & Routes (The DNS Deep Dive)
In the Kong UI, you map internal microservices (Services) to external entry points (Routes).

#### The Namespace DNS Rule
Kubernetes uses Fully Qualified Domain Names (FQDN) to route internal traffic. The format is:
`<service-name>.<namespace>.svc.cluster.local`

* **Cross-Namespace Routing:** If Kong is in the `kong` namespace and your app is in `default`, Kong's host field **must** be `auth-service.default.svc.cluster.local`. If you just type `auth-service`, Kong searches its own isolated "room" and fails to find it.
* **Same-Namespace Routing (Our Setup):** Because we installed Kong into the `default` namespace alongside the apps, they share the same local network context. You can bypass the FQDN completely.

#### Creating the Service in Kong UI
* **Name:** `auth-service`
* **Protocol:** `http`
* **Host:** `auth-service` *(Valid because Kong and the app share the `default` namespace).*
* **Port:** `80`
    * *Architect Note:* Why port 80 and not 8080? Your Kubernetes `Service.yaml` acts as a port translator. It listens on K8s standard Port `80` and secretly forwards traffic to the Pod's targetPort `8080`. Kong only needs to know the K8s Service port.

#### Creating the Route in Kong UI
* Navigate to the newly created `auth-service` -> Routes -> New Route.
* **Name:** `auth-route`
* **Paths:** `/auth`
* **Strip Path:** `Yes` *(Removes `/auth` before hitting the Spring Boot container, so Java just sees `/api/...`).*

---

### 4. Testing Kong via cURL
To verify the gateway is successfully catching traffic and routing it to the Spring Boot pods, hit the Kong Proxy LoadBalancer directly.

Find the Proxy IP/Port:
```bash
kubectl get svc -n default
# Look for kong-kong-proxy (e.g., localhost:80 or your DigitalOcean External IP)
```

Run the cURL test against the Route path you defined:
```bash
curl -i -X GET http://<PROXY_IP_OR_LOCALHOST>/auth/api/v1/health
```
*You should receive a `200 OK` from your Spring Boot application.*

---

### 5. Configuring Kong Logging Plugins
Kong excels at observability. You can apply plugins globally (to all traffic) or locally (to specific services).

Navigate to **Plugins -> Add Plugin** in the UI.

#### A. HTTP-Log Plugin (For Log Aggregation)
Used to stream access logs to an external aggregator like ELK, Splunk, or Datadog.
* **http_endpoint:** `http://logstash-service.default.svc.cluster.local:8080/logs`
* **method:** `POST`
* **timeout:** `10000`

#### B. File-Log Plugin (For Local Debugging)
Used to write traffic logs directly to the container's file system.
* **path:** `/dev/stdout` *(Standard K8s practice: writing to stdout allows `kubectl logs` to capture the output seamlessly).*
* Alternatively, use `/tmp/kong-access.log` if you intend to `kubectl exec` into the pod to read them manually.

---

### 6. Troubleshooting Guide

* **UI Error: `ERR_CONNECTION_REFUSED` on /services**
    * *Cause:* The Kong Manager UI (8002) is running, but the backend Admin API (8001) tunnel is down.
    * *Fix:* Ensure `kubectl port-forward svc/kong-kong-admin 8001:8001` is actively running in a separate terminal.
* **Helm Error: `conflict occurred... ValidatingWebhookConfiguration`**
    * *Cause:* A cluster-scoped security webhook from a previous failed or DB-less installation was left behind.
    * *Fix:* Manually delete the ghost resource: `kubectl delete ValidatingWebhookConfiguration kong-kong-kong-validations`.
* **Helm Error: `incompatible types for comparison: int64 and string`**
    * *Cause:* Helm interprets numeric tags (like `15`) as integers, but the template requires a string.
    * *Fix:* Use the `--set-string` flag for version numbers (e.g., `--set-string postgresql.image.tag=15`).

---

### 7. The 20-Year SA & DevOps Perspective

As a Senior Architect, here is how you should think about the system you just built:

1.  **ClickOps vs. GitOps (The Evolution):** Using the Kong UI ("ClickOps") is phenomenal for rapid prototyping, learning the gateway, and testing plugin configurations. However, for production stability, your ultimate goal should be "GitOps." Eventually, you will export the Kong configurations you made in the UI into a declarative `kong.yaml` file and deploy it via CI/CD (using decK). This ensures your API gateway configuration is version-controlled just like your Java code.
2.  **The Database Connection Penalty:** By pointing Kong to your existing `postgres-postgresql` pod, you saved hundreds of megabytes of RAM. But beware of connection exhaustion. Kong proxy nodes open multiple persistent connections to Postgres. If you scale Kong to 5 replicas, and your microservices scale to 10 replicas, you could hit Postgres' max connection limit. Keep an eye on this; the next architectural step would be injecting a lightweight connection pooler like **PgBouncer**.
3.  **Namespace Isolation vs. Convenience:** Putting Kong in the `default` namespace makes DNS resolution beautifully simple (`auth-service`). However, strict enterprise compliance often mandates a DMZ (Demilitarized Zone) architecture. In the future, you may need to move Kong back to an isolated namespace (`ingress-system`) and use K8s `ExternalName` aliases to securely bridge the network gap. The approach we took today prioritizes developer velocity for a mid-sized platform.


