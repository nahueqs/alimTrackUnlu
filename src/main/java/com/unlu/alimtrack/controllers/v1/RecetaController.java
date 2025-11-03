package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.RecetaResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RecetaController.class);
    private final RecetaService recetaService;

    @GetMapping
    public ResponseEntity<List<RecetaResponseDTO>> getAllRecetas() {
        log.debug("Obteniendo todas las recetas");
        List<RecetaResponseDTO> recetas = recetaService.findAllRecetas();
        log.debug("Retornando {} recetas", recetas.size());
        return ResponseEntity.ok(recetas);
    }

    @GetMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> getReceta(@PathVariable String codigoReceta) {
        log.debug("Buscando receta con código: {}", codigoReceta);
        RecetaResponseDTO receta = recetaService.findReceta(codigoReceta);
        log.debug("Receta encontrada: {}", receta != null ? receta.codigoReceta() : "No encontrada");
        return ResponseEntity.ok(receta);
    }

    @PutMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> updateReceta(@PathVariable String codigoReceta,
                                                          @Valid @RequestBody RecetaModifyDTO receta) {
        log.debug("Actualizando receta con código: {}", codigoReceta);
        RecetaResponseDTO updated = recetaService.updateReceta(codigoReceta, receta);
        log.debug("Receta actualizada exitosamente: {}", updated.codigoReceta());
        return ResponseEntity.ok(updated);
    }

    @PostMapping()
    public ResponseEntity<RecetaResponseDTO> addReceta(@PathVariable String codigoReceta,
                                                       @Valid @RequestBody RecetaCreateDTO receta) {
        log.debug("Creando nueva receta con código: {}", codigoReceta);
        RecetaResponseDTO created = recetaService.addReceta(codigoReceta, receta);
        log.debug("Receta creada exitosamente: {}", created.codigoReceta());
        return ResponseEntity.created(URI.create("/api/v1/recetas/" + created.codigoReceta()))
                .body(created);
    }

    @DeleteMapping("/{codigoReceta}")
    public ResponseEntity<Void> deleteReceta(@PathVariable String codigoReceta) {
        log.debug("Eliminando receta con código: {}", codigoReceta);
        recetaService.deleteReceta(codigoReceta);
        log.debug("Receta eliminada exitosamente: {}", codigoReceta);
        return ResponseEntity.noContent().build();
    }


}
