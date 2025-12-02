package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.request.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.ProduccionQueryService;
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

    private final ProduccionQueryService produccionQueryService;
    private final ProduccionManagementService produccionManagementService;

    @GetMapping
    public ResponseEntity<List<ProduccionMetadataResponseDTO>> getAllProduccionesMetadata(@ModelAttribute ProduccionFilterRequestDTO filtros) {
        log.info("Solicitud para obtener todas las producciones con filtros: {}", filtros);
        List<ProduccionMetadataResponseDTO> producciones = produccionQueryService.getAllProduccionesMetadata(filtros);
        log.debug("Retornando {} producciones", producciones.size());
        return ResponseEntity.ok(producciones);
    }

    @GetMapping("/{codigoProduccion}")
    public ResponseEntity<ProduccionMetadataResponseDTO> getMetadataByCodigoProduccion(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud para obtener la producción con código: {}", codigoProduccion);
        ProduccionMetadataResponseDTO produccion = produccionQueryService.findByCodigoProduccion(codigoProduccion);
        log.debug("Retornando producción: {}", produccion.codigoProduccion());
        return ResponseEntity.ok(produccion);
    }


    @PostMapping()
    public ResponseEntity<ProduccionMetadataResponseDTO> iniciarProduccion(@Valid @RequestBody ProduccionCreateDTO createDTO) {
        log.info("Solicitud para iniciar una nueva producción con código: {}", createDTO.codigoProduccion());
        ProduccionMetadataResponseDTO created = produccionManagementService.iniciarProduccion(createDTO);
        log.info("Producción {} iniciada exitosamente", created.codigoProduccion());
        return ResponseEntity.created(URI.create("/api/v1/producciones/" + created.codigoProduccion())).body(created);
    }

    @PutMapping("/{codigoProduccion}/campos/{idCampo}")
    public ResponseEntity<RespuestaCampoResponseDTO> guardarRespuestaCampo(
            @PathVariable String codigoProduccion,
            @PathVariable Long idCampo,
            @Valid @RequestBody RespuestaCampoRequestDTO request) {
        log.info("Solicitud para guardar respuesta en el campo {} de la producción {}", idCampo, codigoProduccion);
        RespuestaCampoResponseDTO respuesta = produccionManagementService.guardarRespuestaCampo(
                codigoProduccion, idCampo, request);
        log.info("Respuesta guardada exitosamente para el campo {} en la producción {}", idCampo, codigoProduccion);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{codigoProduccion}/estado-actual")
    public ResponseEntity<UltimasRespuestasProduccionResponseDTO> getUltimasRespuestas(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud para obtener el estado actual completo de la producción: {}", codigoProduccion);
        UltimasRespuestasProduccionResponseDTO estado = produccionManagementService.getUltimasRespuestas(codigoProduccion);
        log.debug("Retornando estado actual completo para la producción {}", codigoProduccion);
        return ResponseEntity.ok(estado);
    }

    @PutMapping("/{codigoProduccion}/cambiar-estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable String codigoProduccion, @Valid @RequestBody ProduccionCambioEstadoRequestDTO request) {
        log.info("Solicitud para cambiar el estado de la producción {} a {}", codigoProduccion, request.valor());
        produccionManagementService.updateEstado(codigoProduccion, request);
        log.info("Estado de la producción {} cambiado exitosamente a {}", codigoProduccion, request.valor());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{codigoProduccion}/public")
    public ResponseEntity<EstadoProduccionPublicoResponseDTO> getEstadoProduccion(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud para obtener la información pública de la producción: {}", codigoProduccion);
        EstadoProduccionPublicoResponseDTO produccion = produccionQueryService.getEstadoProduccion(codigoProduccion);
        log.debug("Retornando información pública para la producción {}", codigoProduccion);
        return ResponseEntity.ok(produccion);
    }

    @GetMapping("/test")
    public UltimasRespuestasProduccionResponseDTO test() {
        return produccionManagementService.test();
    }

}
