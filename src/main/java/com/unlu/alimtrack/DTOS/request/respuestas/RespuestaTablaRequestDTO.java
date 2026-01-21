package com.unlu.alimtrack.DTOS.request.respuestas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaTablaRequestDTO extends BaseRespuestaRequestDTO {

    @NotNull(message = "El email del creador es obligatorio")
    private String emailCreador;

    // Opcional: puedes agregar estos campos si quieres validar en el DTO
    private Long idTabla;
    private Long idFila;
    private Long idColumna;

    /**
     * Valida coherencia de IDs si se proporcionan
     */
    @JsonIgnore
    public void validarCoherenciaIds(Long idTablaPath, Long idFilaPath, Long idColumnaPath) {
        if (this.idTabla != null && !this.idTabla.equals(idTablaPath)) {
            throw new IllegalArgumentException("ID de tabla inconsistente");
        }
        if (this.idFila != null && !this.idFila.equals(idFilaPath)) {
            throw new IllegalArgumentException("ID de fila inconsistente");
        }
        if (this.idColumna != null && !this.idColumna.equals(idColumnaPath)) {
            throw new IllegalArgumentException("ID de columna inconsistente");
        }
    }
}