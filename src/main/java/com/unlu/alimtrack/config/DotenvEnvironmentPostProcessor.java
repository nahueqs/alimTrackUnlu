// Archivo: src/main/java/com/unlu/alimtrack/config/DotenvEnvironmentPostProcessor.java
package com.unlu.alimtrack.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Se ejecuta AL INICIO, antes de que Spring intente inyectar propiedades
 */

@Component
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(DotenvEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        // Cargar .env ANTES de que Spring procese @Value
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMissing()
                    .load();

            if (!dotenv.entries().iterator().hasNext()) {
                log.debug("⚠️ Archivo .env no encontrado o vacío");
                return;
            }

            // Convertir a Map para Spring
            Map<String, Object> envMap = new HashMap<>();
            int count = 0;

            for (var entry : dotenv.entries()) {
                envMap.put(entry.getKey(), entry.getValue());
                count++;

                // Debug: mostrar algunas variables
                if (entry.getKey().equals("JWT_SECRET")) {
                    log.debug("✅ JWT_SECRET encontrada en .env");
                }
                if (entry.getKey().equals("SPRING_PROFILES_ACTIVE")) {
                    log.debug("📊 Perfil activo desde .env: {}", entry.getValue());
                }
            }

            // Agregar como PRIMERA fuente (alta prioridad)
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenv", envMap));

            log.debug("🚀 {} variables cargadas desde .env", count);

        } catch (Exception e) {
            log.debug("❌ Error cargando .env: {}", e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        // Orden MUY alto para ejecutar primero
        return Ordered.HIGHEST_PRECEDENCE;
    }
}