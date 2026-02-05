package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaCampoRequestDTO;
import com.unlu.alimtrack.DTOS.request.respuestas.RespuestaTablaRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.UltimasRespuestasProduccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.services.ProduccionManagementService;
import com.unlu.alimtrack.services.ProduccionQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Producciones", description = "Gestión de procesos productivos")
public class ProduccionController {

    private final ProduccionQueryService produccionQueryService;
    private final ProduccionManagementService produccionManagementService;

    @Operation(summary = "Listar producciones", description = "Obtiene todas las producciones con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de producciones recuperada exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<ProduccionMetadataResponseDTO>> getAllProduccionesMetadata(@ModelAttribute ProduccionFilterRequestDTO filtros) {
        log.info("Solicitud para obtener todas las producciones con filtros: {}", filtros);
        List<ProduccionMetadataResponseDTO> producciones = produccionQueryService.getAllProduccionesMetadata(filtros);
        log.debug("Retornando {} producciones", producciones.size());
        return ResponseEntity.ok(producciones);
    }

    @Operation(summary = "Obtener producción por código", description = "Devuelve la metadata de una producción específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producción encontrada",
                    content = @Content(schema = @Schema(implementation = ProduccionMetadataResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @GetMapping("/{codigoProduccion}")
    public ResponseEntity<ProduccionMetadataResponseDTO> getMetadataByCodigoProduccion(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud para obtener la producción con código: {}", codigoProduccion);
        ProduccionMetadataResponseDTO produccion = produccionQueryService.findByCodigoProduccion(codigoProduccion);
        log.debug("Retornando producción: {}", produccion.codigoProduccion());
        return ResponseEntity.ok(produccion);
    }


    @Operation(summary = "Iniciar producción", description = "Crea una nueva instancia de producción basada en una receta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producción iniciada exitosamente",
                    content = @Content(schema = @Schema(implementation = ProduccionMetadataResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El código de producción ya existe"),
            @ApiResponse(responseCode = "400", description = "Datos de creación inválidos")
    })
    @PostMapping()
    public ResponseEntity<ProduccionMetadataResponseDTO> iniciarProduccion(@Valid @RequestBody ProduccionCreateDTO createDTO) {
        log.info("Solicitud para iniciar una nueva producción con código: {}", createDTO.codigoProduccion());
        ProduccionMetadataResponseDTO created = produccionManagementService.iniciarProduccion(createDTO);
        log.info("Producción {} iniciada exitosamente", created.codigoProduccion());
        return ResponseEntity.created(URI.create("/api/v1/producciones/" + created.codigoProduccion())).body(created);
    }

    @Operation(summary = "Guardar respuesta de campo", description = "Registra o actualiza el valor de un campo simple")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta guardada exitosamente",
                    content = @Content(schema = @Schema(implementation = RespuestaCampoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Valor inválido para el tipo de campo"),
            @ApiResponse(responseCode = "404", description = "Producción o campo no encontrado")
    })
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

    @Operation(summary = "Guardar respuesta de tabla", description = "Registra o actualiza el valor de una celda en una tabla")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta de tabla guardada exitosamente",
                    content = @Content(schema = @Schema(implementation = RespuestaCeldaTablaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Valor inválido o coordenadas incorrectas"),
            @ApiResponse(responseCode = "404", description = "Producción, tabla, fila o columna no encontrada")
    })
    @PutMapping("/{codigoProduccion}/tablas/{idTabla}/{idFila}/{idColumna}")
    public ResponseEntity<RespuestaCeldaTablaResponseDTO> guardarRespuestaCeldaTabla(
            @PathVariable String codigoProduccion,
            @PathVariable Long idTabla,
            @PathVariable Long idFila,
            @PathVariable Long idColumna,
            @Valid @RequestBody RespuestaTablaRequestDTO request) {
        RespuestaCeldaTablaResponseDTO respuesta = produccionManagementService.guardarRespuestaCeldaTabla(
                codigoProduccion, idTabla, idFila, idColumna, request);
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Obtener últimas respuestas", description = "Devuelve el estado actual completo de respuestas de la producción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuestas recuperadas exitosamente",
                    content = @Content(schema = @Schema(implementation = UltimasRespuestasProduccionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @GetMapping("/{codigoProduccion}/ultimas-respuestas")
    public ResponseEntity<UltimasRespuestasProduccionResponseDTO> getUltimasRespuestas(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud para obtener el estado actual completo de la producción: {}", codigoProduccion);
        UltimasRespuestasProduccionResponseDTO estado = produccionManagementService.getUltimasRespuestas(codigoProduccion);
        log.debug("Retornando estado actual completo para la producción {}", codigoProduccion);
        return ResponseEntity.ok(estado);
    }

    @Operation(summary = "Cambiar estado", description = "Actualiza el estado de la producción (ej. EN_PROCESO a FINALIZADA)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado cambiado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida"),
            @ApiResponse(responseCode = "403", description = "Operación no permitida (ej. usuario inactivo)")
    })
    @PutMapping("/{codigoProduccion}/cambiar-estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable String codigoProduccion, @Valid @RequestBody ProduccionCambioEstadoRequestDTO request) {
        log.info("Solicitud para cambiar el estado de la producción {} a {}", codigoProduccion, request.valor());
        produccionManagementService.updateEstado(codigoProduccion, request);
        log.info("Estado de la producción {} cambiado exitosamente a {}", codigoProduccion, request.valor());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar metadata", description = "Modifica datos básicos como lote, encargado u observaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Metadata actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @PutMapping("/{codigoProduccion}/metadata")
    public ResponseEntity<Void> updateMetadata(@PathVariable String codigoProduccion, @Valid @RequestBody ProduccionMetadataModifyRequestDTO request) {
        log.info("Solicitud para cambiar la metadata de la producción {}", codigoProduccion);
        produccionManagementService.updateMetadata(codigoProduccion, request);
        log.info("Estado de la producción {} metadata cambiado exitosamente", codigoProduccion);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener estado público", description = "Devuelve información resumida y pública de la producción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado público recuperado",
                    content = @Content(schema = @Schema(implementation = EstadoProduccionPublicoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @GetMapping("/{codigoProduccion}/estado-actual")
    public ResponseEntity<EstadoProduccionPublicoResponseDTO> getEstadoProduccion(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud para obtener la información pública de la producción: {}", codigoProduccion);
        EstadoProduccionPublicoResponseDTO produccion = produccionQueryService.getEstadoProduccion(codigoProduccion);
        log.debug("Retornando información pública para la producción {}", codigoProduccion);
        return ResponseEntity.ok(produccion);
    }

    @Operation(summary = "Eliminar producción", description = "Borra una producción del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producción eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @DeleteMapping("/{codigoProduccion}")
    public ResponseEntity<Void> deleteProduccion(@PathVariable String codigoProduccion) {
        log.info("Solicitud para eliminar la producción con código: {}", codigoProduccion);
        produccionManagementService.deleteProduccion(codigoProduccion);
        log.info("Producción {} eliminada exitosamente", codigoProduccion);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Test endpoint", description = "Endpoint de prueba")
    @GetMapping("/test")
    public UltimasRespuestasProduccionResponseDTO test() {
        return produccionManagementService.test();
    }

}
