// RespuestaTablaGuardadaEvent.java
package com.unlu.alimtrack.eventos;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RespuestaTablaGuardadaEvent extends ApplicationEvent {
    private final String codigoProduccion;
    private final Long idTabla;
    private final Long idFila;
    private final Long idColumna;
    private final String valor;
    private final String tipoEvento = "CELDA_TABLA_GUARDADA";

    public RespuestaTablaGuardadaEvent(Object source, String codigoProduccion, Long idTabla, Long idFila, Long idColumna, String valor) {
        super(source);
        this.codigoProduccion = codigoProduccion;
        this.idTabla = idTabla;
        this.idFila = idFila;
        this.idColumna = idColumna;
        this.valor = valor;
    }
}