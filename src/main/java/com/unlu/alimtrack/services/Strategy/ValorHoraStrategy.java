package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.springframework.stereotype.Component;

@Component
public class ValorHoraStrategy implements ValorCampoStrategy {

    @Override
    public void asignarValor(RespuestaCampoModel respuesta, Object valor) {
        // Para hora, también usar valorFecha o crear campo separado
        // Si usas valorFecha para hora también
        if (valor instanceof java.time.LocalDateTime) {
            respuesta.setValorFecha((java.time.LocalDateTime) valor);
        } else if (valor instanceof java.time.LocalTime) {
            // Convertir LocalTime a LocalDateTime (con fecha actual)
            java.time.LocalTime time = (java.time.LocalTime) valor;
            respuesta.setValorFecha(java.time.LocalDateTime.now().with(time));
        } else if (valor != null) {
            try {
                // Intentar parsear string
                respuesta.setValorFecha(java.time.LocalDateTime.parse(valor.toString()));
            } catch (Exception e) {
                throw new IllegalArgumentException("Valor hora inválido: " + valor);
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
        return TipoDatoCampo.HORA;
    }
}