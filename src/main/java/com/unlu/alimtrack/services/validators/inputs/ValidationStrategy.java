package com.unlu.alimtrack.services.validators.inputs;

import org.springframework.stereotype.Component;

@Component
public interface ValidationStrategy {
    boolean validate(String data);

    String getErrorMessage();
}
