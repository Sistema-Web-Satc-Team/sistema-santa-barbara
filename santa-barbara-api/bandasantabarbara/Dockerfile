
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app


RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*

COPY pom.xml .
COPY src ./src


RUN mkdir -p src/main/resources/certs && \
    openssl genrsa -out src/main/resources/certs/private.pem 2048 && \
    openssl rsa -in src/main/resources/certs/private.pem -pubout -out src/main/resources/certs/public.pem


RUN mvn clean package -DskipTests


FROM eclipse-temurin:25-jre-alpine
WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


ENV JAVA_TOOL_OPTIONS="-Xmx350m -Xms200m"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
