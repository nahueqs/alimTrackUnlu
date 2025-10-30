package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;


    public AuthResponse login(LoginRequestDTO request) {
        return null;

    }

    public AuthResponse register(RegisterRequestDTO request) {


        return null;

    }
}
