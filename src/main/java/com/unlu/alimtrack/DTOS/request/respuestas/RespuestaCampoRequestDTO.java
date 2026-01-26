package com.unlu.alimtrack.DTOS.request.respuestas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaCampoRequestDTO extends BaseRespuestaRequestDTO {

    @JsonProperty("idCampo")
    @NotNull(message = "El ID del campo es obligatorio")
    private Long idCampo;

    @NotNull(message = "El email del creador es obligatorio")
    private String emailCreador;



}