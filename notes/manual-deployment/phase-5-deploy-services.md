# Phase 5: Deployment & Orchestration Master Playbook

### 1. Kubernetes Secrets Management (The Vault)
Before any application boots, we must inject our highly sensitive passwords (Database, Redis, GitHub Tokens) into the Kubernetes cluster. We do this using a K8s `Secret`. **Never commit passwords to your Git repository.**

To create the central secret vault (`cinemesh-secrets`), run this command imperatively from your Mac's terminal. Kubernetes will encrypt and store these values internally:

```bash
kubectl create secret generic cinemesh-secrets \
  --from-literal=DB_PASSWORD='your_production_db_password' \
  --from-literal=REDIS_PASSWORD='your_redis_password' \
  --from-literal=GIT_USERNAME='truongbb' \
  --from-literal=GIT_TOKEN='ghp_your_github_personal_access_token' \
  -n default
```
*Note: If you ever need to update a password, delete the secret (`kubectl delete secret cinemesh-secrets`) and run the creation command again with the new values, then restart your pods.*

---

### 2. Dockerization Mastery (The 2-Stage Build)
To keep our Kubernetes cluster secure and lightweight, we use a **Multi-Stage Build**. Stage 1 compiles the code using a heavy Maven image; Stage 2 extracts *only* the compiled `.jar` into a tiny Java Runtime environment.

**File: `services/auth-service/Dockerfile`**
```dockerfile
# Stage 1: Build the application using the official Maven image
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy the ENTIRE cinemesh root project into the container
COPY . .

# Build the entire project to perfectly resolve all dependencies (Bulletproof method)
RUN mvn clean package -DskipTests

# Stage 2: Create the lightweight runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Run as non-root
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Extract ONLY the auth-service jar from the massive build
COPY --from=builder /app/services/auth-service/target/*.jar app.jar

EXPOSE 8080

# K8s memory limits
ENTRYPOINT ["java", \
            "-XX:InitialRAMPercentage=50.0", \
            "-XX:MaxRAMPercentage=75.0", \
            "-XX:+UseContainerSupport", \
            "-jar", "/app/app.jar"]

```

#### The DigitalOcean 5-Repository Limit (The "Monorepo" Strategy)
DigitalOcean's Basic Container Registry limits you to **5 Repositories**. We bypass this by putting *all* microservices into a single repository named `cinemesh`, using **Tags** to differentiate the services.

**1. Build the Image:**
```bash
docker build --platform=linux/amd64 -f ./services/auth-service/Dockerfile -t registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:auth-service-v1.0.0 .
```
**`--platform=linux/amd64` (The Apple Silicon Trap):** When building the image on an Apple M1/M2/M3 Mac, Docker defaults to ARM64 architecture. DigitalOcean Kubernetes servers run on Intel/AMD (AMD64). You **must** pass this flag, or the pod will instantly crash in K8s with an `exec format error`.

**2. Push to Registry:**
```bash
docker push registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:auth-service-v1.0.0
```
*(Optional) If you built an image locally under the wrong name, use `docker tag` to rename it before pushing:*
```bash
docker tag auth-service:v1.0.0 registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:auth-service-v1.0.0
```

---

### 3. Kubernetes GitOps Organization (The "Company Way")
We organize our K8s files using Kustomize, strictly separating application logic from external routing.

**Folder Structure:**
```text
config-k8s-prod/
├── ingress/
│   └── auth_ingress.yml
├── config-server/
│   ├── config-server-deployment.yml
│   ├── config-server-svc.yml
│   └── kustomization.yml
├── auth-service/
│   ├── auth-service-deployment.yml
│   ├── auth-service-svc.yml
│   └── kustomization.yml
```

#### A. The Config Server Setup (The Infrastructure Backbone)
The Config Server is fundamentally different from other microservices. It does not connect to a database; it connects to GitHub.

**`config-server-deployment.yml`**
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-server
  namespace: default
