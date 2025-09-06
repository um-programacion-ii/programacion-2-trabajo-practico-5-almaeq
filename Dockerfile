# --- Etapa 1: Compilación con Maven ---
# CORRECCIÓN: Usamos una etiqueta oficial y estable que combina Maven 3.9 con Eclipse Temurin JDK 21.
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# --- Etapa 2: Creación de la imagen final ---
# Mantenemos la imagen de JRE 21 para la ejecución.
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]