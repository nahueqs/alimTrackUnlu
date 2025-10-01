package com.unlu.alimtrack.enums;

public enum TipoEstadoProduccion {
  EN_CURSO,
  FINALIZADA;

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