# Phase 2: Artifact Management (The Factory)

**Goal:** To package the approved, tested code into a standardized, immutable Docker container and securely upload it to the DigitalOcean Container Registry (DOCR).

**Technologies Used:** Docker, DigitalOcean CLI (`doctl`), GitHub Secrets, GitHub Matrix Strategy.

## Step-by-Step Breakdown
1. **The Dependency (`needs: gatekeeper`):** This is critical. It forces this job to wait. If the tests in Phase 1 fail, this job is automatically canceled. 
2. **Authentication (`doctl registry login`):** The pipeline uses a hidden GitHub Secret to securely log into your DigitalOcean account without exposing passwords in the code.
3. **The Matrix Strategy (`strategy: matrix`):** Instead of writing the build commands 5 times for 5 microservices, we define an array of our service names. GitHub automatically spins up parallel servers to build them all simultaneously.
4. **Build & Push:** Packages the `.jar` file into a Docker image, stamps it with the Git Commit SHA, and uploads it to the cloud.

## The Code Implementation
```yaml
  # ==========================================
  # JOB 2: THE FACTORY
  # ==========================================
  artifact-factory:
    name: Build and Push Images
    runs-on: ubuntu-latest
    needs: gatekeeper 
    if: github.ref == 'refs/heads/main'

    strategy:
      matrix:
        service: [auth-service, booking-service, notification-service, payment-service]

    steps:
      - name: Checkout Code
        uses: actions/checkout@v3

      - name: Install DigitalOcean CLI
        uses: digitalocean/action-doctl@v2
        with:
          token: ${{ secrets.DIGITALOCEAN_ACCESS_TOKEN }}

      - name: Log in to DO Container Registry
        run: doctl registry login --expiry-seconds 1200

      - name: Build and Push ${{ matrix.service }}
        run: |
          docker build -f services/${{ matrix.service }}/Dockerfile -t [registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:$](https://registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:$){{ matrix.service }}-${{ github.sha }} .
          docker push [registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:$](https://registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:$){{ matrix.service }}-${{ github.sha }}
```

## 🔍 Key Concepts to Focus On
* **Traceability (No `:latest` tags):** We tag every image with `${{ github.sha }}`. If production crashes, we can trace the exact Docker container back to the exact line of code and the exact developer who pushed it.
* **Monorepo Architecture:** The `docker build` command is run from the root of the repository, but the `-f` flag directs Docker to the specific `Dockerfile` hidden deep inside the `services/` folders.

## 🐛 Frequent Bugs & Troubleshooting
**Bug 1: `invalid reference format` during Docker Build**
* **Cause:** You attempted to tag an image with two colons (e.g., `cinemesh:auth-service:a1b2`). Docker only allows one colon to separate the image name and the tag.
* **Fix:** Use a hyphen for the second separator: `cinemesh:${{ matrix.service }}-${{ github.sha }}`.

**Bug 2: `unauthorized: authentication required` during Docker Push**
* **Cause:** The GitHub pipeline does not have permission to write to DigitalOcean.
* **Fix:** Verify that your `DIGITALOCEAN_ACCESS_TOKEN` is correctly saved in GitHub Secrets and that the token has "Write" permissions enabled in the DigitalOcean dashboard.
