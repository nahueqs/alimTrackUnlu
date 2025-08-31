package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {

        this.recetaService = recetaService;
    }

    @GetMapping

    public ResponseEntity<List<RecetaResponseDTO>> getAllRecetas() {
        return ResponseEntity.ok(recetaService.findAllRecetasResponseDTOS());
    }

    @GetMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> getReceta(@PathVariable String codigoReceta) {
        return ResponseEntity.ok(recetaService.findRecetaByCodigoReceta(codigoReceta));
    }

    @PutMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> updateReceta(@PathVariable String codigoReceta, @RequestBody RecetaModifyDTO receta) {
        RecetaResponseDTO actualizada = recetaService.updateReceta(codigoReceta, receta);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{codigoReceta}")
    public ResponseEntity<Void> deleteReceta(@PathVariable String codigoReceta) {
        recetaService.deleteRecetaByCodigoReceta(codigoReceta);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> addReceta(@PathVariable String codigoReceta, @RequestBody RecetaCreateDTO receta) {
        return ResponseEntity.ok(recetaService.addReceta(receta));
    }

}
