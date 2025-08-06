package com.unlu.alimtrack.exception;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String mensaje) {
        super(mensaje);
    }
}

