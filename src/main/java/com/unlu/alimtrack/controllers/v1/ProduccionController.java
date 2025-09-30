package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.services.ProduccionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/producciones")
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

  @PostMapping("/{codigoProduccion}")
  public ResponseEntity<ProduccionResponseDTO> saveProduccion(@PathVariable String codigoProduccion,
      @Valid @RequestBody ProduccionCreateDTO createDTO) {
    ProduccionResponseDTO created = produccionService.saveProduccion(codigoProduccion, createDTO);
    return ResponseEntity.created(URI.create("/api/v1/producciones/" + created.codigoProduccion())).body(created);
  }

//  @GetMapping("/{idProduccion}/estructura")
//  public ResponseEntity<EstructuraProduccionDTO> getEstructuraCompleta(
//      @PathVariable Long idProduccion) {
//    EstructuraProduccionDTO estructura = produccionService.getEstructuraCompleta(idProduccion);
//    return ResponseEntity.ok(estructura);


}
