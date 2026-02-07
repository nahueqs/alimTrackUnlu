# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

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
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar tzdata para soporte de zonas horarias en Alpine
RUN apk add --no-cache tzdata

# Crea un usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

USER spring:spring

# Copia el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Ejecuta la aplicación
ENTRYPOINT ["java", \
    "-Xmx256m", \
    "-Xss512k", \
    "-XX:+UseSerialGC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Duser.timezone=America/Argentina/Buenos_Aires", \
    "-Dspring.profiles.active=prod", \
    "-jar", \
    "app.jar"]