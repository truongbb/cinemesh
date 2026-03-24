
# Phase 2: The Private Container Registry

**Goal:** Create a secure cloud vault for your Docker images and grant the Kubernetes cluster permission to download them.

## Step 2.1: Create the Registry
DigitalOcean Container Registry (DOCR) provides a private endpoint to host your compiled microservices.

**Action:**
```bash
doctl registry create cinemesh-registry-<yourname>
```

**Why we do this:** Kubernetes cannot run local `.jar` files or local Docker images. It requires a central, remote URL to pull container images from when spinning up new Pods.

## Step 2.2: Integrate Registry with Cluster (CRITICAL)
Give the Kubernetes worker nodes the security credentials required to read the private registry.

**Action (Via DO Dashboard):**
1. Navigate to **Kubernetes** -> `cinemesh-cluster` -> **Settings**.
2. Locate **DigitalOcean Container Registry Integration**.
3. Click **Edit**, select your registry, and click **Save**.

**Why we do this:** Kubernetes is secure by default. If it attempts to pull an image from a private registry without the correct digital "keycard," it will fail and throw an endless `ImagePullBackOff` error. This UI button automatically generates the secret keys and injects them into the cluster's default service account.
