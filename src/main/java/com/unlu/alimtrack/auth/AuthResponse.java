package com.unlu.alimtrack.auth;


import lombok.Builder;

@Builder
public record AuthResponse(
        String token
) {
}
