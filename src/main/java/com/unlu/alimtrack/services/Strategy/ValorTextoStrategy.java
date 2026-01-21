package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.stereotype.Component;

@Component
public class ValorTextoStrategy implements ValorCampoStrategy {

    @Override
    public void asignarValor(RespuestaCampoModel respuesta, Object valor) {
        respuesta.setValorTexto(valor != null ? valor.toString() : null);

        respuesta.setValorNumerico(null);
        respuesta.setValorFecha(null);
    }

    @Override
    public TipoDatoCampo getTipo() {
        return TipoDatoCampo.TEXTO;
    }
}
