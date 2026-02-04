package com.unlu.alimtrack.DTOS.request.respuestas;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.services.validators.RespuestaValidationService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseRespuestaRequestDTO {

    // Campos comunes para todos los tipos de respuesta
    private String valorTexto;
    private BigDecimal valorNumerico;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime valorFecha;

    private Boolean valorBooleano;

    /**
     * Método para validar usando el servicio de validación
     */
    @JsonIgnore
    public void validate(TipoDatoCampo tipoDato, RespuestaValidationService validationService) {
        validationService.validarRespuesta(
                tipoDato,
                valorTexto,
                valorNumerico,
                valorFecha,
                valorBooleano
        );
    }

    /**
     * Obtiene el valor según el tipo
     */
    @JsonIgnore
    public Object getValor(TipoDatoCampo tipoDato) {
        if (tipoDato == null) return null;

        return switch (tipoDato) {
            case TEXTO -> valorTexto;
            case DECIMAL, ENTERO -> valorNumerico;
            case FECHA, HORA -> valorFecha;
            case BOOLEANO -> valorBooleano;
            default -> null;
        };
    }

    /**
     * Verifica si es una respuesta vacía
     */
    @JsonIgnore
    public boolean esRespuestaVacia() {
        return valorTexto == null &&
                valorNumerico == null &&
                valorFecha == null &&
                valorBooleano == null;
    }

    /**
     * Asigna el valor según el tipo
     */
    public void setValorSegunTipo(TipoDatoCampo tipoDato, Object valor) {
        // Primero limpiar todos los campos
        this.valorTexto = null;
        this.valorNumerico = null;
        this.valorFecha = null;
        this.valorBooleano = null;

        if (tipoDato == null || valor == null) {
            return;
        }

        switch (tipoDato) {
            case TEXTO:
                this.valorTexto = valor.toString();
                break;
            case DECIMAL:
            case ENTERO:
                if (valor instanceof BigDecimal) {
                    this.valorNumerico = (BigDecimal) valor;
                } else if (valor instanceof Number) {
                    this.valorNumerico = new BigDecimal(valor.toString());
                } else {
                    this.valorNumerico = new BigDecimal(valor.toString());
                }
                break;
            case FECHA:
            case HORA:
                if (valor instanceof LocalDateTime) {
                    this.valorFecha = (LocalDateTime) valor;
                } else if (valor instanceof String) {
                    // Parsear string a LocalDateTime según formato
                    this.valorFecha = LocalDateTime.parse((String) valor);
                }
                break;
            case BOOLEANO:
                if (valor instanceof Boolean) {
                    this.valorBooleano = (Boolean) valor;
                } else if (valor instanceof String) {
                    this.valorBooleano = Boolean.parseBoolean((String) valor);
                } else if (valor instanceof Number) {
                    this.valorBooleano = ((Number) valor).intValue() != 0;
                }
                break;
        }
    }
}