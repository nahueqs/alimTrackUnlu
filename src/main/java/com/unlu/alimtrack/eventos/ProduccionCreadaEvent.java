// ProduccionCreadaEvent.java
package com.unlu.alimtrack.eventos;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class ProduccionCreadaEvent extends ApplicationEvent {
    private final String codigoProduccion;
    private final String codigoVersionReceta;
    private final String lote;
    private final LocalDateTime fechaInicio;
    private final LocalDateTime fechaFin;
    private final String tipoEvento = "PRODUCCION_CREADA";

    public ProduccionCreadaEvent(Object source, String codigoProduccion, String codigoVersionReceta, String lote, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        super(source);
        this.codigoProduccion = codigoProduccion;
        this.codigoVersionReceta = codigoVersionReceta;
        this.lote = lote;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}