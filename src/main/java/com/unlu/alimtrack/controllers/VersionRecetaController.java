package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{idReceta}/versiones/{idVersion}")
    public ResponseEntity<VersionRecetaResponseDTO> getVersionById(@PathVariable Long idReceta, @PathVariable Long idVersion) {
        return ResponseEntity.ok(versionRecetaService.findVersionRecetaByIdRecetaAndIdVersion(idReceta, idVersion));
    }

    @GetMapping("/{idReceta}/versiones")
    public ResponseEntity<List<VersionRecetaResponseDTO>> getVersionesByIdRecetaPadre(@PathVariable Long idReceta) {
        return ResponseEntity.ok(versionRecetaService.findAllVersionesByIdRecetaPadre(idReceta));
    }

    @PostMapping("/{codigoRecetaPadre}/versiones")
    public ResponseEntity<VersionRecetaResponseDTO> saveVersionReceta(@PathVariable String codigoRecetaPadre, @RequestBody VersionRecetaCreateDTO dto) {
        return ResponseEntity.ok(versionRecetaService.saveVersionReceta(codigoRecetaPadre, dto));
    }

    // FALTA DELETE

}
