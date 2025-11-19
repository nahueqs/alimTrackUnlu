package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.jwt.JwtService;
import com.unlu.alimtrack.mappers.UsuarioMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    public AuthResponse login(LoginRequestDTO request) {

        // 1. Autentica al usuario. Si falla, lanza una excepción.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password())
        );

        // 2. Si la autenticación es exitosa, obtenemos los detalles completos del usuario.
        UsuarioModel user = usuarioRepository.findByEmail(request.email()).orElseThrow(
                () -> new RecursoNoEncontradoException("Usuario no encontrado después de la autenticación")
        );

        // 3. Generamos el token.
        String token = jwtService.getToken(user);

        // 4. Construimos la respuesta COMPLETA.
        return AuthResponse.builder()
                .token(token)
                .user(usuarioMapper.convertToResponseDTO(user)) // Asumiendo que AuthResponse tiene un campo 'user' de tipo UsuarioModel o un DTO.
                .build();
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