spec:
  replicas: 1
  selector:
    matchLabels:
      app: config-server
  template:
    metadata:
      labels:
        app: config-server
    spec:
      containers:
        - name: config-server
          image: registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:config-server-v1.0.0
          imagePullPolicy: Always
          ports:
            - containerPort: 8888
          resources:
            requests:
              memory: "256Mi"
              cpu: "100m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          env:
            # Tells Spring to use the Git profile
            - name: SPRING_PROFILES_ACTIVE
              value: "git"
            - name: SPRING_CLOUD_CONFIG_SERVER_GIT_URI
              value: "https://github.com/truongbb/cinemesh.git"
            - name: SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULT_LABEL
              value: "main"
            - name: SPRING_CLOUD_CONFIG_SERVER_GIT_SEARCH_PATHS
              value: "config-files"
            # 🌟 Securely Inject GitHub Credentials from our Vault
            - name: GIT_USERNAME
              valueFrom:
                secretKeyRef:
                  name: cinemesh-secrets
                  key: GIT_USERNAME
            - name: GIT_TOKEN
              valueFrom:
                secretKeyRef:
                  name: cinemesh-secrets
                  key: GIT_TOKEN
```
**`config-server-svc.yml`**
*Note: Config server convention is to strictly expose port 8888 internally, not port 80.*
```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: config-server
  namespace: default
spec:
  selector:
    app: config-server
  ports:
    - protocol: TCP
      port: 8888       
      targetPort: 8888 
  type: ClusterIP      
```

Then run command to apply this config
```bash
kubectl apply -k ./config-server
```

Now you can see the pod config-server on Lens app.

#### B. The Application Service Setup (`auth-service`)

**`auth-service-deployment.yml`**
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
  namespace: default
spec:
  replicas: 1
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
        - name: auth-service
          image: registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:auth-service-v1.0.3
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
          resources:
            requests:
              memory: "256Mi"
              cpu: "100m"
            limits:
              memory: "512Mi"
              cpu: "500m"
          env:
            # 1. The Bootstrap Identity
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: SPRING_APPLICATION_NAME
              value: "auth-service"
            - name: SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULT_LABEL
              value: "main"
            - name: SPRING_CLOUD_CONFIG_PROFILE
              value: "prod"
            - name: SPRING_CLOUD_CONFIG_LABEL
              value: "main"
            - name: SPRING_CONFIG_IMPORT
              value: "optional:configserver:http://config-server:8888/"
            # 2. Inject DB Password from K8s Secret Vault
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: cinemesh-secrets
                  key: DB_PASSWORD
```
**Why define properties in K8s instead of Git?** You cannot put `SPRING_APPLICATION_NAME` inside the remote Git file because Spring Boot needs its "ID card" *before* it can ask the Config Server for its files. Kubernetes injects these variables at the OS level the millisecond the container boots.

**`auth-service-svc.yml`**
```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: auth-service
  namespace: default
spec:
  selector:
    app: auth-service
  ports:
    - protocol: TCP
      port: 80         # Kong and Feign call this clean port
      targetPort: 8080 # K8s secretly forwards it to Spring Boot on 8080
  type: ClusterIP
```

Then run command to apply this config
```bash
kubectl apply -k ./auth-service
```

Note: Please take care to change the config file for `auth-service`. We need to change:
- DB url path to (pay attention to host and port): `jdbc:postgresql://postgres-postgresql:5432/cinemesh_auth_db`
- DB pass to `${DB_PASSWORD}`
- In `application.yml` file inside `auth-service`, comment this block:
```yaml
  config:
    import: "optional:configserver:http://localhost:8888/" # <--- calling to Config Server
```
because we're using CoreDNS not Eureka discovery server anymore (CoreDNS's mentioned bellow).

Do the same with other services (movie-service, booking-service, ...)
---

### 4. Upgrades, Rollouts, and the "Ghost Image" Bug

**The "Ghost Image" Bug:**
If you change your code, rebuild it, and push it to DigitalOcean with the exact same tag (e.g., `v1.0.3`), Kubernetes will look at your YAML, see `v1.0.3` is already running, and do absolutely nothing. You will be stuck testing an old "Ghost Image."

**The Correct Upgrade Flow:**
1.  **Build and Bump the Tag:** (`v1.0.3` -> `v1.0.4`)
    ```bash
    docker build --platform=linux/amd64 -f ./services/auth-service/Dockerfile -t registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:auth-service-v1.0.4 .
    ```
2.  **Push the New Tag:**
    ```bash
    docker push registry.digitalocean.com/cinemesh-registry-truongbb/cinemesh:auth-service-v1.0.4
    ```
