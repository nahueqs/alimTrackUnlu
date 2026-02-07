package com.unlu.alimtrack.eventos;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProduccionEliminadaEvent extends ApplicationEvent {
    private final String codigoProduccion;

    public ProduccionEliminadaEvent(Object source, String codigoProduccion) {
        super(source);
        this.codigoProduccion = codigoProduccion;
    }
}
