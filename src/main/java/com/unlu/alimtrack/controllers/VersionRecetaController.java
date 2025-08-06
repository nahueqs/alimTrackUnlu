package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.request.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/v")
    public ResponseEntity<List<VersionRecetaResponseDTO>> getAllVersiones() {
        return ResponseEntity.ok(versionRecetaService.getAllVersiones());
    }

    @GetMapping("/{idReceta}/versiones/{idVersion}")
    public ResponseEntity<VersionRecetaResponseDTO> getVersionById(@PathVariable Long idReceta, @PathVariable Long idVersion) {
        return ResponseEntity.ok(versionRecetaService.getVersionById(idReceta, idVersion));
    }

    @GetMapping("/{idReceta}/versiones/")
    public ResponseEntity<List<VersionRecetaResponseDTO>> getVersionesByIdRecetaPadre(@PathVariable Long idReceta) {
        return ResponseEntity.ok(versionRecetaService.getVersionesByIdRecetaPadre(idReceta));
    }

    @PostMapping("/{idReceta}/versiones/")
    public ResponseEntity<VersionRecetaResponseDTO> saveVersionReceta(@PathVariable Long idReceta, @RequestBody VersionRecetaCreateDTO dto) {
        return ResponseEntity.ok(versionRecetaService.saveVersionReceta(idReceta, dto));
    }


}
