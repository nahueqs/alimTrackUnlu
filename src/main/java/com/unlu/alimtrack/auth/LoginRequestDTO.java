package com.unlu.alimtrack.auth;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull String email,
        @NotNull String password
) {

}