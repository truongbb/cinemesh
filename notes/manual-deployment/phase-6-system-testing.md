# Phase 6: System Testing & End-to-End Flow Validation

**Goal:** Assert that all APIs, microservices, stateful databases, and asynchronous event streams are functioning logically and correctly within the live Kubernetes environment.

## Step 6.1: End-to-End Business Flow Tests

Instead of testing pods in isolation, we test the actual user journeys to prove the distributed architecture is sound.

**Test 1: The CDC & Auth Pipeline (Registration -> Kafka)**
*   **Action:** Send a `POST` request to the Auth Service API to register a new user.
*   **Assertion 1 (DB):** Verify the user is successfully inserted into the `cinemesh_auth_db` Postgres database.
*   **Assertion 2 (Debezium):** Verify the connector is `RUNNING` and watch the Kafka topic:
    ```bash
    # Check connector status
    kubectl exec -it <kafka-connect-pod> -n kafka -- curl localhost:8083/connectors/auth-service-postgres-connector/status
    
    # Watch the topic (Strimzi Consumer)
    kubectl exec -it <kafka-pod> -n kafka -- bin/kafka-console-consumer.sh \
      --bootstrap-server localhost:9092 \
      --topic cinemesh.auth-service.user-logs \
      --from-beginning
    ```
*   **Success Criteria:** The exact JSON payload of the Postgres `INSERT` event appears in the Kafka topic instantly. Note: The payload should match the `KafkaMessagePayloadDto` structure (direct payload without extra wrapping).

**Test 2: CoreDNS Service Discovery & Feign (Internal Communication)**
*   **Action:** Send a request to a service that relies on Feign (e.g., `theater-service` calling `movie-service`).
*   **Assertion:** The request should succeed without Eureka.
*   **Success Criteria:** CoreDNS must resolve `http://movie-service:80` internally. If it fails with `UnknownHostException`, verify the Kubernetes Service name matches the Feign client URL exactly.

**Test 3: Distributed Caching (Booking Service)**
*   **Action:** Send a `GET` request to the Booking Service to fetch available seats.
*   **Assertion 1 (API):** The API should return a `200 OK`.
*   **Assertion 2 (Connectivity):** Check logs for Redis host resolution: `kubectl logs deploy/booking-service`.
*   **Success Criteria:** The service must hit `redis-master:6379`. If you see `Connection refused: localhost`, ensure the `SPRING_DATA_REDIS_HOST` environment variable is correctly injected in the deployment YAML.

**Test 4: External Webhooks & Public IP (VNPay IPN)**
*   **Action:** Simulate a payment callback from VNPay using the public IP of the `payment-service`.
*   **Assertion:** Get the external IP: `kubectl get svc payment-service`. Send the IPN payload to `http://<EXTERNAL-IP>/api/v1/payments/vnpay-ipn`.
*   **Success Criteria:** DigitalOcean must provision a LoadBalancer IP. The booking status in `cinemesh_booking_db` must update from `PENDING` to `PAID` via the asynchronous `cinemesh.payment-service.payment-logs` topic.

---

## Step 6.2: Senior Architect's Post-Deployment Checklist

Before officially declaring the environment "Production Ready," we must enforce these strict architectural guardrails.

**1. Database Schema Migrations**
*   **Never** use `ddl-auto: update` in a true production environment.
*   **The Standard:** Use **Flyway** or **Liquibase**. Run migrations as a Kubernetes `Job` or an `InitContainer` to ensure the database schema is locked and versioned *before* the application pod starts.

**2. Secret Management**
*   **The Vault:** Use `kubectl create secret generic cinemesh-secrets` for all sensitive keys.
*   **Reference:** Always use `valueFrom.secretKeyRef` or `envFrom.secretRef`. Never hardcode passwords in `config-files/*.yml` stored in Git.

**3. Log Aggregation & Observability**
*   **Stack:** Use the **Loki-Grafana** stack for Kubernetes logs. It is significantly lighter than ELK.
*   **Tracing:** Implement **Spring Cloud Sleuth/Micrometer Tracing** with **Zipkin**. This allows you to track a single "Trace ID" across 5 different microservices to find exactly where a request timed out.

**4. The "Zero-Downtime" Test**
*   **Strategy:** Ensure `strategy: type: RollingUpdate` is defined.
*   **Test:** Trigger a rollout (`kubectl rollout restart deployment/auth-service`) and run a load test (e.g., using `k6` or `JMeter`). There should be **zero** failed requests during the transition.

---

# Appendix A: The Distributed Systems Cheat Sheet

## 1. The Kubernetes Detective (Core K8s Debugging)
When a pod isn't working, follow the "Holy Trinity" plus one:

**Step 1: The Status Check**
`kubectl get pods -n <namespace>`

**Step 2: The Events (The "Why")**
If a pod is stuck in `Pending` or `ImagePullBackOff`, check the cluster events:
`kubectl get events --sort-by='.metadata.creationTimestamp' -n <namespace>`

**Step 3: The Autopsy**
`kubectl describe pod <pod-name> -n <namespace>`

**Step 4: The Black Box (Post-Mortem Logs)**
If a pod crashed (`CrashLoopBackOff`), read the logs of the *failed* instance:
`kubectl logs deploy/<name> --previous -n <namespace>`

## 2. Kafka & Debezium "Ghost" Bugs
*   **The Slot Collision:** Postgres replication slots MUST be unique. If you see `Replication slot "..." is active`, you likely have two connectors trying to use the same `slot.name`. Always use unique names like `auth_service_slot`, `booking_service_slot`.
*   **The Hyphen Trap:** Do not use hyphens (`-`) in Debezium `slot.name`. Postgres/Debezium often fails silently or throws obscure errors with hyphens in slots. Use underscores (`_`).
*   **Topic Prefix:** In Debezium 2.x+, use `topic.prefix` instead of the deprecated `database.server.name`.

## 3. Senior Developer Power Tools

### Trick 1: The "K9s" Superpower
Stop typing long commands. Install **K9s** (`brew install k9s`). It provides a terminal UI to view logs, shell into pods, and manage resources with single-key shortcuts (`l` for logs, `s` for shell, `y` for YAML).

### Trick 2: Network Debugging (The "Netshoot" Pod)
Spring Boot images are often "distroless" (no `curl`, `telnet`, or `ping`). If you can't reach a service, spin up a temporary "Swiss Army Knife" pod:
```bash
kubectl run tmp-shell --rm -i --tty --image nicolaka/netshoot -- /bin/bash
# Inside, you can run:
nslookup movie-service
curl http://auth-service:80/api/health
```

### Trick 3: Port-Forwarding (The Local Tunnel)
Connect local tools (DBeaver, Postman) to private cluster services:
`kubectl port-forward svc/postgres-postgresql 5432:5432`
`kubectl port-forward svc/config-server 8888:8888`
