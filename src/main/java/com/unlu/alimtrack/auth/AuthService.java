package com.unlu.alimtrack.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.exceptions.EmailYaRegistradoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.jwt.JwtService;
import com.unlu.alimtrack.mappers.UsuarioMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Servicio encargado de la autenticación y registro de usuarios.
 * Maneja la lógica de negocio para el inicio de sesión, registro de nuevos usuarios
 * y renovación de tokens JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    /**
     * Autentica a un usuario con sus credenciales (email y contraseña).
     *
     * @param request DTO que contiene el email y la contraseña del usuario.
     * @return AuthResponse que contiene el token de acceso, token de refresco y datos del usuario.
     * @throws RecursoNoEncontradoException Si el usuario no se encuentra en la base de datos después de una autenticación exitosa.
     * @throws AuthenticationException Si la autenticación falla (credenciales inválidas).
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequestDTO request) {
        log.info("Intentando autenticar al usuario: {}", request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            log.error("Fallo en la autenticación para el usuario: {}", request.email(), e);
            throw e;
        }

        UsuarioModel user = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.error("Usuario autenticado pero no encontrado en la base de datos: {}", request.email());
                    return new RecursoNoEncontradoException("Usuario no encontrado después de una autenticación exitosa para: " + request.email());
                });

        log.info("Usuario {} autenticado exitosamente.", user.getEmail());
        String accessToken = jwtService.getToken(user);
        String refreshToken = jwtService.getRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(usuarioMapper.toResponseDTO(user))
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request DTO con los datos del nuevo usuario (nombre, email, contraseña).
     * @return AuthResponse con los tokens generados y los datos del usuario registrado.
     * @throws IllegalArgumentException Si el nombre es nulo o vacío.
     * @throws EmailYaRegistradoException Si el email ya existe en la base de datos.
     */
    @Transactional
    public AuthResponse register(RegisterRequestDTO request) {
        log.info("Iniciando registro para el usuario: {}", request.email());

        if (request.nombre() == null || request.nombre().trim().isEmpty()) {
            log.warn("Intento de registro con nombre vacío para el email: {}", request.email());
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            log.warn("Intento de registro con email ya existente: {}", request.email());
            throw new EmailYaRegistradoException("El email ya está registrado: " + request.email());
        }

        UsuarioModel usuario = UsuarioModel.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(TipoRolUsuario.OPERADOR)
                .estaActivo(true)
                .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario registrado exitosamente con ID: {}", usuario.getId());

        String accessToken = jwtService.getToken(usuario);
        String refreshToken = jwtService.getRefreshToken(usuario);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(usuarioMapper.toResponseDTO(usuario))
                .build();
    }

    /**
     * Renueva el token de acceso utilizando un token de refresco válido.
     * Escribe la nueva respuesta de autenticación directamente en el HttpServletResponse.
     *
     * @param request  La petición HTTP que contiene el encabezado Authorization con el token de refresco.
     * @param response La respuesta HTTP donde se escribirá el nuevo token.
     * @throws IOException Si ocurre un error al escribir en el flujo de salida de la respuesta.
     * @throws RecursoNoEncontradoException Si el usuario asociado al token no existe.
     */
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Intento de refrescar token sin encabezado Authorization válido.");
            return;
        }

        refreshToken = authHeader.substring(7);
        userEmail = jwtService.getUsernameFromToken(refreshToken);

        if (userEmail != null) {
            var user = this.usuarioRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado para el email: " + userEmail));

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.getToken(user);
                var authResponse = AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .user(usuarioMapper.toResponseDTO(user))
                        .build();
                
                log.info("Token refrescado exitosamente para el usuario: {}", userEmail);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            } else {
                log.warn("Token de refresco inválido para el usuario: {}", userEmail);
            }
        } else {
             log.warn("No se pudo extraer el email del token de refresco.");
        }
    }
}
