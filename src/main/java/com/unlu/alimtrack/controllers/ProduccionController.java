package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.ProduccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/producciones")
public class ProduccionController {

    private final ProduccionService  produccionService;

    public ProduccionController(ProduccionService produccionService) {
        this.produccionService = produccionService;
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping()
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProducciones(){
        return ResponseEntity.ok(produccionService.getAllProducciones());
    }


}
