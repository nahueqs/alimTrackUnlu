package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.request.VersionRecetaCreateDTO;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recetas")
public class VersionRecetaController {
    @Autowired
    VersionRecetaService versionRecetaService;

    //devuelve todas las versiones
    @GetMapping("/v")
    public List<VersionRecetaCreateDTO> getAllVersiones() {
        return versionRecetaService.getAllVersiones();
    }

    @GetMapping("/{idReceta}/versiones/{idVersion}")
    public VersionRecetaCreateDTO getVersionById(@PathVariable Long idReceta, @PathVariable Long idVersion) {
        return versionRecetaService.getVersionById(idReceta, idVersion);
    }

    @GetMapping("/{idReceta}/versiones/")
    public List<VersionRecetaCreateDTO> getVersionesByIdRecetaPadre(@PathVariable Long idReceta) {
        return versionRecetaService.getVersionesByIdRecetaPadre(idReceta);
    }

    @PostMapping("/{idReceta}/versiones/")
    public VersionRecetaCreateDTO saveVersionReceta(@PathVariable Long idReceta, @RequestBody VersionRecetaCreateDTO dto) {
            return versionRecetaService.saveVersionReceta(idReceta, dto);
    }


}
