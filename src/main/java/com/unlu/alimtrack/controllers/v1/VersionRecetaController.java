package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
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
public class VersionRecetaController {

    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final VersionRecetaEstructuraService versionEstructuraService;

    @GetMapping("/versiones-receta")
    public ResponseEntity<List<VersionMetadataResponseDTO>> getAllVersiones() {
        log.info("Solicitud para obtener todas las versiones de recetas");
        List<VersionMetadataResponseDTO> versiones = versionRecetaMetadataService.findAllVersiones();
        log.debug("Retornando {} versiones de recetas", versiones.size());
        return ResponseEntity.ok(versiones);
    }

    @GetMapping("/versiones-receta/{codigoVersion}")
    public ResponseEntity<VersionMetadataResponseDTO> getByCodigoVersion(
            @PathVariable String codigoVersion) {
        log.info("Solicitud para obtener la versión de receta con código: {}", codigoVersion);
        VersionMetadataResponseDTO version = versionRecetaMetadataService.findByCodigoVersion(codigoVersion);
        log.debug("Retornando versión de receta: {}", version.codigoVersionReceta());
        return ResponseEntity.ok(version);
    }

    @GetMapping("/recetas/{codigoReceta}/versiones-receta")
    public ResponseEntity<List<VersionMetadataResponseDTO>> getAllByCodigoReceta(
            @PathVariable String codigoReceta) {
        log.info("Solicitud para obtener todas las versiones de la receta con código: {}", codigoReceta);
        List<VersionMetadataResponseDTO> versiones = versionRecetaMetadataService.findAllByCodigoReceta(codigoReceta);
        log.debug("Retornando {} versiones para la receta: {}", versiones.size(), codigoReceta);
        return ResponseEntity.ok(versiones);
    }

    @PostMapping("/recetas/{codigoReceta}/versiones-receta")
    public ResponseEntity<VersionMetadataResponseDTO> saveVersionReceta(
            @PathVariable String codigoReceta, @Valid @RequestBody VersionRecetaCreateDTO dto) {
        log.info("Solicitud para crear una nueva versión para la receta con código: {}", codigoReceta);
        VersionMetadataResponseDTO created = versionRecetaMetadataService.saveVersionReceta(codigoReceta, dto);
        log.info("Versión creada exitosamente: {} para la receta: {}", created.codigoVersionReceta(), codigoReceta);
        return ResponseEntity.created(
                        URI.create("/api/v1/versiones-receta/" + created.codigoVersionReceta()))
                .body(created);
    }

    @PutMapping("/versiones-receta/{codigoVersion}")
    public ResponseEntity<VersionMetadataResponseDTO> updateVersionReceta(@PathVariable String codigoVersion,
                                                                          @Valid @RequestBody VersionRecetaModifyDTO receta) {
        log.info("Solicitud para actualizar la versión de receta con código: {}", codigoVersion);
        VersionMetadataResponseDTO actualizada = versionRecetaMetadataService.updateVersionReceta(codigoVersion, receta);
        log.info("Versión de receta {} actualizada exitosamente", actualizada.codigoVersionReceta());
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/versiones-receta/{codigoVersion}")
    public ResponseEntity<Void> deleteVersionReceta(@PathVariable String codigoVersion) {
        log.info("Solicitud para eliminar la versión de receta con código: {}", codigoVersion);
        versionRecetaMetadataService.deleteVersionReceta(codigoVersion);
        log.info("Versión de receta {} eliminada exitosamente", codigoVersion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/versiones-receta/{codigoVersion}/estructura-completa")
    public ResponseEntity<VersionEstructuraPublicResponseDTO> obtenerEstructuraCompleta(
            @PathVariable String codigoVersion) {
        log.info("Solicitud para obtener la estructura completa de la versión de receta: {}", codigoVersion);
        VersionEstructuraPublicResponseDTO estructura = versionEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("Retornando estructura completa para la versión: {}", estructura.metadata().codigoVersionReceta());
        return ResponseEntity.ok(estructura);
    }
}
