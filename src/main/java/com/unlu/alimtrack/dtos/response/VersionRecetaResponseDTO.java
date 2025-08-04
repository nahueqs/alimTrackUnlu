package com.unlu.alimtrack.dtos.response;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public record VersionRecetaResponseDTO (@NotNull Long id, Instant fechaCreacion, Long idCreadoPor) {

}
