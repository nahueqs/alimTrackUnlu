package com.unlu.alimtrack.auth;


import com.unlu.alimtrack.DTOS.response.UsuarioResponseDTO;
import lombok.Builder;

@Builder
public record AuthResponse(
        String token,
        UsuarioResponseDTO user
) {
}
