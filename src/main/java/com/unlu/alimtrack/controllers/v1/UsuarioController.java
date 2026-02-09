package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.DTOS.create.UsuarioCreateDTO;
import com.unlu.alimtrack.DTOS.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente")
    })
    @GetMapping()
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        log.info("Solicitud para obtener todos los usuarios");
        List<UsuarioResponseDTO> usuarios = usuarioService.getAllUsuarios();
        log.debug("Retornando {} usuarios", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por email", description = "Devuelve la información de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{email}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioByEmail(@PathVariable String email) {
        log.info("Solicitud para obtener el usuario con email: {}", email);
        UsuarioResponseDTO usuario = usuarioService.getUsuarioByEmail(email);
        log.debug("Retornando usuario: {}", usuario.email());
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario (rol OPERADOR por defecto)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado"),
            @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> saveUsuario(@Valid @RequestBody UsuarioCreateDTO usuario) {
        log.info("Solicitud para crear un nuevo usuario con email: {}", usuario.email());
        UsuarioResponseDTO saved = usuarioService.addUsuario(usuario);
        log.info("Usuario creado exitosamente con email: {}", saved.email());
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + saved.email())).body(saved);
    }

    @Operation(summary = "Modificar usuario", description = "Actualiza datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario modificado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de modificación inválidos")
    })
    @PutMapping("/{email}")
    public ResponseEntity<Void> updateUsuario(@PathVariable String email,
                                              @Valid @RequestBody UsuarioModifyDTO modificacion) {
        log.info("Solicitud para modificar el usuario con email: {}", email);
        usuarioService.modifyUsuario(email, modificacion);
        log.info("Usuario {} modificado exitosamente", email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar usuario", description = "Borra un usuario si no tiene dependencias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar porque tiene dependencias (recetas/versiones)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable String email) {
        log.info("Solicitud para eliminar el usuario con email: {}", email);
        usuarioService.deleteUsuario(email);
        log.info("Usuario {} eliminado exitosamente", email);
        return ResponseEntity.noContent().build();
    }
}
