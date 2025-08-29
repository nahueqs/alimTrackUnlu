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
/*
    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> getRecetaById(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.findRecetaById(id));
    }*/

    @GetMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> getRecetaByCodigoReceta(@PathVariable String codigoReceta) {
        return ResponseEntity.ok(recetaService.findRecetaByCodigoReceta(codigoReceta));
    }

    @PutMapping("/{codigoReceta}")
    public ResponseEntity<RecetaResponseDTO> updateReceta(@PathVariable String codigoReceta, @RequestBody RecetaModifyDTO receta) {
        RecetaResponseDTO actualizada = recetaService.updateReceta(codigoReceta, receta);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecetaById(@PathVariable Long id) {
        recetaService.deleteRecetaByID(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> deleteRecetaodigoReceta(@PathVariable String codigo) {
        recetaService.deleteRecetaByCodigoReceta(codigo);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> addReceta(@RequestBody RecetaCreateDTO receta) {
        return ResponseEntity.ok(recetaService.addReceta(receta));
    }

}
