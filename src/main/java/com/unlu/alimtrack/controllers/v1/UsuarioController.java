package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UsuarioController.class);
    final UsuarioService usuarioService;

    @GetMapping()
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        log.debug("Obteniendo todos los usuarios");
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();
        log.debug("Retornando {} usuarios", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioByUsername(@PathVariable String username) {
        log.debug("Buscando usuario con username: {}", username);
        UsuarioResponseDTO usuario = usuarioService.getUsuarioByUsername(username);
        log.debug("Usuario encontrado: {}", usuario != null ? usuario.username() : "No encontrado");
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> addUsuario(@RequestBody UsuarioCreateDTO usuario) {
        log.debug("Creando nuevo usuario con username: {}", usuario.username());
        UsuarioResponseDTO saved = usuarioService.addUsuario(usuario);
        log.debug("Usuario creado exitosamente: {}", saved.username());
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + saved.username())).body(saved);
    }


    @PutMapping("/{username}")
    public ResponseEntity<Void> modifyUsuario(@PathVariable String username,
                                              @Valid @RequestBody UsuarioModifyDTO modificacion) {
        log.debug("Actualizando usuario con username: {}", username);
        usuarioService.modifyUsuario(username, modificacion);
        log.debug("Usuario actualizado exitosamente: {}", username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String username) {
        log.debug("Eliminando usuario con username: {}", username);
        usuarioService.deleteUsuario(username);
        log.debug("Usuario eliminado exitosamente: {}", username);
        return ResponseEntity.noContent().build();
    }

}
