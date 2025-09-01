package com.unlu.alimtrack.services.validators;

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

}
