package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.services.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recetas")
public class RecetaController {
    @Autowired
    RecetaService recetaService;

    @GetMapping
    public List<RecetaDto> getAllRecetasDTOS() {
        return recetaService.getAllRecetasDTOS();
    }

    @GetMapping("/{id}")
    public RecetaDto getRecetaDtoById(@PathVariable Long id) {
        return recetaService.getRecetaDtoById(id);
    }

    @PutMapping("/{id}")
    public void updateReceta2(@RequestBody RecetaDto receta) {
        recetaService.updateReceta(receta);
    }

    @DeleteMapping("/{id}")
    public void deleteRecetaById(@PathVariable Long id) {
        recetaService.deleteRecetaByID(id);
    }

    @GetMapping("/test")
    public String test() {
        return "Controlador funcionando";
    }

}
