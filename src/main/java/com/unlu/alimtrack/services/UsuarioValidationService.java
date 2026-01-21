// UsuarioValidationService.java
package com.unlu.alimtrack.services;

import com.unlu.alimtrack.models.UsuarioModel;

public interface UsuarioValidationService {
    UsuarioModel validarUsuarioAutorizado(String email);

    UsuarioModel validarUsuarioActivo(UsuarioModel usuario);
}