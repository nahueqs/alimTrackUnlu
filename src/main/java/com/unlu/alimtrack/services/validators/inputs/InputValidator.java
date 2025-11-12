package com.unlu.alimtrack.services.validators.inputs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InputValidator {
    private final List<ValidationStrategy> strategies;
    private final List<String> errorMessages;


    public void addValidationStrategy(ValidationStrategy strategy) {
        strategies.add(strategy);
    }

    public boolean validate(String data) {
        errorMessages.clear();
        boolean isValid = true;

        for (ValidationStrategy strategy : strategies) {
            if (!strategy.validate(data)) {
                errorMessages.add(strategy.getErrorMessage());
                isValid = false;
            }
        }
        return isValid;
    }

    public boolean validateWithStrategy(String data, ValidationStrategy strategy) {
        errorMessages.clear();
        if (!strategy.validate(data)) {
            errorMessages.add(strategy.getErrorMessage());
            return false;
        }
        return true;
    }


    public List<String> getErrorMessages() {
        return new ArrayList<>(errorMessages);
    }

    public void clearStrategies() {
        strategies.clear();
        errorMessages.clear();
    }

}

