package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class RespuestaValidationService {

    private static final int MAX_LONGITUD_TEXTO = 255;
    private static final BigDecimal MAX_VALOR_NUMERICO = new BigDecimal("999999999999.99");
    private static final BigDecimal MIN_VALOR_NUMERICO = new BigDecimal("-999999999999.99");
    private static final DateTimeFormatter FECHA_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter HORA_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Valida una respuesta según su tipo
     */
    public void validarRespuesta(TipoDatoCampo tipo,
                                 String valorTexto,
                                 BigDecimal valorNumerico,
                                 LocalDateTime valorFecha,
                                 Boolean valorBooleano) {

        if (tipo == null) {
            throw new ValidationException("Tipo de dato no especificado");
        }

        // Validar que solo haya un valor no nulo
        validarUnSoloValor(tipo, valorTexto, valorNumerico, valorFecha, valorBooleano);

        // Validar límites según tipo
        validarLimites(tipo, valorTexto, valorNumerico);
    }

    private void validarUnSoloValor(TipoDatoCampo tipo,
                                    String valorTexto,
                                    BigDecimal valorNumerico,
                                    LocalDateTime valorFecha,
                                    Boolean valorBooleano) {

        int valoresNoNulos = 0;

        if (valorTexto != null) valoresNoNulos++;
        if (valorNumerico != null) valoresNoNulos++;
        if (valorFecha != null) valoresNoNulos++;
        if (valorBooleano != null) valoresNoNulos++;

        if (valoresNoNulos > 1) {
            throw new ValidationException("Debe proporcionar solo UN valor");
        }

        // Validar que el valor no nulo corresponda al tipo
        switch (tipo) {
            case TEXTO:
                if (valorNumerico != null || valorFecha != null || valorBooleano != null) {
                    throw new ValidationException("Campo de tipo texto no acepta otros tipos de valores");
                }
                break;
            case DECIMAL:
            case ENTERO:
                if (valorTexto != null || valorFecha != null || valorBooleano != null) {
                    throw new ValidationException("Campo numérico no acepta otros tipos de valores");
                }
                break;
            case FECHA:
            case HORA:
                if (valorTexto != null || valorNumerico != null || valorBooleano != null) {
                    throw new ValidationException("Campo fecha/hora no acepta otros tipos de valores");
                }
                break;
            case BOOLEANO:
                if (valorTexto != null || valorNumerico != null || valorFecha != null) {
                    throw new ValidationException("Campo booleano no acepta otros tipos de valores");
                }
                break;
        }
    }

    private void validarLimites(TipoDatoCampo tipo, String valorTexto, BigDecimal valorNumerico) {
        switch (tipo) {
            case TEXTO:
                if (valorTexto != null && valorTexto.length() > MAX_LONGITUD_TEXTO) {
                    throw new ValidationException(
                            "Texto excede la longitud máxima de " + MAX_LONGITUD_TEXTO + " caracteres"
                    );
                }
                break;
            case DECIMAL:
                if (valorNumerico != null &&
                        (valorNumerico.compareTo(MIN_VALOR_NUMERICO) < 0 ||
                                valorNumerico.compareTo(MAX_VALOR_NUMERICO) > 0)) {
                    throw new ValidationException(
                            "Valor decimal fuera de rango (" + MIN_VALOR_NUMERICO +
                                    " a " + MAX_VALOR_NUMERICO + ")"
                    );
                }
                break;
            case ENTERO:
                if (valorNumerico != null) {
                    if (valorNumerico.stripTrailingZeros().scale() > 0) {
                        throw new ValidationException("El valor debe ser un número entero (sin decimales)");
                    }
                    if (valorNumerico.compareTo(MIN_VALOR_NUMERICO) < 0 ||
                            valorNumerico.compareTo(MAX_VALOR_NUMERICO) > 0) {
                        throw new ValidationException(
                                "Valor entero fuera de rango (" + MIN_VALOR_NUMERICO +
                                        " a " + MAX_VALOR_NUMERICO + ")"
                        );
                    }
                }
                break;
            default:
                // FECHA, HORA, BOOLEANO no tienen límites específicos
                break;
        }
    }

    /**
     * Convierte un String a BigDecimal validándolo
     */
    public BigDecimal validarYConvertirDecimal(String valor) {
        if (valor == null) return null;

        try {
            BigDecimal numero = new BigDecimal(valor);

            if (numero.compareTo(MIN_VALOR_NUMERICO) < 0 ||
                    numero.compareTo(MAX_VALOR_NUMERICO) > 0) {
                throw new ValidationException(
                        "Valor fuera de rango (" + MIN_VALOR_NUMERICO +
                                " a " + MAX_VALOR_NUMERICO + ")"
                );
            }

            return numero;
        } catch (NumberFormatException e) {
            throw new ValidationException("Valor decimal inválido: " + valor);
        }
    }

    /**
     * Convierte un String a entero validándolo
     */
    public BigDecimal validarYConvertirEntero(String valor) {
        if (valor == null) return null;

        BigDecimal numero = validarYConvertirDecimal(valor);

        if (numero != null && numero.stripTrailingZeros().scale() > 0) {
            throw new ValidationException("El valor debe ser un número entero (sin decimales)");
        }

        return numero;
    }

    /**
     * Valida y convierte fecha
     */
    public LocalDateTime validarYConvertirFecha(String valor) {
        if (valor == null) return null;

        try {
            return LocalDateTime.parse(valor, FECHA_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(valor, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e2) {
                throw new ValidationException("Formato de fecha inválido. Use: " +
                        FECHA_FORMATTER.toString() + " o ISO 8601");
            }
        }
    }

    /**
     * Valida booleano
     */
    public Boolean validarYConvertirBooleano(String valor) {
        if (valor == null) return null;

        String lowerValor = valor.toLowerCase().trim();

        if ("true".equals(lowerValor) || "1".equals(lowerValor) ||
                "si".equals(lowerValor) || "yes".equals(lowerValor) ||
                "verdadero".equals(lowerValor) || "on".equals(lowerValor)) {
            return true;
        } else if ("false".equals(lowerValor) || "0".equals(lowerValor) ||
                "no".equals(lowerValor) || "off".equals(lowerValor) ||
                "falso".equals(lowerValor)) {
            return false;
        } else {
            throw new ValidationException(
                    "Valor booleano inválido: " + valor +
                            ". Use: true/false, 1/0, si/no, yes/no"
            );
        }
    }
}