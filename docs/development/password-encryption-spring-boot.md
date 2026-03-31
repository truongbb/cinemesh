# Password Encryption in Spring Boot Microservices

This guide systematizes the knowledge on securing sensitive configuration properties (like database passwords) in a microservices environment, covering three primary architectural patterns.

---

## 1. Why Encrypt Configurations?
Storing plaintext passwords in Git repositories (even private ones) or configuration files is a critical security risk. Modern architectures solve this by ensuring that secrets are only decrypted in the application's RAM at runtime.

---

## 2. Comparison of Security Patterns

| Approach | Best For | Pros | Cons |
| :--- | :--- | :--- | :--- |
| **Jasypt** | Standalone services / Simple setups | Easy to setup; File-level security. | Requires library dependency; "Secret Zero" problem (master key storage). |
| **Env Variables** | Docker / Kubernetes | Native to OS; No extra code; DevOps friendly. | Manual management for local dev; No encryption-at-rest in Git. |
| **Config Server ({cipher})** | Spring Cloud Ecosystem | Centralized; No extra libs in services; Built-in REST API. | Requires setup of JKS/JCE or symmetric keys on Config Server. |
| **Secret Managers** | Production / Enterprise | Auto-rotation; High security; Audit trails. | High infrastructure overhead/cost (Vault/AWS KMS). |

---

## 3. Implementation: Jasypt (Java Simplified Encryption)
Jasypt is the industry standard for encrypting properties inside YAML/properties files.

### Step 1: Add Dependency
Add to your service's `pom.xml`:
```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

### Step 2: Generate Encrypted String
Use the Maven plugin to encrypt your password using a Master Key (e.g., `cinemesh_secret`):
```bash
mvn jasypt:encrypt-value -Djasypt.encryptor.password="cinemesh_secret" -Djasypt.plugin.value="your_db_password"
```

### Step 3: Configure YAML
Wrap the output in `ENC(...)`:
```yaml
spring:
  datasource:
    password: ENC(encrypted_string_here)
```

### Step 4: Pass Master Key at Runtime
Never store the master key in the repo. Pass it as an environment variable:
*   **IntelliJ:** `JASYPT_ENCRYPTOR_PASSWORD=cinemesh_secret`
*   **Docker/CLI:** `--jasypt.encryptor.password=cinemesh_secret`

---

## 4. Implementation: Spring Cloud Config Native ({cipher})
If you already use a Config Server, this is the cleanest "built-in" method.

### Step 1: Set Symmetric Key on Config Server
Start your Config Server with an `ENCRYPT_KEY`:
```bash
export ENCRYPT_KEY=cinemesh_super_secret
java -jar config-server.jar

# or 

java -jar config-server.jar ENCRYPT_KEY=cinemesh_super_secret
```

### Step 2: Use the Encryption API
The Config Server exposes `/encrypt` and `/decrypt` endpoints. Use Postman/cURL to encrypt:
*   **POST** `http://localhost:8888/encrypt`
*   **Body (Text):** `your_db_password`
*   **Response:** `a1b2c3d4...`

### Step 3: Update Config Repository
Paste the result into your YAML file with the `{cipher}` prefix:
```yaml
spring:
  datasource:
    password: '{cipher}a1b2c3d4...'
```

### The Workflow Result
When the target microservice (e.g., `payment-service`) requests its config, the Config Server decrypts the string using its `ENCRYPT_KEY` and sends the **plaintext** password securely over the network directly to the service's RAM.

---

## 5. Implementation: Environment Variables (12-Factor App)
The simplest way to remove secrets from Git without using encryption libraries.

### Step 1: Use Placeholders in YAML
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
```

### Step 2: Inject at Runtime
Inject the real password into the container/OS environment as `DB_PASSWORD`. This is the standard practice for Kubernetes (Secrets) and Docker Compose.

---

## 6. Recommendations
*   **Development:** Use **Environment Variables** for simplicity.
*   **Testing/Staging:** Use **Spring Cloud Config {cipher}** to keep Git repositories clean.
*   **Production:** Move towards **HashiCorp Vault** or **AWS Secrets Manager** for rotation and auditing.
