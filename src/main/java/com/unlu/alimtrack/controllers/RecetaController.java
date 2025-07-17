package com.unlu.alimtrack.controllers;


import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.services.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/receta")
public class RecetaController {
    @Autowired
    RecetaService recetaService;

    @GetMapping
    public ArrayList<RecetaDTO> getAllRecetas() {
        return  recetaService.getAllRecetas();
    }

    @GetMapping("/test")
    public String test() {
        return "Controlador funcionando";
    }

}
