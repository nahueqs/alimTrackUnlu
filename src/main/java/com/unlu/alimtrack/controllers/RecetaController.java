package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok(recetaService.getAllRecetasResponseDTOS());
    }

    @GetMapping("/{id}/")
    public ResponseEntity<RecetaResponseDTO> getRecetaById(@PathVariable Long id) {
        return ResponseEntity.ok(recetaService.getRecetaResponseDTOById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> updateReceta(@RequestBody RecetaDto receta) {
        RecetaResponseDTO actualizada = recetaService.updateReceta(receta);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecetaById(@PathVariable Long id) {
        recetaService.deleteRecetaByID(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String test() {
        return "Controlador funcionando";
    }

}
