package com.unlu.alimtrack.models;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class RespuestaBaseModel {

    @Column(name = "valor_texto", columnDefinition = "TEXT")
    protected String valorTexto;

    @Column(name = "valor_numerico", precision = 18, scale = 5)
    protected BigDecimal valorNumerico;

    @Column(name = "valor_fecha", columnDefinition = "DATETIME")
    protected LocalDateTime valorFecha;

    @Column(name = "timestamp")
    protected LocalDateTime timestamp;

    /**
     * Limpia todos los valores de la respuesta
     */
    public void limpiarValores() {
        this.valorTexto = null;
        this.valorNumerico = null;
        this.valorFecha = null;
    }

    /**
     * Obtiene el valor según el tipo de dato
     */
    public Object getValor(TipoDatoCampo tipo) {
        if (tipo == null) return null;

        return switch (tipo) {
            case TEXTO -> valorTexto;
            case DECIMAL, ENTERO -> valorNumerico;
            case FECHA, HORA -> valorFecha;
            case BOOLEANO -> valorNumerico != null ?
                    valorNumerico.compareTo(BigDecimal.ONE) == 0 :
                    null;
            default -> null;
        };
    }

    /**
     * Verifica si la respuesta está vacía
     */
    public boolean esRespuestaVacia() {
        return valorTexto == null &&
                valorNumerico == null &&
                valorFecha == null;
    }
}