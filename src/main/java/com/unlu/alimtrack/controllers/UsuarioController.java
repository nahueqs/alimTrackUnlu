package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.create.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @GetMapping("/{username}")
  public ResponseEntity<UsuarioResponseDTO> getUsuarioByUsername(@PathVariable String username) {
    return ResponseEntity.ok(usuarioService.getUsuarioByUsername(username));
  }

  @PostMapping
  public ResponseEntity<UsuarioResponseDTO> addUsuario(@RequestBody UsuarioCreateDTO usuario) {
    UsuarioResponseDTO saved = usuarioService.addUsuario(usuario);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @PutMapping("/{username}")
  public ResponseEntity<Void> modifyUsuario(@PathVariable String username,
      @Valid @RequestBody UsuarioModifyDTO modificacion) {
    usuarioService.modifyUsuario(username, modificacion);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{username}")
  public ResponseEntity<Void> deleteUsuario(@PathVariable String username) {
    usuarioService.deleteUsuario(username);
    return ResponseEntity.noContent().build();
  }

}
