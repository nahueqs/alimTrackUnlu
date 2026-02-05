package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1")
@Tag(name = "Versiones de Receta", description = "Gestión de versiones y estructura de recetas")
public class VersionRecetaController {

    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final VersionRecetaEstructuraService versionEstructuraService;

    @Operation(summary = "Listar todas las versiones", description = "Obtiene la metadata de todas las versiones de recetas")
    @GetMapping("/versiones-receta")
    public ResponseEntity<List<VersionMetadataResponseDTO>> getAllVersiones() {
        log.info("Solicitud para obtener todas las versiones de recetas");
        List<VersionMetadataResponseDTO> versiones = versionRecetaMetadataService.findAllVersiones();
        log.debug("Retornando {} versiones de recetas", versiones.size());
        return ResponseEntity.ok(versiones);
    }

    @Operation(summary = "Obtener versión por código", description = "Devuelve la metadata de una versión específica")
    @GetMapping("/versiones-receta/{codigoVersion}")
    public ResponseEntity<VersionMetadataResponseDTO> getByCodigoVersion(
            @PathVariable String codigoVersion) {
        log.info("Solicitud para obtener la versión de receta con código: {}", codigoVersion);
        VersionMetadataResponseDTO version = versionRecetaMetadataService.findByCodigoVersion(codigoVersion);
        log.debug("Retornando versión de receta: {}", version.codigoVersionReceta());
        return ResponseEntity.ok(version);
    }

    @Operation(summary = "Listar versiones de una receta", description = "Obtiene todas las versiones asociadas a una receta padre")
    @GetMapping("/recetas/{codigoReceta}/versiones-receta")
    public ResponseEntity<List<VersionMetadataResponseDTO>> getAllByCodigoReceta(
            @PathVariable String codigoReceta) {
        log.info("Solicitud para obtener todas las versiones de la receta con código: {}", codigoReceta);
        List<VersionMetadataResponseDTO> versiones = versionRecetaMetadataService.findAllByCodigoReceta(codigoReceta);
        log.debug("Retornando {} versiones para la receta: {}", versiones.size(), codigoReceta);
        return ResponseEntity.ok(versiones);
    }

    @Operation(summary = "Crear versión de receta", description = "Crea una nueva versión para una receta existente")
    @PostMapping("/recetas/versiones-receta")
    public ResponseEntity<VersionMetadataResponseDTO> saveVersionReceta(@Valid @RequestBody VersionRecetaCreateDTO dto) {
        log.info("Solicitud para crear una nueva versión para la receta padre: {}", dto.codigoRecetaPadre());
        VersionMetadataResponseDTO created = versionRecetaMetadataService.saveVersionReceta(dto.codigoRecetaPadre(), dto);
        log.info("Versión creada exitosamente: {} para la receta: {}", created.codigoVersionReceta(), dto.codigoRecetaPadre());
        return ResponseEntity.created(
                        URI.create("/api/v1/versiones-receta/" + created.codigoVersionReceta()))
                .body(created);
    }

    @Operation(summary = "Actualizar versión", description = "Modifica la metadata de una versión de receta")
    @PutMapping("/versiones-receta/{codigoVersion}")
    public ResponseEntity<VersionMetadataResponseDTO> updateVersionReceta(@PathVariable String codigoVersion,
                                                                          @Valid @RequestBody VersionRecetaModifyDTO receta) {
        log.info("Solicitud para actualizar la versión de receta con código: {}", codigoVersion);
        VersionMetadataResponseDTO actualizada = versionRecetaMetadataService.updateVersionReceta(codigoVersion, receta);
        log.info("Versión de receta {} actualizada exitosamente", actualizada.codigoVersionReceta());
        return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Eliminar versión", description = "Borra una versión de receta si no tiene producciones asociadas")
    @DeleteMapping("/versiones-receta/{codigoVersion}")
    public ResponseEntity<Void> deleteVersionReceta(@PathVariable String codigoVersion) {
        log.info("Solicitud para eliminar la versión de receta con código: {}", codigoVersion);
        versionRecetaMetadataService.deleteVersionReceta(codigoVersion);
        log.info("Versión de receta {} eliminada exitosamente", codigoVersion);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener estructura completa", description = "Devuelve la estructura jerárquica completa (secciones, campos, tablas) de una versión")
    @GetMapping("/versiones-receta/{codigoVersion}/estructura-completa")
    public ResponseEntity<VersionEstructuraPublicResponseDTO> obtenerEstructuraCompleta(
            @PathVariable String codigoVersion) {
        log.info("Solicitud para obtener la estructura completa de la versión de receta: {}", codigoVersion);
        VersionEstructuraPublicResponseDTO estructura = versionEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("Retornando estructura completa para la versión: {}", estructura.metadata().codigoVersionReceta());
        return ResponseEntity.ok(estructura);
    }
}
