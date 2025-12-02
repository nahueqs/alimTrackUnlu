package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.auth.AuthResponse;
import com.unlu.alimtrack.auth.AuthService;
import com.unlu.alimtrack.auth.LoginRequestDTO;
import com.unlu.alimtrack.auth.RegisterRequestDTO;
import com.unlu.alimtrack.enums.TipoRolUsuario;
import com.unlu.alimtrack.exceptions.EmailYaRegistradoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.jwt.JwtService;
import com.unlu.alimtrack.mappers.UsuarioMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private UsuarioModel usuarioModel;
    private UsuarioResponseDTO usuarioResponseDTO;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO("test@example.com", "password123", "Test User");
        loginRequest = new LoginRequestDTO("test@example.com", "password123");

        usuarioModel = UsuarioModel.builder()
                .id(1L)
                .nombre("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .rol(TipoRolUsuario.OPERADOR)
                .estaActivo(true)
                .build();


        usuarioResponseDTO = new UsuarioResponseDTO("test@example.com", "Test User", TipoRolUsuario.OPERADOR.name());
        jwtToken = "mockedJwtToken";
    }

    @Test
    void register_shouldCreateUserAndReturnAuthResponse() {
        // Arrange
        when(usuarioRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioModel);
        when(jwtService.getToken(any(UsuarioModel.class))).thenReturn(jwtToken);
        when(usuarioMapper.toResponseDTO(any(UsuarioModel.class))).thenReturn(usuarioResponseDTO);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(jwtToken);
        assertThat(response.user()).isEqualTo(usuarioResponseDTO);
        verify(usuarioRepository, times(1)).existsByEmail(registerRequest.email());
        verify(passwordEncoder, times(1)).encode(registerRequest.password());
        verify(usuarioRepository, times(1)).save(any(UsuarioModel.class));
        verify(jwtService, times(1)).getToken(any(UsuarioModel.class));
        verify(usuarioMapper, times(1)).toResponseDTO(any(UsuarioModel.class));
    }

    @Test
    void register_shouldThrowEmailYaRegistradoException_whenEmailExists() {
        // Arrange
        when(usuarioRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailYaRegistradoException.class)
                .hasMessageContaining("El email ya está registrado: " + registerRequest.email());
        verify(usuarioRepository, times(1)).existsByEmail(registerRequest.email());
        verifyNoInteractions(passwordEncoder, jwtService, usuarioMapper);
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void register_shouldThrowIllegalArgumentException_whenNameIsEmpty() {
        // Arrange
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO("test@example.com", "password123", "");

        // Act & Assert
        assertThatThrownBy(() -> authService.register(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no puede ser nulo o vacío.");
        verifyNoInteractions(usuarioRepository, passwordEncoder, jwtService, usuarioMapper);
    }

    @Test
    void register_shouldThrowIllegalArgumentException_whenNameIsNull() {
        // Arrange
        RegisterRequestDTO invalidRequest = new RegisterRequestDTO("test@example.com", "password123", null);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El nombre no puede ser nulo o vacío.");
        verifyNoInteractions(usuarioRepository, passwordEncoder, jwtService, usuarioMapper);
    }

    @Test
    void login_shouldAuthenticateUserAndReturnAuthResponse() {
        // Arrange
        when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(usuarioModel));
        when(jwtService.getToken(usuarioModel)).thenReturn(jwtToken);
        when(usuarioMapper.toResponseDTO(usuarioModel)).thenReturn(usuarioResponseDTO);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo(jwtToken);
        assertThat(response.user()).isEqualTo(usuarioResponseDTO);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, times(1)).findByEmail(loginRequest.email());
        verify(jwtService, times(1)).getToken(usuarioModel);
        verify(usuarioMapper, times(1)).toResponseDTO(usuarioModel);
    }

    @Test
    void login_shouldThrowAuthenticationException_whenInvalidCredentials() {
        // Arrange
        doThrow(new BadCredentialsException("Invalid credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(usuarioRepository, jwtService, usuarioMapper);
    }

    @Test
    void login_shouldThrowRecursoNoEncontradoException_whenUserNotFoundAfterAuthentication() {
        // Arrange
        when(usuarioRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Usuario no encontrado después de una autenticación exitosa para: " + loginRequest.email());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, times(1)).findByEmail(loginRequest.email());
        verifyNoInteractions(jwtService, usuarioMapper);
    }
}
