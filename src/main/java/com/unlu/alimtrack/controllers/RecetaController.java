package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recetas")
public class RecetaController {

  private final RecetaService recetaService;

  @GetMapping

  public ResponseEntity<List<RecetaResponseDTO>> getAllRecetas() {
    return ResponseEntity.ok(recetaService.findAllRecetas());
  }

  @GetMapping("/{codigoReceta}")
  public ResponseEntity<RecetaResponseDTO> getReceta(@PathVariable String codigoReceta) {
    return ResponseEntity.ok(recetaService.findReceta(codigoReceta));
  }

  @PutMapping("/{codigoReceta}")
  public ResponseEntity<RecetaResponseDTO> updateReceta(@PathVariable String codigoReceta,
     @Valid @RequestBody RecetaModifyDTO receta) {
    return ResponseEntity.ok(recetaService.updateReceta(codigoReceta, receta));
  }

  @PostMapping("/{codigoReceta}")
  public ResponseEntity<RecetaResponseDTO> addReceta(@PathVariable String codigoReceta,
     @Valid @RequestBody RecetaCreateDTO receta) {
    RecetaResponseDTO created = recetaService.addReceta(receta);
    return ResponseEntity.created(URI.create("/recipes/" + created.codigoReceta()))
        .body(created);
  }

  @DeleteMapping("/{codigoReceta}")
  public ResponseEntity<Void> deleteReceta(@PathVariable String codigoReceta) {
    recetaService.deleteReceta(codigoReceta);
    return ResponseEntity.noContent().build();
  }


}
