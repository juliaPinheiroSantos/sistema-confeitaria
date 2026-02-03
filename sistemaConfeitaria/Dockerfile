# Java Swing application (javax.swing) - Bakery System
# Build: Maven. Runtime: Eclipse Temurin 17 with full JDK (includes Swing/AWT).

# Stage 1: Build with Maven (Java 17)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn package -DskipTests -B -q && \
    mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -B -q

# Stage 2: Runtime - JDK image (includes Java Swing / javax.swing)
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/target/classes /app/classes
COPY --from=builder /app/target/dependency /app/dependency

# Java Swing: GUI uses javax.swing (JFrame, JButton, etc.). In Docker there is no display
# by default, so we run headless: app logs flow and exits. For GUI run locally: mvn exec:java
ENV JAVA_OPTS="-Djava.awt.headless=true"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp /app/classes:/app/dependency/* app.Main"]
