package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.dtos.VersionRecetaDto;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.services.VersionRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recetas/v")
public class VersionRecetaController {
    @Autowired
    VersionRecetaService versionRecetaService;

    @GetMapping()
    public List<VersionRecetaDto> getAllVersiones() {
        return versionRecetaService.getAllVersiones();
    }

    /*@GetMapping("/{id}/versiones")
    public List<VersionRecetaDto> getAllVersionesDeReceta(@PathVariable Long id) {
        return versionRecetaService.getAllVersiones();
    }*/

}
