package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
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
@RequestMapping("/api/v1")
public class VersionRecetaController {

  final VersionRecetaService versionRecetaService;

  //devuelve todas las versiones
  @GetMapping("/versiones")
  public ResponseEntity<List<VersionRecetaResponseDTO>> getAllVersiones() {
    return ResponseEntity.ok(versionRecetaService.findAllVersiones());
  }

  @GetMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
  public ResponseEntity<VersionRecetaResponseDTO> getByCodigoVersion(
      @PathVariable String codigoVersion) {
    return ResponseEntity.ok(versionRecetaService.findByCodigoVersion(codigoVersion));
  }

  @GetMapping("/recetas/{codigoReceta}/versiones")
  public ResponseEntity<List<VersionRecetaResponseDTO>> getAllByCodigoReceta(
      @PathVariable String codigoReceta) {
    return ResponseEntity.ok(versionRecetaService.findAllByCodigoReceta(codigoReceta));
  }

  @PostMapping("/recetas/{codigoReceta}/versiones")
  public ResponseEntity<VersionRecetaResponseDTO> saveVersionReceta(
      @PathVariable String codigoReceta, @Valid @RequestBody VersionRecetaCreateDTO dto) {
    VersionRecetaResponseDTO created = versionRecetaService.saveVersionReceta(codigoReceta, dto);
    return ResponseEntity.created(
            URI.create("/api/v1/recetas/" + codigoReceta + "/versiones/" + created.codigoVersionReceta()))
        .body(created);
  }

  @PutMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
  public ResponseEntity<VersionRecetaResponseDTO> updateVersionReceta(@PathVariable String codigoVersion,
      @RequestBody VersionRecetaModifyDTO receta) {
    VersionRecetaResponseDTO actualizada = versionRecetaService.updateVersionReceta(codigoVersion, receta);
    return ResponseEntity.ok(actualizada);
  }

  @DeleteMapping("/recetas/{codigoReceta}/versiones/{codigoVersion}")
  public ResponseEntity<Void> deleteVersionReceta(@PathVariable String codigoVersion) {
    versionRecetaService.deleteVersionReceta(codigoVersion);
    return ResponseEntity.noContent().build();
  }


}
