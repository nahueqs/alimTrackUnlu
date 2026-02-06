package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para login, registro y gestión de tokens")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService userService;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens de acceso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "403", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDTO request) {
        log.info("Solicitud de login recibida para el usuario: {}", request.email());
        AuthResponse response = authService.login(request);
        log.debug("Login exitoso para el usuario: {}. Token generado.", request.email());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro exitoso",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos")
    })
    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Solicitud de registro recibida para el usuario: {}", request.email());
        AuthResponse response = authService.register(request);
        log.info("Registro exitoso para el usuario: {}. Token generado.", request.email());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refrescar token", description = "Renueva el token de acceso usando un refresh token válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Refresh token inválido o expirado")
    })
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("token refreshed");
        authService.refreshToken(request, response);
    }

    @Operation(summary = "Obtener usuario actual", description = "Devuelve la información del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario recuperada",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        log.debug("=== /me endpoint called ===");
        log.debug("Authentication object: {}", authentication);
        log.debug("Authentication class: {}", authentication != null ? authentication.getClass().getName() : "null");
        log.debug("Is authenticated: {}", authentication != null && authentication.isAuthenticated());
        log.debug("Is anonymous: {}", authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);

        // Verificar si el usuario está autenticado
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {

            log.info("Usuario no autenticado intentando acceder a /me");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 403);
            errorResponse.put("message", "No autenticado");
            errorResponse.put("path", "/api/v1/auth/me");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

        try {
            String username = authentication.getName();
            log.info("Solicitud para obtener el usuario actual: {}", username);

            UsuarioResponseDTO user = userService.getUsuarioByEmail(username);
            log.debug("Retornando información para el usuario: {}", user.email());

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error al obtener usuario: ", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("message", "Error al obtener usuario: " + e.getMessage());
            errorResponse.put("path", "/api/v1/auth/me");
            errorResponse.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)  // ← CRÍTICO: Forzar JSON
                    .body(errorResponse);
        }
    }
}
