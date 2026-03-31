# Phase 3: Continuous Deployment (The Push Method)

**Goal:** To automate the process of logging into the live Kubernetes cluster and smoothly updating the microservices to the new Docker images with zero downtime.

**Technologies Used:** Kubernetes CLI (`kubectl`), DigitalOcean Kubernetes (DOKS).

## Step-by-Step Breakdown
1. **Cluster Authentication (`doctl kubernetes cluster config save`):** Downloads the highly sensitive `kubeconfig` security file so GitHub Actions can issue commands to the live production cluster.
2. **The Rollout (`kubectl set image`):** This is a native K8s command that dynamically patches the live deployment in memory, swapping out the old image tag for the new `${{ github.sha }}` tag we just built.
3. **Verification (`kubectl rollout status`):** The pipeline pauses and watches the cluster. It ensures the new pods start up successfully and report as "Healthy" before declaring the deployment a success.

## The Code Implementation
```yaml
  # ==========================================
  # JOB 3: THE DEPLOYMENT
  # ==========================================
  deploy-to-cluster:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: artifact-factory 
    if: github.ref == 'refs/heads/main'

    strategy:
      matrix:
        service: [auth-service, booking-service, notification-service, payment-service]

    steps:
      - name: Install DigitalOcean CLI
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}

      - name: Save DigitalOcean Kubeconfig
        run: doctl kubernetes cluster config save cinemesh-cluster

      - name: Deploy ${{ matrix.service }} to Kubernetes
        run: |
          kubectl set image deployment/${{ matrix.service }} \
            ${{ matrix.service }}=[registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:$](https://registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:$){{ matrix.service }}-${{ github.sha }} \
            -n default
            
          kubectl rollout status deployment/${{ matrix.service }} -n default
```

## 🔍 Key Concepts to Focus On
* **The Blast Radius (Job Separation):** We keep Deployment as a separate job from the Build process. If the Kubernetes API goes down temporarily, the deploy job will fail, but we don't have to waste time recompiling the Java code. We just click "Retry Job 3."
* **Zero-Downtime Updates:** Kubernetes handles the tricky part. When it receives the new image, it spins up the new pod, waits for it to be ready, routes user traffic to it, and *then* kills the old pod. The user never experiences an outage.

## 🐛 Frequent Bugs & Troubleshooting
**Bug 1: `error: unable to find container named "payment-service"`**
* **Cause:** The `kubectl set image` command expects the `name:` of the container inside your K8s YAML file to perfectly match the name you provide in the command. If your container is named `app` or `backend`, it will fail.
* **Fix:** Enforce a strict naming convention. Open your `deployment.yaml` files and ensure the container name exactly matches the service name (e.g., `- name: payment-service`).

**Bug 2: `Error: no cluster goes by the name...`**
* **Cause:** You accidentally provided the name of your Container Registry instead of your Kubernetes Cluster.
* **Fix:** Log into the DigitalOcean dashboard, navigate to Kubernetes, and copy the exact cluster name (e.g., `cinemesh-cluster`).
