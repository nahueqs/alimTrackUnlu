package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValorBooleanoStrategy implements ValorCampoStrategy {

    @Override
    public void asignarValor(RespuestaCampoModel respuesta, Object valor) {
        BigDecimal valorBooleano = convertirBooleanoANumero(valor);

        respuesta.setValorNumerico(valorBooleano);

        respuesta.setValorTexto(null);
        respuesta.setValorFecha(null);
    }

    private BigDecimal convertirBooleanoANumero(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof Boolean) {
            return ((Boolean) valor) ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (valor instanceof String) {
            String str = ((String) valor).toLowerCase().trim();
            if ("true".equals(str) || "1".equals(str) || "si".equals(str) || "yes".equals(str)) {
                return BigDecimal.ONE;
            } else if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
                return BigDecimal.ZERO;
            } else {
                throw new IllegalArgumentException("Valor booleano inválido: " + valor);
            }
        } else if (valor instanceof Number) {
            int num = ((Number) valor).intValue();
            return num == 0 ? BigDecimal.ZERO : BigDecimal.ONE;
        } else {
            throw new IllegalArgumentException("Valor booleano inválido: " + valor);
        }
    }

    @Override
    public TipoDatoCampo getTipo() {
        return TipoDatoCampo.BOOLEANO;
    }
}