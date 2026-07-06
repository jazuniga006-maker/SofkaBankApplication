# ====== BUILD ======
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean package

# ====== RUN ======
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiamos solo el jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]