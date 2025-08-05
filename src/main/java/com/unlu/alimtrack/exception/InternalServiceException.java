package com.unlu.alimtrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InternalServiceException extends RuntimeException {
    public InternalServiceException(String mensaje) {
        super(mensaje);
    }
}

