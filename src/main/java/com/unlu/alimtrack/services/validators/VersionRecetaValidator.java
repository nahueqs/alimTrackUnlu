package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.dtos.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.VersionRecetaModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VersionRecetaValidator {

  public void validarVersionReceta(VersionRecetaModel model, String codigoVersionReceta) {
    if (model == null) {
      throw new RecursoNoEncontradoException(
          "No existe ninguna version con el codigo " + codigoVersionReceta);
    }
  }

  public void validarVersionRecetaList(List<VersionRecetaModel> list) {
    if (list == null) {
      throw new RecursoNoEncontradoException("No existen versiones para la receta");
    }
  }

  public void validateModification(VersionRecetaModifyDTO modificacion) {
    if (modificacion.nombre() != null) {
      validateNombre(modificacion.nombre());
    }
    if (modificacion.descripcion() != null) {
      validateDescripcion(modificacion.descripcion());
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
