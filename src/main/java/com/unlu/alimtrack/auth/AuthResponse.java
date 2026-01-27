package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken,
        UsuarioResponseDTO user
) {
}
