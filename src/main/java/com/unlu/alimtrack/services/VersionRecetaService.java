package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VersionRecetaService {

  private final VersionRecetaRespository versionRecetaRespository;
  private final RecetaService recetaService;
  private final VersionRecetaMapper versionRecetaMapper;
  private final UsuarioQueryService usuarioQueryService;

  @Transactional(readOnly = true)
  public List<VersionRecetaResponseDTO> findAllVersiones() {
    List<VersionRecetaModel> versiones = versionRecetaRespository.findAll();
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

  private void verificarVersionObtenidaByCodigoVersion(VersionRecetaModel version,
      String codigoVersion) {
    if (version == null) {
      throw new RecursoNoEncontradoException("No existe version con el codigo " + codigoVersion);
    }
  }

  @Transactional(readOnly = true)
  public VersionRecetaResponseDTO findByCodigoVersion(String codigoVersion) {
    VersionRecetaModel model = versionRecetaRespository.findByCodigoVersionReceta(codigoVersion);
    verificarVersionObtenidaByCodigoVersion(model, codigoVersion);
    return VersionRecetaMapper.mapper.toVersionRecetaResponseDTO(model);
  }

  @Transactional(readOnly = true)
  public List<VersionRecetaResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre) {
    List<VersionRecetaModel> versiones = versionRecetaRespository.findAllVersionesByCodigoRecetaPadre(
        codigoRecetaPadre);
    verificarListaVersionesObtenidaByCodigoReceta(versiones, codigoRecetaPadre);
    return versionRecetaMapper.toVersionRecetaResponseDTOList(versiones);
  }

  private void verificarCreacionVersionReceta(String codigoRecetaPadre,
      VersionRecetaCreateDTO versionRecetaCreateDTO) {
    verificarIntegridadDatos(codigoRecetaPadre, versionRecetaCreateDTO);
    verificarVersionRepetida(versionRecetaCreateDTO.codigoVersionReceta());
    verificarRecetaExistente(versionRecetaCreateDTO.codigoRecetaPadre());
    verificarUsuarioExiste(versionRecetaCreateDTO.usernameCreador());
  }

  private void verificarIntegridadDatos(String codigoRecetaPadre,
      VersionRecetaCreateDTO versionRecetaCreateDTO) {
    if (!codigoRecetaPadre.equals(versionRecetaCreateDTO.codigoRecetaPadre())) {
      throw new ModificacionInvalidaException("La receta padre es inconsistente");
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
    if (versionRecetaRespository.existsByCodigoVersionReceta(codigoVersion)) {
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

    versionRecetaRespository.save(versionModelFinal);

    return versionRecetaMapper.toVersionRecetaResponseDTO(versionModelFinal);
  }

  protected VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta) {
    VersionRecetaModel model = versionRecetaRespository.findByCodigoVersionReceta(
        codigoVersionReceta);
    if (model == null) {
      throw new RecursoNoEncontradoException(
          "No existe ninguna version con el codigo " + codigoVersionReceta);
    }
    return model;
  }
}
