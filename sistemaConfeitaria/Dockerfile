# Estágio 1: Build e Download do Driver
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app

# Instala o curl para baixar o driver
RUN apt-get update && apt-get install -y curl

# Baixa o driver JDBC do PostgreSQL (versão estável atual)
RUN curl -o postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.7.2.jar

# Copia o código fonte
COPY src ./src

# Compila os arquivos incluindo o JAR do Postgres no classpath
RUN mkdir bin && \
    javac -cp "postgresql.jar" -d bin $(find src -name "*.java")

# Estágio 2: Runtime
FROM eclipse-temurin:23-jre
WORKDIR /app

# Copia os binários compilados e o driver do estágio anterior
COPY --from=builder /app/bin .
COPY --from=builder /app/postgresql.jar .

# Executa a aplicação incluindo o driver e a pasta atual no classpath
# Substitua 'app.Main' pelo nome real da sua classe principal
ENTRYPOINT ["java", "-cp", ".:postgresql.jar", "app.Main"]