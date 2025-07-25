package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.request.versionRecetaCreateDto;
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
    public List<versionRecetaCreateDto> getAllVersiones() {
        return versionRecetaService.getAllVersiones();
    }

    @GetMapping("/{idReceta}/versiones/{idVersion}")
    public versionRecetaCreateDto getVersionById(@PathVariable Long idReceta, @PathVariable Long idVersion) {
        return versionRecetaService.getVersionById(idReceta, idVersion);
    }

    @GetMapping("/{idReceta}/versiones/")
    public List<versionRecetaCreateDto> getVersionesByIdReceta(@PathVariable Long idReceta) {
        return versionRecetaService.getVersionesByIdReceta(idReceta);
    }

    @PostMapping("/{idReceta}/versiones/")
    public versionRecetaCreateDto saveVersionReceta(@PathVariable Long idReceta, @RequestBody versionRecetaCreateDto dto) {
            return versionRecetaService.saveVersionReceta(idReceta, dto);
    }


}
