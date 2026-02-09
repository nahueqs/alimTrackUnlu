package com.unlu.alimtrack.services.validators.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TableStructureValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTableStructure {
    String message() default "La estructura de la tabla es inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
