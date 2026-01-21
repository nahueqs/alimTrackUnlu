// MetadataProduccionActualizadaEvent.java
package com.unlu.alimtrack.eventos;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MetadataProduccionActualizadaEvent extends ApplicationEvent {
    private final String codigoProduccion;
    private final String lote;
    private final String encargado;
    private final String observaciones;
    private final String tipoEvento = "METADATA_ACTUALIZADA";

    public MetadataProduccionActualizadaEvent(Object source, String codigoProduccion, String lote, String encargado, String observaciones) {
        super(source);
        this.codigoProduccion = codigoProduccion;
        this.lote = lote;
        this.encargado = encargado;
        this.observaciones = observaciones;
    }
}