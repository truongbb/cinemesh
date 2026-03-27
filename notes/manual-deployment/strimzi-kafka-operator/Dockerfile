# 1. Use the official Strimzi base image (Matches Kafka 3.6.0)
FROM quay.io/strimzi/kafka:0.38.0-kafka-3.6.0

# 2. Switch to root to install the plugin
USER root:root

# 3. Create the plugin directory
RUN mkdir -p /opt/kafka/plugins/debezium-postgres

# 4. Download and extract the Debezium Postgres plugin directly from Maven
RUN curl -L https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.4.0.Final/debezium-connector-postgres-2.4.0.Final-plugin.tar.gz \
    | tar xz -C /opt/kafka/plugins/debezium-postgres --strip-components=1

# 🌟 THE BRUTE FORCE: Create the config directory Strimzi looks for
# And ensure the entire /opt/kafka tree is writable by the Strimzi user
RUN mkdir -p /opt/kafka/custom-config && \
  chown -R 1001:1001 /opt/kafka && \
  chmod -R g+w /opt/kafka

# 5. Switch back to the secure Strimzi user
USER 1001
