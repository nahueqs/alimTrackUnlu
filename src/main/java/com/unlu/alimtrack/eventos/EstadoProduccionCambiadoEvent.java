// EstadoProduccionCambiadoEvent.java
package com.unlu.alimtrack.eventos;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class EstadoProduccionCambiadoEvent extends ApplicationEvent {
    private final String codigoProduccion;
    private final TipoEstadoProduccion estado;
    private final LocalDateTime fechaFin;
    private final String tipoEvento = "ESTADO_CAMBIADO";

    public EstadoProduccionCambiadoEvent(Object source, String codigoProduccion, TipoEstadoProduccion estado, LocalDateTime fechaFin) {
        super(source);
        this.codigoProduccion = codigoProduccion;
        this.estado = estado;
        this.fechaFin = fechaFin;
    }
}