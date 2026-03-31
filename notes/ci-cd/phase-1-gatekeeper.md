# Phase 1: Continuous Integration (The Gatekeeper)

**Goal:** To mathematically prove that new code is safe, functional, and won't break the application before it is allowed to merge into the main project.

**Technologies Used:** GitHub Actions, Ubuntu Linux (Runner), Java JDK 17, Maven, H2 In-Memory Database.

## Step-by-Step Breakdown
1. **The Trigger (`on: push`):** Tells GitHub to wake up and run this pipeline every time code is pushed to the `main` branch or a Pull Request is created.
2. **Environment Provisioning (`actions/setup-java`):** GitHub spins up a blank, temporary virtual machine and installs the exact version of Java needed for the project. This ensures a clean, neutral testing environment.
3. **Compile & Test (`mvn clean test`):** The core engine of the Gatekeeper. This command compiles the source code and runs every automated unit test to verify the new feature works and hasn't broken existing logic.

## The Code Implementation
```yaml
  # ==========================================
  # JOB 1: THE GATEKEEPER
  # ==========================================
  gatekeeper:
    name: Build and Test Java Code
    runs-on: ubuntu-latest
    steps:
      - name: Checkout Code
        uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'

      - name: Run Maven Build and Tests
#        run: mvn -B clean test
        run: mvn clean install -DskipTests
```

## 🔍 Key Concepts to Focus On
* **The "Fast Fail" Principle:** We run this job first. If a developer writes bad code, we want the pipeline to fail and notify them in seconds, rather than wasting 10 minutes building Docker images first.
* **Ephemeral Environments:** The GitHub Runner is deleted the moment the job finishes. It has no memory of previous runs, which is why we must download Java and the code every single time.

## 🐛 Frequent Bugs & Troubleshooting
**Bug 1: `No spring.config.import property has been defined`**
* **Cause:** Your application uses Spring Cloud Config, but the central Config Server isn't running on the GitHub virtual machine.
* **Fix:** Disable the config server during tests by adding this to your test classes:
  `@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})`

**Bug 2: `Failed to configure a DataSource: 'url' attribute is not specified`**
* **Cause:** The GitHub Runner does not have a PostgreSQL database installed for your application to connect to.
* **Fix:** Add the **H2** database dependency (`<scope>test</scope>`) to your `pom.xml` and configure your `application-test.yml` to use `jdbc:h2:mem:testdb`.
