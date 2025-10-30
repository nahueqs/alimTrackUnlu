package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.jwt.JwtService;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequestDTO request) {
        return null;

    }

    public AuthResponse register(RegisterRequestDTO request) {
        UsuarioModel usuario = UsuarioModel.builder()
                .username(request.username())
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(TipoRolUsuario.OPERADOR)
                .estaActivo(true)
                .build();

        usuarioRepository.save(usuario);

        return AuthResponse.builder()
                .token(jwtService.getToken(usuario))
                .build();

    }
}
