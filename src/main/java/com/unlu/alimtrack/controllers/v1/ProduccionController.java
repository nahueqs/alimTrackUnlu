package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.respuestas.EstadoActualProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.respuestas.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ProduccionResponseDTO;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.queries.ProduccionQueryServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/producciones")
public class ProduccionController {

    private final ProduccionQueryServiceImpl produccionService;
    private final ProduccionManagementService produccionManagementService;

    @GetMapping
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProducciones(ProduccionFilterRequestDTO filtros) {

        log.info("Solicitud de búsqueda de producciones recibida: {}", filtros);

        List<ProduccionResponseDTO> producciones = produccionService.findAllByFilters(filtros);

        log.debug("Retornando {} producciones para los filtros: {}", producciones.size(), filtros);

        return ResponseEntity.ok(producciones);
    }

    @GetMapping("/{codigoProduccion}")
    public ResponseEntity<ProduccionResponseDTO> getProduccionByCodigoProduccion(
            @PathVariable String codigoProduccion) {

        log.debug("Solicitando produccion con codigo: {}", codigoProduccion);
        return ResponseEntity.ok(produccionService.findByCodigoProduccion(codigoProduccion));
    }

    @PostMapping("/{codigoProduccion}")
    public ResponseEntity<ProduccionResponseDTO> saveProduccion(@PathVariable String codigoProduccion,
                                                                @Valid @RequestBody ProduccionCreateDTO createDTO) {

        log.debug("Solicitando crear producción con el código: {}, y la requestBody, {}", codigoProduccion, createDTO);

        ProduccionResponseDTO created = produccionManagementService.saveProduccion(codigoProduccion, createDTO);

        log.debug("Retornando producción: {}", created);
        return ResponseEntity.created(URI.create("/api/v1/producciones/" + created.codigoProduccion())).body(created);
    }

    @PutMapping("/{codigoProduccion}/campos/{idCampo}")
    public ResponseEntity<RespuestaCampoResponseDTO> guardarRespuestaCampo(
            @PathVariable String codigoProduccion,
            @PathVariable Long idCampo,
            @Valid @RequestBody RespuestaCampoRequestDTO request) {

        log.debug("Guardando respuesta para campo: {}, producción: {}", idCampo, codigoProduccion);

        RespuestaCampoResponseDTO respuesta = produccionManagementService.guardarRespuestaCampo(
                codigoProduccion, idCampo, request);
        log.debug("Respuesta guardada exitosamente: {}", respuesta);
        return ResponseEntity.ok(respuesta);

    }

    @GetMapping("/{codigoProduccion}/estado-actual")
    public ResponseEntity<EstadoActualProduccionResponseDTO> obtenerEstadoActual(
            @PathVariable String codigoProduccion) {

        log.debug("Solicitando estado actual de producción: {}", codigoProduccion);

        EstadoActualProduccionResponseDTO estado = produccionManagementService.obtenerEstadoActual(codigoProduccion);

        log.debug("Retornando estado actual para: {}", codigoProduccion);
        return ResponseEntity.ok(estado);
    }


}
