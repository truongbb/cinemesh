# Architectural Guide: Distributed Seat Locking & Timeout Strategies

This guide systematizes the strategies for managing temporary resource reservations (like cinema seats) in high-concurrency distributed systems, focusing on performance and data integrity.

---

## 1. The Strategy for Temporary Locking: Redis vs. Database

When a user selects a seat, you must prevent others from picking it. This is an "in-flight" state that precedes the actual financial transaction.

| Feature | Database (PostgreSQL) | Distributed Cache (Redis) |
| :--- | :--- | :--- |
| **Performance** | Slow. High disk I/O for frequent updates. | Ultra-fast. In-memory operations. |
| **Lifecycle** | Requires manual cleanup (cron jobs). | **Native TTL.** Expire keys automatically. |
| **Scaling** | Locking rows across pods can be heavy. | **SETNX** (Set if Not Exists) is natively atomic. |
| **Reliability** | 100% Persistent. | Volatile (acceptable for temporary states). |

### The Standard Workflow
1.  **Selection (Redis):** Use `SET lock:showtime:ID:seat:A1 userId NX EX 600`.
2.  **Checkout (PostgreSQL):** Only when the user clicks "Buy" do you create `RESERVED` tickets in the DB.
3.  **Payment Success:** Update DB to `BOOKED`, delete the Redis lock.

---

## 2. Handling Abandoned Checkouts (The Timeout Problem)

Users often close the browser after reserving a seat. The system must autonomously release these seats.

### Approach A: The Background Cron Job (Polling)
Run a task every minute to find `RESERVED` tickets where `created_at < now - 10m`.
*   **Multi-Pod Safety:** If 3 pods run the same job, they will collide.
*   **The "SKIP LOCKED" Solution:** Use a native query to turn your table into a queue:
    ```sql
    SELECT * FROM orders WHERE status = 'RESERVED' AND created_at < :timeout 
    LIMIT 50 FOR UPDATE SKIP LOCKED;
    ```
    This allows pods to process different batches in parallel without blocking each other.

### Approach B: Delayed Message Queues (Push)
The most scalable method.
1.  **On Reservation:** Send a message to a queue (e.g., RabbitMQ DLX or AWS SQS) with a 10-minute visibility delay.
2.  **On Expiry:** 10 minutes later, the consumer wakes up, checks if the order is still `RESERVED`, and cancels it if so.

---

## 3. Deep Dive
### 1. The "Lock Exhaustion" Attack
In banking, we always assume malicious actors. If a competitor wants to ruin your cinema launch, they can script bots to "select" every seat in the theater, locking them in Redis for 10 minutes without ever paying. 
*   **Architect's Tip:** Implement **Rate Limiting** per user/IP on the seat-locking API. Never allow one user to hold more than X seats simultaneously across all showtimes.

### 2. Consistency: The "Ghost Seat" Problem
If Redis goes down, your temporary locks are gone. If the database cleanup job fails, a seat stays "RESERVED" forever.
*   **Architect's Tip:** Design for **Idempotency**. Your "Release Seat" logic must be safe to run multiple times. Use **Optimistic Locking** (`@Version`) in the database to ensure that if a payment callback and a timeout job hit the same record, only one wins.

### 3. Monitoring "Lock Friction"
You need to know your "Abandonment Rate." If 70% of your locks expire, your UI or pricing might be the problem.
*   **Architect's Tip:** Emit custom metrics (e.g., via Micrometer/Prometheus) every time a lock is created and every time one expires. Visualizing this "Heartbeat of Reservations" is critical for business health.

---

## 4. Summary Recommendation for Cinemesh

1.  **Phase 1 (Current):** Use **Redis (SETNX)** for the initial click and a **Postgres Polling Job with `SKIP LOCKED`** for the 10-minute cleanup. This is the best balance of simplicity and multi-pod safety.
2.  **Phase 2 (Scale):** Move to **Delayed Kafka/RabbitMQ events** to eliminate database polling overhead as traffic grows.
3.  **Data Integrity:** Always store the `seat_code` (e.g., "H12") in the `tickets` table. Even if the `theater-service` is offline, the user can still enter the cinema with their `booking-service` data.
