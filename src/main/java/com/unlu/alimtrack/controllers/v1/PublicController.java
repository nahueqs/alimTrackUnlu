package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.services.PublicRequestsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
@Tag(name = "Público", description = "Endpoints de acceso público sin autenticación")
public class PublicController {

    private final PublicRequestsService publicRequestService;

    @Operation(summary = "Obtener estructura de producción", description = "Devuelve la estructura completa de la receta asociada a una producción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estructura recuperada exitosamente",
                    content = @Content(schema = @Schema(implementation = VersionEstructuraPublicResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @GetMapping("/producciones/{codigoProduccion}/estructura")
    public ResponseEntity<VersionEstructuraPublicResponseDTO> getEstructuraProduccion(@PathVariable String codigoProduccion) {

        log.info("Solicitud pública para obtener la estructura completa de la codigoProduccion: {}", codigoProduccion);
        VersionEstructuraPublicResponseDTO estructura = publicRequestService.getEstructuraProduccion(codigoProduccion);
        log.debug("Retornando estructura completa para la codigoProduccion {}", codigoProduccion);
        return ResponseEntity.ok(estructura);
    }

    @Operation(summary = "Listar producciones públicas", description = "Obtiene la metadata de todas las producciones disponibles públicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de producciones recuperada exitosamente")
    })
    @GetMapping("/producciones")
    public ResponseEntity<List<MetadataProduccionPublicaResponseDTO>> getAllProduccionesMetadataPublico() {
        log.info("Solicitud pública para obtener todas las producciones");
        List<MetadataProduccionPublicaResponseDTO> producciones = publicRequestService.getAllProduccionesMetadataPublico();
        log.debug("Retornando {} producciones públicas", producciones.size());
        return ResponseEntity.ok(producciones);
    }

    @Operation(summary = "Obtener últimas respuestas públicas", description = "Devuelve el estado actual de respuestas de una producción para vista pública")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuestas recuperadas exitosamente",
                    content = @Content(schema = @Schema(implementation = RespuestasProduccionPublicResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @GetMapping("/producciones/{codigoProduccion}/ultimas-respuestas")
    public ResponseEntity<RespuestasProduccionPublicResponseDTO> getUltimasRespuestasProduccion(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud pública para obtener el estado actual de la producción: {}", codigoProduccion);
        RespuestasProduccionPublicResponseDTO estado = publicRequestService.getEstadoActualProduccionPublico(codigoProduccion);

        log.debug("Retornando estado actual público para la producción {}", codigoProduccion);
        return ResponseEntity.ok(estado);
    }

    @Operation(summary = "Obtener última modificación", description = "Devuelve información básica y fecha de última modificación de una producción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información recuperada exitosamente",
                    content = @Content(schema = @Schema(implementation = EstadoProduccionPublicoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producción no encontrada")
    })
    @GetMapping("/producciones/{codigoProduccion}/ultima-modificacion")
    public ResponseEntity<EstadoProduccionPublicoResponseDTO> getUltimaModificacionProduccion(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud pública para obtener la información pública de la producción: {}", codigoProduccion);
        EstadoProduccionPublicoResponseDTO produccion = publicRequestService.getProduccionPublic(codigoProduccion);
        log.debug("Retornando información pública para la producción {}", codigoProduccion);
        return ResponseEntity.ok(produccion);
    }
}
