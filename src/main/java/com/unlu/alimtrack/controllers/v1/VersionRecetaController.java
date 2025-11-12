package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaCompletaResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaEstructuraService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class VersionRecetaController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VersionRecetaController.class);
    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final VersionRecetaEstructuraService versionEstructuraService;

    //devuelve todas las versiones
    @GetMapping("/recetas/versiones")
    public ResponseEntity<List<VersionRecetaMetadataResponseDTO>> getAllVersiones() {
        log.debug("Obteniendo todas las versiones de recetas");
        List<VersionRecetaMetadataResponseDTO> versiones = versionRecetaMetadataService.findAllVersiones();
        log.debug("Retornando {} versiones de recetas", versiones.size());
        return ResponseEntity.ok(versiones);
    }

    @GetMapping("/recetas/versiones/{codigoVersion}")
    public ResponseEntity<VersionRecetaMetadataResponseDTO> getByCodigoVersion(
            @PathVariable String codigoVersion) {
        log.debug("Buscando versión de receta con código: {}", codigoVersion);
        VersionRecetaMetadataResponseDTO version = versionRecetaMetadataService.findByCodigoVersion(codigoVersion);
        log.debug("Versión de receta encontrada: {}", version != null ? version.codigoVersionReceta() : "No encontrada");
        return ResponseEntity.ok(version);
    }

    @GetMapping("/recetas/{codigoReceta}/versiones")
    public ResponseEntity<List<VersionRecetaMetadataResponseDTO>> getAllByCodigoReceta(
            @PathVariable String codigoReceta) {
        log.debug("Buscando todas las versiones para la receta con código: {}", codigoReceta);
        List<VersionRecetaMetadataResponseDTO> versiones = versionRecetaMetadataService.findAllByCodigoReceta(codigoReceta);
        log.debug("Retornando {} versiones para la receta: {}", versiones.size(), codigoReceta);
        return ResponseEntity.ok(versiones);
    }

    @PostMapping("/recetas/{codigoReceta}/versiones")
    public ResponseEntity<VersionRecetaMetadataResponseDTO> saveVersionReceta(
            @PathVariable String codigoReceta, @Valid @RequestBody VersionRecetaCreateDTO dto) {
        log.debug("Creando nueva versión para la receta con código: {}", codigoReceta);
        VersionRecetaMetadataResponseDTO created = versionRecetaMetadataService.saveVersionReceta(codigoReceta, dto);
        log.debug("Versión creada exitosamente: {} para la receta: {}", created.codigoVersionReceta(), codigoReceta);
        return ResponseEntity.created(
                        URI.create("/api/v1/recetas/" + codigoReceta + "/versiones/" + created.codigoVersionReceta()))
                .body(created);
    }

    @PutMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
    public ResponseEntity<VersionRecetaMetadataResponseDTO> updateVersionReceta(@PathVariable String codigoVersion,
                                                                                @RequestBody VersionRecetaModifyDTO receta) {
        log.debug("Actualizando versión de receta con código: {}", codigoVersion);
        VersionRecetaMetadataResponseDTO actualizada = versionRecetaMetadataService.updateVersionReceta(codigoVersion, receta);
        log.debug("Versión de receta actualizada exitosamente: {}", actualizada.codigoVersionReceta());
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
    public ResponseEntity<Void> deleteVersionReceta(@PathVariable String codigoVersion) {
        log.debug("Eliminando versión de receta con código: {}", codigoVersion);
        versionRecetaMetadataService.deleteVersionReceta(codigoVersion);
        log.debug("Versión de receta eliminada exitosamente: {}", codigoVersion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recetas/versiones/{codigoVersion}/estructura-completa")
    public ResponseEntity<VersionRecetaCompletaResponseDTO> obtenerEstructuraCompleta(
            @PathVariable String codigoVersion) {
        log.debug("Obteniendo todas las estructura para la versión de receta con código: {}", codigoVersion);
        VersionRecetaCompletaResponseDTO estructura = versionEstructuraService.getVersionRecetaCompletaResponseDTOByCodigo(codigoVersion);
        log.debug("Retornando {} estructura para la versión de receta: {}", estructura.versionRecetaMetadata().codigoVersionReceta(), codigoVersion);

        return ResponseEntity.ok(estructura);
    }


}
