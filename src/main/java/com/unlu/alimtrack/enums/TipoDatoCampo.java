// TipoDatoCampo.java (agregar estos métodos)
package com.unlu.alimtrack.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoDatoCampo {
    DECIMAL("decimal"),
    ENTERO("entero"),
    FECHA("fecha"),
    HORA("hora"),
    BOOLEANO("booleano"),
    TEXTO("texto");

    private final String value;

    TipoDatoCampo(String value) {
        this.value = value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TipoDatoCampo fromString(String value) {
        if (value == null) return null;

        String trimmedValue = value.trim();
        for (TipoDatoCampo tipo : values()) {
            if (tipo.value.equalsIgnoreCase(trimmedValue)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Valor de tipo de campo no válido: '" + value +
                "'. Los valores válidos son: " + getValidValues());
    }

    private static String getValidValues() {
        StringBuilder sb = new StringBuilder();
        for (TipoDatoCampo tipo : values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("'").append(tipo.value).append("'");
        }
        return sb.toString();
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    // ===== MÉTODOS NUEVOS =====

    public boolean esNumerico() {
        return this == DECIMAL || this == ENTERO;
    }

    public boolean esFechaOHora() {
        return this == FECHA || this == HORA;
    }

    public boolean esTexto() {
        return this == TEXTO;
    }

    public boolean esBooleano() {
        return this == BOOLEANO;
    }

    // Para determinar qué tipo de valor esperar
    public Class<?> getTipoJavaEsperado() {
        return switch (this) {
            case DECIMAL -> java.math.BigDecimal.class;
            case ENTERO -> Integer.class;
            case FECHA, HORA, TEXTO -> String.class;
            case BOOLEANO -> Boolean.class;
            default -> Object.class;
        };
    }
}