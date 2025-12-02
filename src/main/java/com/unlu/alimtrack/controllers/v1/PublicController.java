package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.MetadataProduccionPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestasProduccionPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.services.PublicRequestsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final PublicRequestsService publicRequestService;

    @GetMapping("/producciones/{codigoProduccion}/estado-actual")
    public ResponseEntity<RespuestasProduccionPublicResponseDTO> getRespuestasPublico(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud pública para obtener el estado actual de la producción: {}", codigoProduccion);
        RespuestasProduccionPublicResponseDTO estado = publicRequestService.getEstadoActualProduccionPublico(codigoProduccion);

        log.debug("Retornando estado actual público para la producción {}", codigoProduccion);
        return ResponseEntity.ok(estado);
    }

    @GetMapping("/recetas/versiones-receta/{codigoVersionReceta}/estructura")
    public ResponseEntity<VersionEstructuraPublicResponseDTO> getEstructuraVersion(@PathVariable String codigoVersionReceta) {

        log.info("Solicitud pública para obtener la estructura completa de la versión de receta: {}", codigoVersionReceta);
        VersionEstructuraPublicResponseDTO estructura = publicRequestService.getEstructuraVersionPublica(codigoVersionReceta);
        log.debug("Retornando estructura completa para la versión {}", codigoVersionReceta);
        return ResponseEntity.ok(estructura);
    }

    @GetMapping("/producciones")
    public ResponseEntity<List<MetadataProduccionPublicaResponseDTO>> getAllProduccionesMetadataPublico() {
        log.info("Solicitud pública para obtener todas las producciones");
        List<MetadataProduccionPublicaResponseDTO> producciones = publicRequestService.getAllProduccionesMetadataPublico();
        log.debug("Retornando {} producciones públicas", producciones.size());
        return ResponseEntity.ok(producciones);
    }

    @GetMapping("/producciones/{codigoProduccion}")
    public ResponseEntity<EstadoProduccionPublicoResponseDTO> getProduccionPublic(
            @PathVariable String codigoProduccion) {
        log.info("Solicitud pública para obtener la información pública de la producción: {}", codigoProduccion);
        EstadoProduccionPublicoResponseDTO produccion = publicRequestService.getProduccionPublic(codigoProduccion);
        log.debug("Retornando información pública para la producción {}", codigoProduccion);
        return ResponseEntity.ok(produccion);
    }


}
