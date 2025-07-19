package com.unlu.alimtrack.controllers;


import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.services.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/recetas")
public class RecetaController {
    @Autowired
    RecetaService recetaService;

    @GetMapping
    public ArrayList<RecetaDTO> getAllRecetasDTOS() {
        return recetaService.getAllRecetasDTOS();
    }

    @GetMapping("/test")
    public String test() {
        return "Controlador funcionando";
    }

}
