# Kafka Connect & Debezium: CDC Architectural Guide

This guide systematizes the knowledge for configuring Kafka Connect with Debezium for Change Data Capture (CDC), focusing on local Docker environments and production-grade architectural patterns.

---

## 1. CDC vs. JDBC Source Connector
To stream data from a database (PostgreSQL) to Kafka, you have two primary options:

| Feature | JDBC Source Connector | Debezium (CDC) |
| :--- | :--- | :--- |
| **Mechanism** | **Polling:** Queries DB every X seconds. | **Log-based:** Reads DB Transaction logs (WAL). |
| **Integrity** | Can miss deletions or rapid updates. | Captures **every** change (Inserts, Updates, Deletes). |
| **Load** | Adds query load to the DB. | Extremely lightweight; no impact on query engine. |
| **Latency** | High (determined by polling interval). | Near real-time (milliseconds). |

**Architect's Recommendation:** Use **Debezium** for microservices (Outbox Pattern) and mission-critical data pipelines.

---

## 2. Infrastructure Setup (Docker Compose)

### Step 1: Configure PostgreSQL for CDC
PostgreSQL must be set to `logical` WAL level to allow Debezium to stream logs.
```yaml
postgres:
  image: postgres:15-alpine
  command: ["postgres", "-c", "wal_level=logical"]
  ports:
    - "5432:5432"
```

### Step 2: Add Debezium Connect Service
Use the `debezium/connect` image which comes pre-configured with required drivers.
```yaml
kafka-connect:
  image: debezium/connect:2.5
  environment:
    - BOOTSTRAP_SERVERS=kafka:29092
    - GROUP_ID=1
    - CONFIG_STORAGE_TOPIC=my_connect_configs
    - OFFSET_STORAGE_TOPIC=my_connect_offsets
    - STATUS_STORAGE_TOPIC=my_connect_statuses
  depends_on:
    - kafka
    - postgres
```

### Step 3: The "Golden Rule" of Docker Networking
**Never use `localhost`** inside connector configurations. 
*   **Wrong:** `"database.hostname": "localhost"` (points to the Connect container itself).
*   **Right:** `"database.hostname": "postgres"` (uses the Docker service name).
*   **Port:** Always use the **Internal** port (e.g., `5432`), not the host-mapped port (e.g., `54321`).

---

## 3. Configuration Methods

| Method | Best For | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Kafbat UI** | Local Debugging | Visual, easy to use. | Manual; no version control. |
| **REST API** | CI/CD & Scripts | Standard; scriptable. | Manual execution per restart. |
| **Init Sidecar** | Production/DevExp | Fully automated GitOps. | Setup complexity. |

### API Example (cURL)
```bash
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{
  "name": "cinemesh-user-logs-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname": "cinemesh_auth",
    "database.server.name": "cinemesh",
    "plugin.name": "pgoutput",
    "table.include.list": "public.user_logs"
  }
}'
```

---

## 4. Deep Dive: Architect's Perspective
### A. Avoiding "Dual Writes"
The biggest mistake developers make is trying to write to the DB and Kafka in the same Java method. If the DB write succeeds but Kafka is down, your system is now inconsistent.
*   **Architect's Tip:** Only write to your local DB (Business table + Log table) in one transaction. Let **Debezium** handle the Kafka propagation. This ensures **Atomic Consistency**.

### B. Poison Pills and Backpressure
If a consumer fails to process a message from Debezium, it might get stuck.
*   **Architect's Tip:** Use **Single Message Transforms (SMT)** to clean or flatten data before it hits Kafka. Also, always monitor your **Consumer Lag**. If Debezium is 1 million messages ahead of your service, your "real-time" system is actually an hour behind.

### C. Schema Evolution
What happens when you change a column name in PostgreSQL?
*   **Architect's Tip:** Integrate with a **Schema Registry** (like Confluent or Apicurio). This prevents your consumers from crashing when the DB schema changes upstream.

---

## 5. Troubleshooting Checklist
1.  **WAL Level:** Verify `SHOW wal_level;` returns `logical`.
2.  **Permissions:** Ensure the Postgres user has `REPLICATION` attributes.
3.  **Slot Name:** If a connector fails, check if a stale "Replication Slot" exists in Postgres (`select * from pg_replication_slots;`).
4.  **Network:** Can the Connect container ping the Postgres container by its service name?
