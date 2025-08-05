package com.unlu.alimtrack.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String mensaje) {
        super(mensaje);
    }
}

