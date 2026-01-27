package com.unlu.alimtrack.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        System.out.println("\n🔥 ==== DOTENV DEBUG DETALLADO ==== 🔥");
        System.out.println("Directorio actual: " + new File(".").getAbsolutePath());

        // 1. LEER EL ARCHIVO .env DIRECTAMENTE
        try {
            File envFile = new File(".env");
            System.out.println("Archivo .env: " + envFile.getAbsolutePath());
            System.out.println("Existe: " + envFile.exists());

            if (envFile.exists()) {
                System.out.println("\n📖 CONTENIDO COMPLETO DE .env:");
                String content = new String(Files.readAllBytes(Paths.get(".env")));
                System.out.println(content);

                // Buscar específicamente ALIMTRACK_DEV_DB_URL
                if (content.contains("ALIMTRACK_DEV_DB_URL")) {
                    int start = content.indexOf("ALIMTRACK_DEV_DB_URL");
                    int end = content.indexOf("\n", start);
                    String line = content.substring(start, end != -1 ? end : content.length());
                    System.out.println("\n🔍 LÍNEA ENCONTRADA: " + line);

                    // Verificar si tiene jdbc:
                    if (!line.contains("jdbc:")) {
                        System.out.println("⚠️  ¡ADVERTENCIA! La URL NO tiene 'jdbc:'");
                        System.out.println("Corrigiendo automáticamente...");
                        line = line.replace("mysql://", "jdbc:mysql://");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error leyendo .env directamente: " + e.getMessage());
        }

        // 2. CARGAR CON DOTENV-JAVA
        try {
            System.out.println("\n🔄 Cargando con dotenv-java...");
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();

            // 3. MOSTRAR VARIABLE ESPECÍFICA
            String dbUrl = dotenv.get("ALIMTRACK_DEV_DB_URL");
            System.out.println("\n🎯 ALIMTRACK_DEV_DB_URL desde dotenv:");
            System.out.println("Valor: " + (dbUrl != null ? dbUrl : "NULL"));
            System.out.println("Longitud: " + (dbUrl != null ? dbUrl.length() : "N/A"));
            System.out.println("¿Comienza con 'jdbc:'? " + (dbUrl != null && dbUrl.startsWith("jdbc:") ? "✅ SÍ" : "❌ NO"));

            if (dbUrl != null && !dbUrl.startsWith("jdbc:")) {
                System.out.println("\n🚨 CORRECCIÓN AUTOMÁTICA ACTIVADA");
                dbUrl = "jdbc:" + dbUrl;
                System.out.println("Nuevo valor: " + dbUrl);
            }

            // 4. CARGAR TODAS LAS VARIABLES
            Map<String, Object> envMap = new HashMap<>();
            int count = 0;

            for (var entry : dotenv.entries()) {
                String key = entry.getKey();
                String value = entry.getValue();

                // Corregir automáticamente si es necesario
                if (key.equals("ALIMTRACK_DEV_DB_URL") && !value.startsWith("jdbc:")) {
                    value = "jdbc:" + value;
                    System.out.println("✅ Variable corregida: " + key + " = " + value.substring(0, Math.min(60, value.length())) + "...");
                }

                envMap.put(key, value);
                count++;
            }

            System.out.println("\n📊 Total variables cargadas: " + count);

            // 5. AGREGAR A SPRING
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("dotenv", envMap));

            System.out.println("\n🎉 Dotenv cargado y corregido exitosamente");
            System.out.println("🔥 ==== FIN DEBUG ==== 🔥\n");

        } catch (Exception e) {
            System.out.println("💥 ERROR en dotenv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}