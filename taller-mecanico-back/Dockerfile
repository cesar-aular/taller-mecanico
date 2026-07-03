# Etapa de build con Maven ya instalado: evita depender de ./mvnw (que se rompe
# si el repo se clona en Windows con CRLF -> shebang inválido -> exit 127).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Primero solo el pom para cachear las dependencias entre builds
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
