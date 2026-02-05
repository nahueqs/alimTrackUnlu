package com.unlu.alimtrack.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.ProduccionQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "test@test.com", roles = {"OPERADOR"})
public class ProduccionControllerStressTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProduccionQueryService produccionQueryService;

    @MockitoBean
    private ProduccionManagementService produccionManagementService;

    @Test
    void testConcurrentProductionCreation() throws Exception {
        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Capturamos el contexto de seguridad actual (configurado por @WithMockUser)
        SecurityContext securityContext = SecurityContextHolder.getContext();

        ProduccionMetadataResponseDTO created = new ProduccionMetadataResponseDTO(
                "PROD-CONCURRENT", "REC-V1", "Encargado", "creator@example.com", "LOTE-NEW", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        when(produccionManagementService.iniciarProduccion(any(ProduccionCreateDTO.class))).thenReturn(created);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // Establecemos el contexto de seguridad en este hilo
                    SecurityContextHolder.setContext(securityContext);

                    ProduccionCreateDTO createDTO = new ProduccionCreateDTO(
                            "REC-V1", "PROD-CONCURRENT-" + System.nanoTime(), "creator@example.com", "LOTE-NEW", "Encargado", "Obs"
                    );

                    mockMvc.perform(post("/api/v1/producciones")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createDTO)))
                            .andExpect(status().isCreated());

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    // Limpiamos el contexto al terminar
                    SecurityContextHolder.clearContext();
                }
            });
        }

        executorService.shutdown();
        boolean finished = executorService.awaitTermination(30, TimeUnit.SECONDS);
        
        if (!finished) executorService.shutdownNow();

        if (failureCount.get() > 0) throw new AssertionError("Hubo fallos en las peticiones concurrentes: " + failureCount.get());
        if (successCount.get() != numberOfThreads) throw new AssertionError("No se completaron todas las peticiones. Éxitos: " + successCount.get() + " de " + numberOfThreads);
    }

    @Test
    void testConcurrentGetMetadata() throws Exception {
        int numberOfThreads = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        ProduccionMetadataResponseDTO produccion = new ProduccionMetadataResponseDTO(
                "PROD-1", "VER-1", "Encargado", "email", "Lote", "EN_PROCESO",
                LocalDateTime.now(), null, null, "Obs"
        );
        when(produccionQueryService.findByCodigoProduccion("PROD-1")).thenReturn(produccion);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    SecurityContextHolder.setContext(securityContext);
                    mockMvc.perform(get("/api/v1/producciones/PROD-1"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.codigoProduccion").value("PROD-1"));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });
        }

        executorService.shutdown();
        boolean finished = executorService.awaitTermination(30, TimeUnit.SECONDS);
        if (!finished) executorService.shutdownNow();

        if (failureCount.get() > 0) throw new AssertionError("Fallos en lectura concurrente de metadata: " + failureCount.get());
        if (successCount.get() != numberOfThreads) throw new AssertionError("No se completaron todas las lecturas.");
    }

    @Test
    void testConcurrentGetRespuestas() throws Exception {
        int numberOfThreads = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        UltimasRespuestasProduccionResponseDTO response = new UltimasRespuestasProduccionResponseDTO(
                null, Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now()
        );
        when(produccionManagementService.getUltimasRespuestas("PROD-1")).thenReturn(response);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    SecurityContextHolder.setContext(securityContext);
                    mockMvc.perform(get("/api/v1/producciones/PROD-1/ultimas-respuestas"))
                            .andExpect(status().isOk());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });
        }

        executorService.shutdown();
        boolean finished = executorService.awaitTermination(30, TimeUnit.SECONDS);
        if (!finished) executorService.shutdownNow();

        if (failureCount.get() > 0) throw new AssertionError("Fallos en lectura concurrente de respuestas: " + failureCount.get());
    }

    @Test
    void testConcurrentGetEstado() throws Exception {
        int numberOfThreads = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        SecurityContext securityContext = SecurityContextHolder.getContext();

        EstadoProduccionPublicoResponseDTO response = new EstadoProduccionPublicoResponseDTO(
                "PROD-1", TipoEstadoProduccion.EN_PROCESO, LocalDateTime.now()
        );
        when(produccionQueryService.getEstadoProduccion("PROD-1")).thenReturn(response);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    SecurityContextHolder.setContext(securityContext);
                    mockMvc.perform(get("/api/v1/producciones/PROD-1/estado-actual"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });
        }

        executorService.shutdown();
        boolean finished = executorService.awaitTermination(30, TimeUnit.SECONDS);
        if (!finished) executorService.shutdownNow();

        if (failureCount.get() > 0) throw new AssertionError("Fallos en lectura concurrente de estado: " + failureCount.get());
    }
}
