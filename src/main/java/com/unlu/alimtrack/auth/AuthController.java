package com.unlu.alimtrack.auth;

import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UsuarioResponseDTO user = userService.getUsuarioByEmail(username);
        return ResponseEntity.ok(user);
    }


//    @PostMapping(value = "logout")
//    public String logout() {
//        return "logout";
//    }
//
//    @PostMapping(value = "refresh")
//    public String refresh() {
//        return "refresh";
//    }

}
