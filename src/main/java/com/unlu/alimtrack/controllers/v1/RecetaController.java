package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
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
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    @GetMapping
    public ResponseEntity<List<RecetaMetadataResponseDTO>> getAllRecetas() {
        log.info("Solicitud para obtener todas las recetas");
        List<RecetaMetadataResponseDTO> recetas = recetaService.findAllRecetas();
        log.debug("Retornando {} recetas", recetas.size());
        return ResponseEntity.ok(recetas);
    }

    @GetMapping("/{codigoReceta}")
    public ResponseEntity<RecetaMetadataResponseDTO> getReceta(@PathVariable String codigoReceta) {
        log.info("Solicitud para obtener la receta con código: {}", codigoReceta);
        RecetaMetadataResponseDTO receta = recetaService.findReceta(codigoReceta);
        log.debug("Retornando receta: {}", receta.codigoReceta());
        return ResponseEntity.ok(receta);
    }

    @PutMapping("/{codigoReceta}")
    public ResponseEntity<RecetaMetadataResponseDTO> updateReceta(@PathVariable String codigoReceta,
                                                                  @Valid @RequestBody RecetaModifyDTO receta) {
        log.info("Solicitud para actualizar la receta con código: {}", codigoReceta);
        RecetaMetadataResponseDTO updated = recetaService.updateReceta(codigoReceta, receta);
        log.info("Receta {} actualizada exitosamente", updated.codigoReceta());
        return ResponseEntity.ok(updated);
    }

    @PostMapping()
    public ResponseEntity<RecetaMetadataResponseDTO> addReceta(
            @Valid @RequestBody RecetaCreateDTO receta) {
        log.info("Solicitud para crear una nueva receta con código: {}");
        RecetaMetadataResponseDTO created = recetaService.addReceta(receta);
        log.info("Receta creada exitosamente: {}", created.codigoReceta());
        return ResponseEntity.created(URI.create("/api/v1/recetas/" + created.codigoReceta()))
                .body(created);
    }

    @DeleteMapping("/{codigoReceta}")
    public ResponseEntity<Void> deleteReceta(@PathVariable String codigoReceta) {
        log.info("Solicitud para eliminar la receta con código: {}", codigoReceta);
        recetaService.deleteReceta(codigoReceta);
        log.info("Receta {} eliminada exitosamente", codigoReceta);
        return ResponseEntity.noContent().build();
    }
}
