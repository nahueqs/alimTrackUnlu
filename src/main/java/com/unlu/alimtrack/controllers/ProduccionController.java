package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.services.ProduccionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/producciones")
public class ProduccionController {

  private final ProduccionService produccionService;

  @GetMapping
  public ResponseEntity<List<ProduccionResponseDTO>> getAllProducciones(ProduccionFilterRequestDTO filtros) {

    log.info("Solicitud de búsqueda de producciones recibida: {}", filtros);

    List<ProduccionResponseDTO> producciones = produccionService.findAllByFilters(filtros);

    log.debug("Retornando {} producciones para los filtros: {}", producciones.size(), filtros);

    return ResponseEntity.ok(producciones);
  }

  @GetMapping("/{codigoProduccion}")
  public ResponseEntity<ProduccionResponseDTO> getProduccionByCodigoProduccion(
      @PathVariable String codigoProduccion) {

    return ResponseEntity.ok(produccionService.findByCodigoProduccion(codigoProduccion));
  }


}
