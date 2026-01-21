// RespuestaCampoGuardadaEvent.java
package com.unlu.alimtrack.eventos;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RespuestaCampoGuardadaEvent extends ApplicationEvent {
    private final String codigoProduccion;
    private final Long idCampo;
    private final Object valor;
    private final String tipoEvento = "CAMPO_GUARDADO";

    public RespuestaCampoGuardadaEvent(Object source, String codigoProduccion, Long idCampo, Object valor) {
        super(source);
        this.codigoProduccion = codigoProduccion;
        this.idCampo = idCampo;
        this.valor = valor;
    }
}