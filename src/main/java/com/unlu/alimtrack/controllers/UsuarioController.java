package com.unlu.alimtrack.controllers;

import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    UsuarioService usuarioService;

    @GetMapping()
    public ArrayList<UsuarioModel> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @PostMapping
    public UsuarioModel saveUsuario(@RequestBody UsuarioModel usuarioModel) {
        return usuarioService.saveUsuario(usuarioModel);
    }

}
