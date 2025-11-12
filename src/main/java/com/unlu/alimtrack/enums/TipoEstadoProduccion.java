package com.unlu.alimtrack.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TipoEstadoProduccion {
    @JsonProperty("EN_PROCESO")
    EN_PROCESO,
    @JsonProperty("FINALIZADA")
    FINALIZADA,
    @JsonProperty("CANCELADA")
    CANCELADA;

//  public String getValorBaseDatos() {
//    return name().toUpperCase();
//  }

    public static TipoEstadoProduccion fromString(String valor) {
        if (valor == null) {
            return null;
        }
        return TipoEstadoProduccion.valueOf(valor.toUpperCase());
    }


}