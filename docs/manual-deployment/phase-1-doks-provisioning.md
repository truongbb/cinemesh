# Phase 1: DigitalOcean Kubernetes (DOKS) Provisioning

**Goal:** Provision the raw server hardware (3 worker nodes) and the Kubernetes Control Plane, then securely connect your local machine to the live cluster.

## Step 1.1: Install Infrastructure Tools
Before communicating with the cloud, you need the command-line controllers for both DigitalOcean and Kubernetes.

**Action (macOS):**
```bash
# Install DigitalOcean CLI
brew install doctl

# Install Kubernetes CLI
brew install kubectl
```

**Why we do this:** `doctl` is required to authenticate with your DO account and request the physical hardware. `kubectl` is the universal Kubernetes controller used to deploy your Docker containers into the cluster once it is built.

## Step 1.2: Authenticate the DigitalOcean CLI
Link your local terminal to your DigitalOcean account using a secure API token.

**Action:**
1. Generate an API token with **Read** and **Write** permissions in the DO Dashboard.
2. Run the authentication command:
```bash
doctl auth init
```
3. Paste the API token when prompted.
4. Verify the connection:
```bash
doctl account get
```

**Why we do this:** Without authentication, DigitalOcean will reject any command line requests to spin up or modify infrastructure.

## Step 1.3: Provision the Kubernetes Cluster
Create the highly-available, 3-node compute foundation.

**Action:**
```bash
doctl kubernetes cluster create cinemesh-cluster --region sgp1 --size s-2vcpu-4gb --count 3
```

**Command Breakdown & Why:**
* `--region sgp1`: Provisions the physical servers in the Singapore data center, ensuring the lowest possible network latency for traffic coming from Hanoi.
* `--size s-2vcpu-4gb`: Selects the $24/month Droplet size. This provides 2 vCPUs and 4GB of RAM per node.
* `--count 3`: Ensures 3 separate worker nodes are created. This is the absolute minimum required to maintain High Availability (HA) and satisfy the voting quorum required by Apache Kafka to prevent data loss if a node crashes.

*(Note: This step takes 5 to 8 minutes to complete as the virtual machines boot up and network together).*

## Step 1.4: Download Cluster Security Credentials
Connect your local `kubectl` tool to the newly created cluster.

**Action:**
```bash
doctl kubernetes cluster kubeconfig save cinemesh-cluster
```

**Why we do this:** Kubernetes is strictly secured by default. This command downloads the specific `kubeconfig` (the digital keycard) from DigitalOcean and saves it locally so your Mac is authorized to send deployment commands to the cluster.

## Step 1.5: Verify Node Health
Confirm the cluster is fully operational and ready to receive the stateful data layer (Postgres, Redis, Kafka).

**Action:**
```bash
kubectl get nodes
```

**Expected Output:** You should see exactly 3 nodes listed, and all 3 must show a `STATUS` of `Ready`.