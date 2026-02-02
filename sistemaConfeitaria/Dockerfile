# Estágio 1: Build com Maven (mesmo processo que local)
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copia o POM e baixa dependências (cache de camada)
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

# Copia o código e compila (só código principal; testes são ignorados pelo package)
COPY src ./src
RUN mvn package -DskipTests -B -q && \
    mvn dependency:copy-dependencies -DoutputDirectory=target/dependency -B -q

# Estágio 2: Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia classes e dependências (PostgreSQL driver, etc.)
COPY --from=builder /app/target/classes /app/classes
COPY --from=builder /app/target/dependency /app/dependency

# Executa a aplicação
ENTRYPOINT ["java", "-cp", "/app/classes:/app/dependency/*", "app.Main"]
