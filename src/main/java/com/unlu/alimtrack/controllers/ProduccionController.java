package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.ProduccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/producciones")
public class ProduccionController {

    private final ProduccionService  produccionService;

    public ProduccionController(ProduccionService produccionService) {
        this.produccionService = produccionService;
    }

    @GetMapping
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProducciones( @RequestParam(required = false) String codigoVersionReceta){
        if (codigoVersionReceta != null) {
            return ResponseEntity.ok(produccionService.getAllProduccionesByCodigoVersionReceta(codigoVersionReceta));
        }
        return ResponseEntity.ok(produccionService.getAllProducciones());
    }

    @GetMapping("/{codigoProduccion}")
    public ResponseEntity<ProduccionResponseDTO> getProduccionByCodigoProduccion(@PathVariable String codigoProduccion){
        return ResponseEntity.ok(produccionService.getByCodigoProduccion(codigoProduccion));
    }

    @GetMapping("/en-curso")
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProduccionesEnCurso(){
        return ResponseEntity.ok(produccionService.getAllProduccionesEnCurso());
    }

    @GetMapping("/finalizadas")
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProduccionesFinalizadas(){
        return ResponseEntity.ok(produccionService.getAllProduccionesFinalizadas());
    }

}
