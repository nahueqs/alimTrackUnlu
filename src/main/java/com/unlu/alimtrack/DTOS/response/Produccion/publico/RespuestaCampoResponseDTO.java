// RespuestaCampoResponseDTO.java
package com.unlu.alimtrack.DTOS.response.Produccion.publico;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespuestaCampoResponseDTO {

    private Long idRespuesta;
    private Long idCampo;

    private String valor;
    private TipoDatoCampo tipo;

    // Metadatos
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String creadoPor;

    // ===== MÉTODOS PARA EL FRONTEND =====

    public boolean tieneValor() {
        return valor != null && !valor.trim().isEmpty();
    }

    public String getValorFormateado() {
        if (!tieneValor()) return "";

        if (tipo == null) return valor;

        try {
            switch (tipo) {
                case DECIMAL:
                    // Formatear decimal sin ceros innecesarios
                    java.math.BigDecimal bd = new java.math.BigDecimal(valor);
                    return bd.stripTrailingZeros().toPlainString();

                case ENTERO:
                    // Asegurar que sea entero (sin decimales)
                    Integer entero = Integer.parseInt(valor);
                    return entero.toString();

                case FECHA:
                    // Intentar formatear fecha
                    try {
                        LocalDateTime fecha = LocalDateTime.parse(valor);
                        return fecha.format(java.time.format.DateTimeFormatter
                                .ofPattern("dd/MM/yyyy"));
                    } catch (Exception e) {
                        return valor; // Mantener formato original
                    }

                case HORA:
                    // Formatear hora
                    try {
                        LocalDateTime hora = LocalDateTime.parse(valor);
                        return hora.format(java.time.format.DateTimeFormatter
                                .ofPattern("HH:mm"));
                    } catch (Exception e) {
                        return valor;
                    }

                case BOOLEANO:
                    // Convertir a Sí/No
                    Boolean bool = Boolean.parseBoolean(valor);
                    return bool ? "Sí" : "No";

                case TEXTO:
                default:
                    return valor;
            }
        } catch (Exception e) {
            // Si hay error al formatear, devolver el valor original
            return valor;
        }
    }

    // Método para obtener el valor en su tipo original (opcional)
    public Object getValorOriginal() {
        if (!tieneValor() || tipo == null) return null;

        try {
            switch (tipo) {
                case DECIMAL:
                    return new java.math.BigDecimal(valor);
                case ENTERO:
                    return Integer.valueOf(valor);
                case FECHA:
                case HORA:
                    return LocalDateTime.parse(valor);
                case BOOLEANO:
                    return Boolean.valueOf(valor);
                case TEXTO:
                default:
                    return valor;
            }
        } catch (Exception e) {
            return valor; // Fallback a String
        }
    }
}