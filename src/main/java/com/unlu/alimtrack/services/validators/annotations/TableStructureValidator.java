package com.unlu.alimtrack.services.validators.annotations;

import com.unlu.alimtrack.DTOS.create.secciones.TablaCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TableStructureValidator implements ConstraintValidator<ValidTableStructure, TablaCreateDTO> {

    @Override
    public boolean isValid(TablaCreateDTO tabla, ConstraintValidatorContext context) {
        if (tabla == null) {
            return true; // Dejar que @NotNull lo maneje si es necesario
        }

        boolean valid = true;

        // Validar columnas
        if (tabla.columnas() == null || tabla.columnas().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La tabla debe tener al menos una columna")
                    .addPropertyNode("columnas")
                    .addConstraintViolation();
            valid = false;
        }

        // Validar filas (opcional, pero una tabla sin filas no captura datos predefinidos, aunque podría ser dinámica)
        // Por ahora exigiremos al menos una fila para mantener la estructura coherente
        if (tabla.filas() == null || tabla.filas().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La tabla debe tener al menos una fila")
                    .addPropertyNode("filas")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
