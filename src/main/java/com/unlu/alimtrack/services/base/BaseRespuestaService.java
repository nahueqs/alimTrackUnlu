package com.unlu.alimtrack.services.base;

import com.unlu.alimtrack.DTOS.request.respuestas.BaseRespuestaRequestDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.ValidationException;
import com.unlu.alimtrack.models.RespuestaBaseModel;
import com.unlu.alimtrack.services.validators.RespuestaValidationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class BaseRespuestaService<T extends RespuestaBaseModel> {

    protected final RespuestaValidationService validationService;

    // Constructor protegido - LAS CLASES HIJAS DEBEN LLAMARLO
    protected BaseRespuestaService(RespuestaValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Procesa y valida una respuesta
     */
    protected void procesarRespuesta(T respuesta,
                                     BaseRespuestaRequestDTO request,
                                     TipoDatoCampo tipoDato) {

        // Validar la respuesta
        request.validate(tipoDato, validationService);

        // Asignar valores según tipo
        asignarValoresRespuesta(respuesta, request, tipoDato);

        // Actualizar timestamp
        respuesta.setTimestamp(LocalDateTime.now());
    }

    /**
     * Asigna valores a la respuesta según el tipo
     */
    protected void asignarValoresRespuesta(T respuesta,
                                           BaseRespuestaRequestDTO request,
                                           TipoDatoCampo tipoDato) {

        // Limpiar todos los campos primero usando el método de la clase base
        respuesta.limpiarValores();

        // Asignar según tipo
        switch (tipoDato) {
            case TEXTO:
                respuesta.setValorTexto(request.getValorTexto());
                break;
            case DECIMAL:
            case ENTERO:
                respuesta.setValorNumerico(request.getValorNumerico());
                break;
            case FECHA:
            case HORA:
                respuesta.setValorFecha(request.getValorFecha());
                break;
            case BOOLEANO:
                // Para booleano, usar valorNumerico (1/0)
                if (request.getValorBooleano() != null) {
                    respuesta.setValorNumerico(
                            request.getValorBooleano() ? BigDecimal.ONE : BigDecimal.ZERO
                    );
                }
                break;
            default:
                throw new ValidationException("Tipo de dato no soportado: " + tipoDato);
        }
    }

    /**
     * Obtiene el valor de una respuesta según su tipo
     */
    protected Object obtenerValorRespuesta(T respuesta, TipoDatoCampo tipoDato) {
        if (respuesta == null) return null;

        // Usar el método getValor() de la clase base
        return respuesta.getValor(tipoDato);
    }
}