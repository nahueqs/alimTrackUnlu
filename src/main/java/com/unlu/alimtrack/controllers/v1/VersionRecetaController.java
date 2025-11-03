package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
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
    final VersionRecetaService versionRecetaService;

    //devuelve todas las versiones
    @GetMapping("/versiones")
    public ResponseEntity<List<VersionRecetaResponseDTO>> getAllVersiones() {
        log.debug("Obteniendo todas las versiones de recetas");
        List<VersionRecetaResponseDTO> versiones = versionRecetaService.findAllVersiones();
        log.debug("Retornando {} versiones de recetas", versiones.size());
        return ResponseEntity.ok(versiones);
    }

    @GetMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
    public ResponseEntity<VersionRecetaResponseDTO> getByCodigoVersion(
            @PathVariable String codigoVersion) {
        log.debug("Buscando versión de receta con código: {}", codigoVersion);
        VersionRecetaResponseDTO version = versionRecetaService.findByCodigoVersion(codigoVersion);
        log.debug("Versión de receta encontrada: {}", version != null ? version.codigoVersionReceta() : "No encontrada");
        return ResponseEntity.ok(version);
    }

    @GetMapping("/recetas/{codigoReceta}/versiones")
    public ResponseEntity<List<VersionRecetaResponseDTO>> getAllByCodigoReceta(
            @PathVariable String codigoReceta) {
        log.debug("Buscando todas las versiones para la receta con código: {}", codigoReceta);
        List<VersionRecetaResponseDTO> versiones = versionRecetaService.findAllByCodigoReceta(codigoReceta);
        log.debug("Retornando {} versiones para la receta: {}", versiones.size(), codigoReceta);
        return ResponseEntity.ok(versiones);
    }

    @PostMapping("/recetas/{codigoReceta}/versiones")
    public ResponseEntity<VersionRecetaResponseDTO> saveVersionReceta(
            @PathVariable String codigoReceta, @Valid @RequestBody VersionRecetaCreateDTO dto) {
        log.debug("Creando nueva versión para la receta con código: {}", codigoReceta);
        VersionRecetaResponseDTO created = versionRecetaService.saveVersionReceta(codigoReceta, dto);
        log.debug("Versión creada exitosamente: {} para la receta: {}", created.codigoVersionReceta(), codigoReceta);
        return ResponseEntity.created(
                        URI.create("/api/v1/recetas/" + codigoReceta + "/versiones/" + created.codigoVersionReceta()))
                .body(created);
    }

    @PutMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
    public ResponseEntity<VersionRecetaResponseDTO> updateVersionReceta(@PathVariable String codigoVersion,
                                                                        @RequestBody VersionRecetaModifyDTO receta) {
        log.debug("Actualizando versión de receta con código: {}", codigoVersion);
        VersionRecetaResponseDTO actualizada = versionRecetaService.updateVersionReceta(codigoVersion, receta);
        log.debug("Versión de receta actualizada exitosamente: {}", actualizada.codigoVersionReceta());
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
    public ResponseEntity<Void> deleteVersionReceta(@PathVariable String codigoVersion) {
        log.debug("Eliminando versión de receta con código: {}", codigoVersion);
        versionRecetaService.deleteVersionReceta(codigoVersion);
        log.debug("Versión de receta eliminada exitosamente: {}", codigoVersion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}/secciones")
    public ResponseEntity<List<SeccionResponseDTO>> getAllSeccionesByVersionReceta(@PathVariable String codigoVersion) {
        log.debug("Obteniendo todas las secciones para la versión de receta con código: {}", codigoVersion);
        List<SeccionResponseDTO> secciones = versionRecetaService.findAllSeccionesByVersionReceta(codigoVersion);
        log.debug("Retornando {} secciones para la versión de receta: {}", secciones.size(), codigoVersion);
        return ResponseEntity.ok(secciones);
    }


}
