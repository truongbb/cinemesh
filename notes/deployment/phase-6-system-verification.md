# Phase 6: System Verification & Observability

**Goal:** Ensure the entire distributed system is functioning as a single unit and verify that the Tracing (Request IDs) correctly flows across multiple pods.

---

## Step 6.1: Watch Cluster Health
Before testing, confirm all pods are `Running` and healthy.

**Command:**
```bash
kubectl get pods -A -w
```
*Wait for all microservice pods to transition from `ContainerCreating` to `Running`. Watch for any restarts (`RESTARTS > 0`), which may indicate OOM or configuration errors.*

---

## Step 6.2: Verify Distributed Tracing (Postman)
Fire a complete booking transaction and verify that the `X-Request-ID` is correctly logged by every pod in the chain.

1.  **Request:** Point Postman to your Kong Ingress External IP address.
    *   `POST http://<KONG_IP>/api/v1/bookings`
2.  **Verify Response:** Check the Response Headers. You should see `X-Request-ID: <uuid>`.
3.  **Trace Logs:**
    *   **Booking Pod:** `kubectl logs -l app=booking-service --tail=100`
    *   **Payment Pod:** `kubectl logs -l app=payment-service --tail=100`
    *   **Kafka Logs:** Check the `cinemesh-kafka` logs to ensure the event was published and consumed.

---

## Step 6.3: Senior Architect's Perspective: Why Tracing Matters
In a monolith, a stack trace is enough. In microservices, we have "Silent Failures" where one service works perfectly but another fails downstream.

1.  **Correlation IDs:** This single UUID links the API Gateway request to the Database query and the Kafka event.
2.  **MDC Logging:** Ensure your Spring Boot logs include `[%X{requestId}]`. This is the only way to search for a single user's transaction in a sea of millions of logs.
3.  **Business Continuity:** If a customer reports a failed payment, you ask for their Request ID (from the header). You search your logs for that ID and instantly see exactly where the chain broke (e.g., *Timeout during VNPay IPN processing*).

---

## Step 6.4: Final Verdict
By following this 6-phase deployment playbook, you have built more than just a "code project." You have created a **Production-Grade Ecosystem** that is resilient to hardware failure, traceable during errors, and scalable to handle millions of movie fans. 

**Welcome to Cloud-Native Engineering.**
