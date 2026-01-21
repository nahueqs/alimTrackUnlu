package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.stereotype.Component;

@Component
public class ValorFechaStrategy implements ValorCampoStrategy {

    @Override
    public void asignarValor(RespuestaCampoModel respuesta, Object valor) {
        // Para fecha, almacenar como LocalDateTime en valorFecha
        if (valor instanceof java.time.LocalDateTime) {
            respuesta.setValorFecha((java.time.LocalDateTime) valor);
        } else if (valor != null) {
            try {
                // Intentar parsear string a LocalDateTime
                respuesta.setValorFecha(java.time.LocalDateTime.parse(valor.toString()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Valor fecha inválido: " + valor);
            }
        } else {
            respuesta.setValorFecha(null);
        }

        // Limpiar otros campos
        respuesta.setValorTexto(null);
        respuesta.setValorNumerico(null);
    }

    @Override
    public TipoDatoCampo getTipo() {
        return TipoDatoCampo.FECHA;
    }
}