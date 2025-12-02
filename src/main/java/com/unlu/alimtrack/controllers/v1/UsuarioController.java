package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping()
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        log.info("Solicitud para obtener todos los usuarios");
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();
        log.debug("Retornando {} usuarios", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioByEmail(@PathVariable String email) {
        log.info("Solicitud para obtener el usuario con email: {}", email);
        UsuarioResponseDTO usuario = usuarioService.getUsuarioByEmail(email);
        log.debug("Retornando usuario: {}", usuario.email());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> addUsuario(@Valid @RequestBody UsuarioCreateDTO usuario) {
        log.info("Solicitud para crear un nuevo usuario con email: {}", usuario.email());
        UsuarioResponseDTO saved = usuarioService.addUsuario(usuario);
        log.info("Usuario creado exitosamente con email: {}", saved.email());
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + saved.email())).body(saved);
    }

    @PutMapping("/{email}")
    public ResponseEntity<Void> modifyUsuario(@PathVariable String email,
                                              @Valid @RequestBody UsuarioModifyDTO modificacion) {
        log.info("Solicitud para modificar el usuario con email: {}", email);
        usuarioService.modifyUsuario(email, modificacion);
        log.info("Usuario {} modificado exitosamente", email);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String email) {
        log.info("Solicitud para eliminar el usuario con email: {}", email);
        usuarioService.deleteUsuario(email);
        log.info("Usuario {} eliminado exitosamente", email);
        return ResponseEntity.noContent().build();
    }
}
