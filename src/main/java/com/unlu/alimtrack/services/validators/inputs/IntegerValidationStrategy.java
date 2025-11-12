package com.unlu.alimtrack.services.validators.inputs;

public class IntegerValidationStrategy implements ValidationStrategy {
    @Override
    public boolean validate(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "";
    }
}
