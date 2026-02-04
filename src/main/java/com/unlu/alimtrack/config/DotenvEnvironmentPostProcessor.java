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

@Component
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(DotenvEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        log.info("Iniciando carga de variables de entorno desde .env");

        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();

            Map<String, Object> envMap = new HashMap<>();
            
            for (var entry : dotenv.entries()) {
                envMap.put(entry.getKey(), entry.getValue());
            }

            if (!envMap.isEmpty()) {
                environment.getPropertySources()
                        .addFirst(new MapPropertySource("dotenv", envMap));
                log.info("Se cargaron {} variables desde el archivo .env", envMap.size());
            } else {
                log.info("No se encontraron variables en el archivo .env o el archivo está vacío");
            }

        } catch (Exception e) {
            log.error("Error al cargar el archivo .env: {}", e.getMessage());
            // No lanzamos excepción para permitir que la aplicación continúe si usa variables de sistema
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
