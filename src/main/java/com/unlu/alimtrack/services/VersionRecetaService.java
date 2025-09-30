package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.BorradoFallidoException;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.queries.ProduccionQueryService;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VersionRecetaService {

  private final VersionRecetaRepository versionRecetaRepository;
  private final RecetaService recetaService;
  private final VersionRecetaMapper versionRecetaMapper;
  private final UsuarioQueryService usuarioQueryService;
  private final VersionRecetaValidator versionRecetaValidator;
  private final ProduccionQueryService produccionQueryService;

  @Transactional(readOnly = true)
  public List<VersionRecetaResponseDTO> findAllVersiones() {
    List<VersionRecetaModel> versiones = versionRecetaRepository.findAll();
    verificarListaVersionesObtenida(versiones);
    return versionRecetaMapper.toVersionRecetaResponseDTOList(versiones);
  }

  private void verificarListaVersionesObtenida(List<VersionRecetaModel> versiones) {
    if (versiones.isEmpty()) {
      throw new RecursoNoEncontradoException("No hay versiones guardadas para ninguna receta");
    }
  }

  private void verificarListaVersionesObtenidaByCodigoReceta(List<VersionRecetaModel> versiones,
      String codigoReceta) {
    if (versiones.isEmpty()) {
      throw new RecursoNoEncontradoException(
          "No hay versiones guardadas para la receta con codigo " + codigoReceta);
    }
  }

  @Transactional(readOnly = true)
  public VersionRecetaResponseDTO findByCodigoVersion(String codigoVersion) {
    VersionRecetaModel model = versionRecetaRepository.findByCodigoVersionReceta(codigoVersion);
    verificarVersionModelNotNull(model, codigoVersion);
    return VersionRecetaMapper.mapper.toVersionRecetaResponseDTO(model);
  }

  @Transactional(readOnly = true)
  public List<VersionRecetaResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre) {
    List<VersionRecetaModel> versiones = versionRecetaRepository.findAllVersionesByCodigoRecetaPadre(
        codigoRecetaPadre);
    verificarListaVersionesObtenidaByCodigoReceta(versiones, codigoRecetaPadre);
    return versionRecetaMapper.toVersionRecetaResponseDTOList(versiones);
  }

  private void verificarCreacionVersionReceta(String codigoRecetaPadre,
      VersionRecetaCreateDTO versionRecetaCreateDTO) {
    verificarIntegridadDatosCreacion(codigoRecetaPadre, versionRecetaCreateDTO);
    verificarVersionRepetida(versionRecetaCreateDTO.codigoVersionReceta());
    verificarRecetaExistente(versionRecetaCreateDTO.codigoRecetaPadre());
    verificarUsuarioExiste(versionRecetaCreateDTO.usernameCreador());
  }

  private void verificarIntegridadDatosCreacion(String codigoRecetaPadre,
      VersionRecetaCreateDTO versionRecetaCreateDTO) {
    if (!codigoRecetaPadre.equals(versionRecetaCreateDTO.codigoRecetaPadre())) {
      throw new ModificacionInvalidaException(
          "El código de la url no coincide con el código de receta en el cuerpo de la petición.");
    }
  }

  private void verificarRecetaExistente(String codigoReceta) {
    if (!recetaService.existsByCodigoReceta(codigoReceta)) {
      throw new RecursoNoEncontradoException("Receta no existe");
    }
  }

  private void verificarUsuarioExiste(String username) {
    if (!usuarioQueryService.existsByUsername(username)) {
      throw new RecursoNoEncontradoException(
          "No existe un usuario registrado con el username " + username);
    }
  }

  private void verificarVersionRepetida(String codigoVersion) {
    if (versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion)) {
      throw new RecursoNoEncontradoException(
          "Ya existe una version receta con el codigo " + codigoVersion);
    }
  }

  @Transactional
  public VersionRecetaResponseDTO saveVersionReceta(String codigoRecetaPadre,
      VersionRecetaCreateDTO versionRecetaCreateDTO) {
    // verifico que no exista una version con ese codigo
    // verifico que exista la receta padre
    // verifico que exista el usuario creador
    verificarCreacionVersionReceta(codigoRecetaPadre, versionRecetaCreateDTO);
    // mapeo el dto a un nuevo model
    VersionRecetaModel versionModelFinal = versionRecetaMapper.toVersionRecetaModel(
        versionRecetaCreateDTO);
    versionRecetaRepository.save(versionModelFinal);
    return versionRecetaMapper.toVersionRecetaResponseDTO(versionModelFinal);
  }

  private void verificarVersionModelNotNull(VersionRecetaModel model, String codigoVersion) {
    if (model == null) {
      throw new RecursoNoEncontradoException(
          "No existe ninguna version con el codigo " + codigoVersion);
    }
  }

  public VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta) {
    VersionRecetaModel model = versionRecetaRepository.findByCodigoVersionReceta(
        codigoVersionReceta);
    verificarVersionModelNotNull(model, codigoVersionReceta);
    return model;
  }

  public VersionRecetaResponseDTO updateVersionReceta(String codigoReceta, VersionRecetaModifyDTO modificacion) {
    versionRecetaValidator.validateModification(modificacion);
    VersionRecetaModel model = findVersionModelByCodigo(codigoReceta);
    versionRecetaMapper.updateModelFromModifyDTO(modificacion, model);
    saveVersionModel(model);
    return versionRecetaMapper.toVersionRecetaResponseDTO(model);
  }

  private void saveVersionModel(VersionRecetaModel model) {
    versionRecetaRepository.save(model);
  }

  private void validateDelete(String codigoVersion) {
    validateNoTieneProduccionesHijas(codigoVersion);
  }

  private void validateNoTieneProduccionesHijas(String codigoVersion) {
    if (produccionQueryService.existsByVersionRecetaPadre(codigoVersion)) {
      throw new BorradoFallidoException(
          "La version tiene producciones hijas existentes. codigo version: " + codigoVersion);
    }
  }

  public void deleteVersionReceta(String codigoVersion) {
    VersionRecetaModel receta = findVersionModelByCodigo(codigoVersion);
    validateDelete(codigoVersion);
    versionRecetaRepository.delete(receta);
  }

}
