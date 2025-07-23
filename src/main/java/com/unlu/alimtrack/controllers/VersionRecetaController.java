package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.VersionRecetaDto;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recetas")
public class VersionRecetaController {
    @Autowired
    VersionRecetaService versionRecetaService;

    //devuelve todas las versiones
    @GetMapping("/v")
    public List<VersionRecetaDto> getAllVersiones() {
        return versionRecetaService.getAllVersiones();
    }

    @GetMapping("/{idReceta}/versiones/{idVersion}")
    public VersionRecetaDto getVersionById(@PathVariable Long idReceta, @PathVariable Long idVersion) {
        return versionRecetaService.getVersionById(idReceta, idVersion);
    }









}
