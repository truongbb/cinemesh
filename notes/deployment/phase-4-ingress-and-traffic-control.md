# Phase 4: Ingress & Traffic Control (Kong)

**Goal:** Securely expose the private cluster to the internet and establish the foundation for Distributed Tracing (Request IDs).

---

## Step 4.1: Deploy Kong Ingress Controller
We use **Kong** as our Gateway because it is lightweight, high-performance, and supports the "Correlation ID" plugin out of the box.

1.  **Add Repo:**
    ```bash
    helm repo add kong https://charts.konghq.com
    helm repo update
    ```

2.  **Install Gateway:**
    *Setting `proxy.type=LoadBalancer` triggers DigitalOcean to automatically provision a Cloud Node Balancer ($12/mo).*
    ```bash
    helm install kong kong/kong --namespace default --set proxy.type=LoadBalancer
    ```

3.  **Retrieve Public Entrypoint:**
    ```bash
    kubectl get svc kong-kong-proxy
    ```
    *Wait for `EXTERNAL-IP` to change from `<pending>` to a real IP address. This is the URL you will point Postman to.*

---

## Step 4.2: Enable Distributed Tracing (Correlation ID)
In a microservices architecture, debugging a "Failed Payment" without a Trace ID is impossible. We apply a global plugin to Kong to inject `X-Request-ID` into every incoming request.

**Apply this manifest (`kong-plugins.yaml`):**
```yaml
apiVersion: configuration.konghq.com/v1
kind: KongPlugin
metadata:
  name: global-request-id
  namespace: default
config:
  header_name: X-Request-ID
  generator: uuid
  echo_back: true
plugin: correlation-id
---
apiVersion: configuration.konghq.com/v1
kind: KongClusterPlugin
metadata:
  name: global-request-id
  annotations:
    kubernetes.io/ingress.class: kong
config:
  header_name: X-Request-ID
  generator: uuid
  echo_back: true
plugin: correlation-id
```

---

## Step 4.3: Senior Architect's Perspective: Why Kong?

| Feature | Nginx Ingress | Kong Gateway |
| :--- | :--- | :--- |
| **Tracing** | Requires manual header logic. | **Native Plugin** (Correlation-ID). |
| **Performance** | Standard. | **Optimized Lua/OpenResty** engine. |
| **Plugins** | Limited. | Extensive (Rate Limiting, JWT Auth, CORS). |
| **FinOps** | No built-in cost management. | Can be integrated with monitoring for precise usage. |

**Expert Tip:** Always set `echo_back: true` in your Correlation ID config. This ensures the frontend receives the Request ID in the response headers, allowing users to provide that ID to support if they encounter an error.
