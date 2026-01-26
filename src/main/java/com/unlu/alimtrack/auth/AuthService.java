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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequestDTO request) {
        log.info("Procesando autenticación para el usuario: {}", request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UsuarioModel user = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado después de una autenticación exitosa para: " + request.email()));

        log.debug("Usuario {} autenticado correctamente. Generando token.", user.getEmail());
        String accessToken = jwtService.getToken(user);
        String refreshToken = jwtService.getRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(usuarioMapper.toResponseDTO(user))
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequestDTO request) {
        log.info("Procesando registro para el nuevo usuario: {}", request.email());

        if (request.nombre() == null || request.nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException("El email ya está registrado: " + request.email());
        }

        log.debug("Codificando password y creando nuevo UsuarioModel para: {}", request.email());
        UsuarioModel usuario = UsuarioModel.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(TipoRolUsuario.OPERADOR)
                .estaActivo(true)
                .build();

        usuario = usuarioRepository.save(usuario); // Reassign the usuario object with the saved instance
        log.info("Usuario {} guardado en la base de datos.", usuario.getEmail());

        String accessToken = jwtService.getToken(usuario);
        String refreshToken = jwtService.getRefreshToken(usuario);
        log.debug("Token generado para el nuevo usuario: {}", usuario.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(usuarioMapper.toResponseDTO(usuario))
                .build();
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.getUsernameFromToken(refreshToken);
        if (userEmail != null) {
            var user = this.usuarioRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.getToken(user);
                var authResponse = AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .user(usuarioMapper.toResponseDTO(user))
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
