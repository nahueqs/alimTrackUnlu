package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.VersionRecetaModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VersionRecetaValidator {

    public void validarVersionReceta(VersionRecetaModel model, String codigoVersionReceta) {
        if (model == null) {
            throw new RecursoNoEncontradoException("No existe ninguna version con el codigo " + codigoVersionReceta);
        }
    }

}
