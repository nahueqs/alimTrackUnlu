// Archivo: src/main/java/com/unlu/alimtrack/config/SpringEnvConfig.java
package com.unlu.alimtrack.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EnvConfig {

    private final ConfigurableEnvironment environment;

    @PostConstruct
    public void loadEnvToSpring() {
        try {
            // Cargar desde el archivo .env en la raíz del proyecto
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")  // Carpeta raíz del proyecto
                    .ignoreIfMissing()
                    .load();

            if (!dotenv.entries().iterator().hasNext()) {
                log.info("📄 Archivo .env vacío o no encontrado");
                return;
            }

            // Agregar variables al contexto de Spring
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry ->
                    envMap.put(entry.getKey(), entry.getValue())
            );

            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenv", envMap));

            log.info("✅ {} variables de .env disponibles en Spring", envMap.size());

        } catch (Exception e) {
            log.debug("No se cargó archivo .env: {}", e.getMessage());
        }
    }
}