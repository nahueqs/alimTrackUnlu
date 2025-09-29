package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.UsuarioCreateDTO;
import com.unlu.alimtrack.dtos.modify.UsuarioModifyDTO;
import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import com.unlu.alimtrack.exception.BorradoFallidoException;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoDuplicadoException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.UsuarioMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.UsuarioRepository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.validators.UsuarioValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final UsuarioValidator usuarioValidator;
  private final PasswordEncoder passwordEncoder;
  private final UsuarioQueryService usuarioQueryService;

  @Transactional(readOnly = true)
  public List<UsuarioResponseDTO> getAllUsuarios() {
    List<UsuarioModel> usuarios = usuarioRepository.findAll();
    usuarioValidator.validateListUsuarios(usuarios);
    return usuarioMapper.convertToResponseDTOList(usuarios);
  }

  @Transactional(readOnly = true)
  public UsuarioResponseDTO getUsuarioByUsername(String username) {
    usuarioValidator.validateUsername(username);
    UsuarioModel model = getUsuarioModelByUsername(username);
    return usuarioMapper.convertToResponseDTO(model);
  }

  public UsuarioModel getUsuarioModelByUsername(String username) {
    UsuarioModel model = usuarioRepository.findByUsername(username).orElseThrow(
        () -> new RecursoNoEncontradoException("Usuario no encontrado con username" + username));
    usuarioValidator.validateUsuario(model);
    return model;
  }

  private void validarNuevoUsuario(UsuarioCreateDTO usuario) {
    if (usuarioRepository.existsByEmail(usuario.email())) {
      throw new RecursoDuplicadoException("El email ya ha sido usado por un usuario existente");
    }

    if (usuarioRepository.existsByUsername(usuario.username())) {
      throw new RecursoDuplicadoException("El email ya ha sido usado por un usuario existente");
    }
  }

  private UsuarioModel crearNuevoUsuarioByCreateDTO(UsuarioCreateDTO usuario) {
    UsuarioModel usuarioModel = usuarioMapper.usuarioCreateDTOToModel(usuario);
    String passwordEncriptada = passwordEncoder.encode(usuario.contraseña());
    usuarioModel.setPassword(passwordEncriptada);
    return usuarioModel;
  }

  public UsuarioResponseDTO addUsuario(UsuarioCreateDTO usuarioCreateDTO) {
    validarNuevoUsuario(usuarioCreateDTO);
    UsuarioModel usuarioModel = crearNuevoUsuarioByCreateDTO(usuarioCreateDTO);
    usuarioRepository.save(usuarioModel);
    return usuarioMapper.convertToResponseDTO(usuarioModel);
  }

  private void aplicarModificaciones(UsuarioModel usuarioExistente, UsuarioModifyDTO modificacion) {
    usuarioMapper.updateModelFromModifyDTO(modificacion, usuarioExistente);
    if (modificacion.contraseña() != null) {
      String passwordEncriptada = passwordEncoder.encode(modificacion.contraseña());
      usuarioExistente.setPassword(passwordEncriptada);
    }
  }

  public void modifyUsuario(String username, UsuarioModifyDTO modificacion) {
    // verifico validez de datos de entrada
    usuarioValidator.validarModificacion(modificacion);
    // traigo el usuario sobre el que se van a hacer las modificaciones
    UsuarioModel usuarioExistente = getUsuarioModelByUsername(username);
    // valido que el nuevo email y el nuevo username no sean usados por otros usuarios
    validateUnicidadDatos(usuarioExistente, modificacion);
    // aplico las modificaciones al usuarioExistente
    aplicarModificaciones(usuarioExistente, modificacion);

    usuarioRepository.save(usuarioExistente);
  }

  private void validateUnicidadDatos(UsuarioModel usuarioExistente, UsuarioModifyDTO modificacion) {
    validateUnicidadEmail(usuarioExistente, modificacion.email());
    validateUnicidadUsername(usuarioExistente, modificacion.username());
  }

  private void validateUnicidadUsername(UsuarioModel usuarioExistente, String username) {
    if (username != null && !username.equals(usuarioExistente.getUsername())) {
      if (usuarioRepository.existsByUsername(username)) {
        throw new ModificacionInvalidaException("El email ya está en uso por otro usuario");
      }
    }
  }

  private void validateUnicidadEmail(UsuarioModel usuarioExistente, String email) {
    if (email != null && !email.equals(usuarioExistente.getEmail())) {
      if (usuarioRepository.existsByEmail(email)) {
        throw new ModificacionInvalidaException("El email ya está en uso por otro usuario");
      }
    }
  }

  private void validarDeleteUsario(String username) {
    if (!usuarioQueryService.usuarioPuedeSerEliminado(username)) {
      throw new BorradoFallidoException(
          "No se puede borrar el usuario, tiene recetas o versiones asociadas.");
    }
  }

  @Transactional
  public void deleteUsuario(String username) {
    UsuarioModel model = getUsuarioModelByUsername(username);
    validarDeleteUsario(username);
    usuarioRepository.deleteByUsername(username);
  }


}
