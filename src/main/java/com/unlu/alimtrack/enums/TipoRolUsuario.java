package com.unlu.alimtrack.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TipoRolUsuario {
    @JsonProperty("ADMINISTRADOR")
    ADMIN,

    @JsonProperty("OPERADOR")
    OPERADOR
}
