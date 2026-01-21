package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;

public interface ValorCampoStrategy {

    void asignarValor(RespuestaCampoModel respuesta, Object valor);

    TipoDatoCampo getTipo();
}