3.  **Update YAML & Apply:** Change the `image:` line in `auth-service-deployment.yml` to `v1.0.4`.
    ```bash
    kubectl apply -k ./auth-service
    ```
*(K8s will automatically terminate the old pod and seamlessly spin up the new one).*

---

### 5. Dropping Eureka for Native Kubernetes CoreDNS

We intentionally do **not** use Spring Cloud Netflix Eureka. In a modern cluster, Eureka consumes unnecessary RAM and adds network overhead. Kubernetes has a built-in DNS system called **CoreDNS**.

**The Mechanism:** 1. Delete the `spring-cloud-starter-netflix-eureka-client` dependency from `pom.xml` and `@EnableDiscoveryClient` from Java.
2. When we apply `auth-service-svc.yml` (naming the service `auth-service`), CoreDNS instantly registers the internal domain `http://auth-service` mapped to the pod's IP.
3. **Feign Updates:** Instead of Feign asking Eureka for an IP, hardcode Feign to trust CoreDNS:
   `@FeignClient(name = "movie-service", url = "http://movie-service:80")`

---

### 6. Database Initialization inside Kubernetes

Fresh Postgres pods are completely empty. You must manually initialize the database structure before the applications start.

1.  **Exec into Postgres:**
    ```bash
    kubectl exec -it deployment/postgres-postgresql -n default -- psql -U postgres
    ```
2.  **Create Roles and Databases:**
    ```sql
    CREATE ROLE cinemesh WITH LOGIN PASSWORD 'your_secure_password';
    ALTER ROLE cinemesh CREATEDB;
    CREATE DATABASE cinemesh_auth_db OWNER cinemesh;
    ```
Do the same with other databases for other services.    

3.  **Insert Master Data:**
    ```sql
    \c cinemesh_auth_db
    CREATE TABLE roles (id SERIAL PRIMARY KEY, name VARCHAR(50));
    INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');
    \q
    ```

---

### 7. Network Exposure (Port-Forwarding & Kong)

**Port-Forwarding for Local Development:**
To connect your local Mac tools to the secure internal Kubernetes network without exposing them to the internet:
* **Postgres (for DBeaver/DataGrip/...):** `kubectl port-forward svc/postgres-postgresql 5432:5432 -n default`
* **Kong Admin API:** `kubectl port-forward svc/kong-kong-admin 8001:8001 -n default`

**Configuring Kong & Testing:**
Do not use the Kong UI to create routes ("ClickOps"). Always write `auth_ingress.yml` files ("GitOps") so your infrastructure is restorable.
1.  Get the Kong Load Balancer IP: `kubectl get svc -A | grep kong-proxy`
2.  Find the `EXTERNAL-IP`.
3.  Test in Postman: `http://<EXTERNAL-IP>/api/v1/auth/login`. *(No port needed, Kong handles port 80 natively).*

---

### 8. The Ultimate Deployment Debugging Guide

When a pod fails to start, use `kubectl logs -f -l app=auth-service` and target the specific error:

* **Error: `Connection refused` to `http://localhost:8888`**
  * *Cause:* The K8s `env` variables (`SPRING_CONFIG_IMPORT`) were not injected due to YAML indentation errors, causing Spring to look for the config server locally.
  * *Debug:* Run `kubectl exec -it deployment/auth-service -- env | grep SPRING`. If variables are missing, fix YAML formatting and redeploy.
* **Error: `Located environment: name=application, profiles=[default]`**
  * *Cause:* The pod reached the Config Server, but Spring Boot fired the request before it knew its identity.
  * *Debug:* Ensure `SPRING_CLOUD_CONFIG_PROFILE` and `SPRING_APPLICATION_NAME` are explicitly set in the K8s deployment `env` block.
* **Error: `unable to obtain isolated JDBC connection`**
  * *Cause:* The database URL in your Git configuration is wrong, or the database hasn't been created yet.
  * *Debug:* Ensure the URL is exactly `jdbc:postgresql://postgres-postgresql:5432/cinemesh_auth_db`. Verify the DB password K8s Secret matches the one created in Postgres.
* **Using Lens IDE:** Instead of terminal commands, use the Lens UI to quickly view pod status, check real-time RAM usage, and open container shells with one click.
