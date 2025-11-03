package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.RecetaModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecetaValidator {

    public void validateCodigoReceta(String codigoReceta) {
        if (codigoReceta == null || codigoReceta.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de receta no puede ser nulo o vacío");
        }
    }

    public void validateModel(RecetaModel model, String codigo) {
        if (model == null) {
            throw new RecursoNoEncontradoException("Receta no encontrada con ID: " + codigo);
        }
    }

    public void validateModelList(List<RecetaModel> lista) {
        if (lista.isEmpty()) {
            throw new RecursoNoEncontradoException("Recetas no encontradas");
        }
    }

    public void validateDatosModification(RecetaModifyDTO receta) {
        if (receta.nombre() != null) {
            validateNombre(receta.nombre());
        }
        if (receta.descripcion() != null) {
            validateDescripcion(receta.descripcion());
        }
    }

    public void validateNombre(String nombre) {
        if (nombre.isBlank()) {
            throw new ModificacionInvalidaException("El nombre de la receta no puede estar vacío");
        }
        if (nombre.length() < 2 || nombre.length() > 100) {
            throw new ModificacionInvalidaException(
                    "El nombre  de la receta debe tener 2-100 caracteres");
        }
    }

    public void validateDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 255) {
            throw new ModificacionInvalidaException(
                    "La descripción de la receta no puede exceder 255 caracteres");
        }
    }

}
