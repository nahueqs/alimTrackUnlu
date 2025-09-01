package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recetas")
public class VersionRecetaController {

  final VersionRecetaService versionRecetaService;

  public VersionRecetaController(VersionRecetaService versionRecetaService) {
    this.versionRecetaService = versionRecetaService;
  }

  //devuelve todas las versiones
  @GetMapping("/versiones")
  public ResponseEntity<List<VersionRecetaResponseDTO>> getAllVersiones() {
    return ResponseEntity.ok(versionRecetaService.findAllVersiones());
  }

  @GetMapping("/versiones/{codigoVersion}")
  public ResponseEntity<VersionRecetaResponseDTO> getByCodigoVersion(
      @PathVariable String codigoVersion) {
    return ResponseEntity.ok(versionRecetaService.findByCodigoVersion(codigoVersion));
  }

  @GetMapping("/{codigoReceta}/versiones")
  public ResponseEntity<List<VersionRecetaResponseDTO>> getAllByCodigoReceta(
      @PathVariable String codigoReceta) {
    return ResponseEntity.ok(versionRecetaService.findAllByCodigoReceta(codigoReceta));
  }

  @PostMapping("/{codigoRecetaPadre}/versiones")
  public ResponseEntity<VersionRecetaResponseDTO> saveVersionReceta(
      @PathVariable String codigoRecetaPadre, @RequestBody VersionRecetaCreateDTO dto) {
    return ResponseEntity.ok(versionRecetaService.saveVersionReceta(codigoRecetaPadre, dto));
  }


}
