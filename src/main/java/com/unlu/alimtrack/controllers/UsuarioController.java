package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.dtos.request.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.request.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.UsuarioService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping()
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> saveUsuario(@RequestBody UsuarioCreateDTO usuario) {
        UsuarioResponseDTO saved = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioResponseDTOById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modificarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioModifyDTO modificacion) {
        usuarioService.modificarUsuario(id, modificacion);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
    }

}
