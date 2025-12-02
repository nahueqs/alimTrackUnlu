package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioServiceImpl userService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDTO request) {
        log.info("Solicitud de login recibida para el usuario: {}", request.email());
        AuthResponse response = authService.login(request);
        log.debug("Login exitoso para el usuario: {}. Token generado.", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Solicitud de registro recibida para el usuario: {}", request.email());
        AuthResponse response = authService.register(request);
        log.info("Registro exitoso para el usuario: {}. Token generado.", request.email());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        log.info("Solicitud para obtener el usuario actual: {}", username);
        UsuarioResponseDTO user = userService.getUsuarioByEmail(username);
        log.debug("Retornando información para el usuario: {}", user.email());
        return ResponseEntity.ok(user);
    }
}
