# Architectural Rationale: 3-Node Kubernetes Cluster for Cinemesh

This document systematizes the technical and business reasons for choosing a **3-node Managed Kubernetes (DOKS)** cluster for the Cinemesh platform. It transitions the mindset from "Individual Servers" to "Distributed Resource Pools."

---

## 1. The Paradigm Shift: From Servers to Resource Pools

In a traditional Docker Compose setup, developers often think in terms of "The Web Server" or "The DB Server." In Kubernetes (K8S), we treat nodes as **anonymous compute resources**.

*   **Unified Pool:** A 3-node cluster with 2 vCPUs and 4GB RAM each is not three separate buckets; it is a single **6 vCPU / 12GB RAM pool**.
*   **Dynamic Scheduling:** The K8S Scheduler (the "Master Brain") continuously evaluates which node has the most "headroom" to host a Pod (container). If a service like `payment-service` spikes, K8S can shift other non-critical workloads to different nodes to ensure performance.

---

## 2. The "Quorum" Requirement (Data Integrity & Kafka)

In high-stakes domains like banking and finance, data corruption is a greater risk than downtime. Distributed systems like **Apache Kafka** (used in Cinemesh) and **etcd** (the heart of K8S) rely on the **Majority Rule (Quorum)**.

### Why 2 Nodes is a Trap
*   **The Split-Brain Scenario:** If you have 2 nodes and the network cable between them is cut (Network Partition), both nodes might think the other is dead and try to become the "Leader." This leads to data divergence where two different versions of the truth exist.
*   **The Vote Problem:** To prevent split-brain, systems require `(n/2) + 1` votes to operate.
    *   **2 Nodes:** If 1 fails, only 1 remains (50%). **50% is not a majority.** The system freezes to protect data.
    *   **3 Nodes:** If 1 fails, 2 remain (66%). **66% is a majority.** The cluster remains fully operational.

**Architect's Verdict:** 3 is the *mathematical minimum* for a self-healing, distributed system that handles financial transactions.

---

## 3. High Availability (HA) & The "Blast Radius"

In a production environment, hardware *will* fail. Disks die, motherboards short-circuit, and hypervisors crash.

*   **Redundancy:** By distributing `booking-service` replicas across 3 nodes, you ensure that the failure of any single physical host results in only a **33% reduction in capacity**, rather than 100% (with 1 node) or 50% (with 2 nodes).
*   **Anti-Affinity:** We use K8S `podAntiAffinity` rules to force redundant copies of critical services (like Kafka brokers or the Gateway) onto *different* physical nodes. This ensures that a "single fire" in a data center rack doesn't take out both your primary and your backup.

---

## 4. Operational Excellence: Zero-Downtime Maintenance

A 3-node cluster enables "Cordon and Drain" operations, which are essential for long-term maintenance.

1.  **Node Patching:** When DigitalOcean needs to update the underlying Linux kernel of a node, K8S "Cordons" the node (stops new pods from arriving) and "Drains" it (gracefully moves existing pods to the other 2 nodes).
2.  **Rolling Updates:** During a deployment of `cinemesh-v2`, K8S boots the new version on Node C while Node A and B still handle traffic for `v1`. Only once `v2` passes health checks is `v1` decommissioned.
3.  **Breathing Room:** Multiple nodes provide the "surge capacity" needed to hold two versions of your app simultaneously during a deployment handoff.

---

## 5. Compliance & Banking Standards

From a 20-year SA/DevOps perspective in the finance sector, a single-node or two-node cluster is often a **Compliance Violation**.

*   **SPOF (Single Point of Failure):** PCI-DSS and most central bank regulations require proof of redundancy for transaction-processing systems. A 1-node cluster is a "Hard Fail" in any professional audit.
*   **Disaster Recovery (DR):** 3 nodes are the baseline for "Liveness" probes to accurately detect if a service is stuck. In a 1-node setup, if the node is "Zombie" (partially alive but unresponsive), the cluster cannot heal itself because there is nowhere else to go.

---

## 6. FinOps: Cost-Benefit Analysis

| Configuration | Cost | Reliability | Verdict |
| :--- | :--- | :--- | :--- |
| **1x Large Node** (4 vCPU / 8GB) | ~$48/mo | **Zero** | High risk. No benefit of K8S orchestration. |
| **2x Medium Nodes** (2 vCPU / 4GB) | ~$48/mo | **Low** | Better, but fails Quorum during any maintenance. |
| **3x Medium Nodes** (2 vCPU / 4GB) | ~$72/mo | **High** | **Recommended.** The "Sweet Spot" for production stability. |

### Summary
Choosing 3 nodes isn't just about "having more RAM." It is about **purchasing an insurance policy** for your data integrity, ensuring your Kafka cluster survives partitions, and guaranteeing that your movie ticket sales never stop—even when the underlying hardware fails.
