package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValorNumericoStrategy implements ValorCampoStrategy {

    @Override
    public void asignarValor(RespuestaCampoModel respuesta, Object valor) {
        BigDecimal numero;

        if (valor instanceof Integer) {
            numero = new BigDecimal((Integer) valor);
        } else if (valor instanceof Long) {
            numero = new BigDecimal((Long) valor);
        } else if (valor instanceof BigDecimal) {
            numero = (BigDecimal) valor;
        } else if (valor != null) {
            try {
                numero = new BigDecimal(valor.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Valor numérico inválido: " + valor);
            }
        } else {
            numero = null;
        }

        respuesta.setValorNumerico(numero);
        respuesta.setValorTexto(null);
        respuesta.setValorFecha(null);
    }

    @Override
    public TipoDatoCampo getTipo() {
        return TipoDatoCampo.DECIMAL;
    }
}