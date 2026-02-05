package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.services.RecetaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recetas")
@Tag(name = "Recetas", description = "Gestión de recetas base")
public class RecetaController {

    private final RecetaService recetaService;

    @Operation(summary = "Listar recetas", description = "Obtiene todas las recetas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de recetas recuperada exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<RecetaMetadataResponseDTO>> getAllRecetas() {
        log.info("Solicitud para obtener todas las recetas");
        List<RecetaMetadataResponseDTO> recetas = recetaService.findAllRecetas();
        log.debug("Retornando {} recetas", recetas.size());
        return ResponseEntity.ok(recetas);
    }

    @Operation(summary = "Obtener receta por código", description = "Devuelve la información de una receta específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta encontrada",
                    content = @Content(schema = @Schema(implementation = RecetaMetadataResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{codigoReceta}")
    public ResponseEntity<RecetaMetadataResponseDTO> getReceta(@PathVariable String codigoReceta) {
        log.info("Solicitud para obtener la receta con código: {}", codigoReceta);
        RecetaMetadataResponseDTO receta = recetaService.findReceta(codigoReceta);
        log.debug("Retornando receta: {}", receta.codigoReceta());
        return ResponseEntity.ok(receta);
    }

    @Operation(summary = "Actualizar receta", description = "Modifica los datos básicos de una receta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receta actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = RecetaMetadataResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @PutMapping("/{codigoReceta}")
    public ResponseEntity<RecetaMetadataResponseDTO> updateReceta(@PathVariable String codigoReceta,
                                                                  @Valid @RequestBody RecetaModifyDTO receta) {
        log.info("Solicitud para actualizar la receta con código: {}", codigoReceta);
        RecetaMetadataResponseDTO updated = recetaService.updateReceta(codigoReceta, receta);
        log.info("Receta {} actualizada exitosamente", updated.codigoReceta());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Crear receta", description = "Registra una nueva receta en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Receta creada exitosamente",
                    content = @Content(schema = @Schema(implementation = RecetaMetadataResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El código de receta ya existe"),
            @ApiResponse(responseCode = "400", description = "Datos de creación inválidos")
    })
    @PostMapping()
    public ResponseEntity<RecetaMetadataResponseDTO> addReceta(
            @Valid @RequestBody RecetaCreateDTO receta) {
        log.info("Solicitud para crear una nueva receta con código: {}");
        RecetaMetadataResponseDTO created = recetaService.addReceta(receta);
        log.info("Receta creada exitosamente: {}", created.codigoReceta());
        return ResponseEntity.created(URI.create("/api/v1/recetas/" + created.codigoReceta()))
                .body(created);
    }

    @Operation(summary = "Eliminar receta", description = "Borra una receta si no tiene versiones asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Receta eliminada exitosamente"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar porque tiene versiones asociadas"),
            @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @DeleteMapping("/{codigoReceta}")
    public ResponseEntity<Void> deleteReceta(@PathVariable String codigoReceta) {
        log.info("Solicitud para eliminar la receta con código: {}", codigoReceta);
        recetaService.deleteReceta(codigoReceta);
        log.info("Receta {} eliminada exitosamente", codigoReceta);
        return ResponseEntity.noContent().build();
    }
}
