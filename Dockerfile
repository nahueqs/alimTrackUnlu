# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia solo los archivos de dependencias primero (mejor cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia el código fuente
COPY src ./src

# Compila la aplicación (saltando tests para build más rápido)
# Si quieres ejecutar tests: RUN mvn clean package -B
RUN mvn clean package -DskipTests -B

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crea un usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecuta la aplicación
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]