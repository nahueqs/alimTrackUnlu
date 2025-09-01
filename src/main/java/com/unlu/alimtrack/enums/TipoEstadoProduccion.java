package com.unlu.alimtrack.enums;

public enum TipoEstadoProduccion {
  EN_CURSO,
  FINALIZADA;

  public String getValorBaseDatos() {
    return name().toLowerCase(); // Devuelve "en_curso", "finalizada"
  }
}