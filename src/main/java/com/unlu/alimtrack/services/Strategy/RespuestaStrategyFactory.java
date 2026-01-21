package com.unlu.alimtrack.services.Strategy;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RespuestaStrategyFactory {

    private final Map<String, ValorCampoStrategy> estrategias;

    // Inyección por nombre del bean
    public RespuestaStrategyFactory(
            ValorTextoStrategy textoStrategy,
            ValorNumericoStrategy numericoStrategy,
            ValorFechaStrategy fechaStrategy,
            ValorBooleanoStrategy booleanoStrategy,
            ValorHoraStrategy horaStrategy
    ) {

        this.estrategias = Map.of(
                TipoDatoCampo.TEXTO.name(), textoStrategy,
                TipoDatoCampo.DECIMAL.name(), numericoStrategy,
                TipoDatoCampo.ENTERO.name(), numericoStrategy,
                TipoDatoCampo.FECHA.name(), fechaStrategy,
                TipoDatoCampo.BOOLEANO.name(), booleanoStrategy,
                TipoDatoCampo.HORA.name(), horaStrategy
        );
    }

    public ValorCampoStrategy getStrategy(TipoDatoCampo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de dato no puede ser nulo");
        }

        ValorCampoStrategy estrategia = estrategias.get(tipo.name());
        if (estrategia == null) {
            throw new IllegalArgumentException("Tipo de dato no soportado: " + tipo);
        }

        return estrategia;
    }
}