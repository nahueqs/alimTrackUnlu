package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.dtos.UsuarioDto;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;

    @GetMapping()
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @PostMapping
    public UsuarioModel saveUsuario(@RequestBody UsuarioModel usuarioModel) {
        return usuarioService.saveUsuario(usuarioModel);
    }

    @GetMapping("/{id}")
    public UsuarioDto getRecetaDtoById(@PathVariable Long id) {
        return usuarioService.getUsuarioDtoById(id);
    }

    @PutMapping("/{id}")
    public void modificarUsuario(@RequestBody UsuarioModel usuarioModel) {
        usuarioService.modificarUsuario(usuarioModel);
    }

    @DeleteMapping("/{id}")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.borrarUsuario(id);
    }

}
