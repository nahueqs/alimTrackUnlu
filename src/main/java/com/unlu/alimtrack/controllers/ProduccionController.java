package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.services.ProduccionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/producciones")
public class ProduccionController {

    private final ProduccionService produccionService;

    public ProduccionController(ProduccionService produccionService) {
        this.produccionService = produccionService;
    }

    @GetMapping
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProducciones(@RequestParam(required = false) String codigoVersionReceta,
                                                                          @RequestParam(required = false) String fechaInicio,
                                                                          @RequestParam(required = false) String fechaFin,
                                                                          @RequestParam(required = false) String lote,
                                                                          @RequestParam(required = false) String encargado) {

        List<ProduccionResponseDTO> producciones = produccionService.findProduccionesByFilters(codigoVersionReceta, lote, encargado, fechaInicio, fechaFin);
        log.debug("Produ controller Buscando producciones con filtros: codigoVersionReceta={}, lote={}, encargado={}, fechaInicio={}, fechaFin={}",
                codigoVersionReceta, lote, encargado, fechaInicio, fechaFin);

        return ResponseEntity.ok(producciones);

    }

    @GetMapping("/{codigoProduccion}")
    public ResponseEntity<ProduccionResponseDTO> getProduccionByCodigoProduccion(@PathVariable String codigoProduccion) {
        System.out.println("Usando getProduccionByCodigoProduccion");

        return ResponseEntity.ok(produccionService.getByCodigoProduccion(codigoProduccion));
    }

    @GetMapping("/en-curso")
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProduccionesEnCurso() {
        return ResponseEntity.ok(produccionService.getAllProduccionesEnCurso());
    }

    @GetMapping("/finalizadas")
    public ResponseEntity<List<ProduccionResponseDTO>> getAllProduccionesFinalizadas() {
        return ResponseEntity.ok(produccionService.getAllProduccionesFinalizadas());
    }


}
