package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.validators.RecetaValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecetaService {

  private final RecetaRepository recetaRepository;
  private final RecetaMapper recetaMapper;
  private final RecetaValidator recetaValidator;
  private final UsuarioQueryService usuarioQueryService;


  @Transactional(readOnly = true)
  public List<RecetaResponseDTO> findAllRecetas() {
    List<RecetaModel> recetas = recetaRepository.findAll();
    recetaValidator.validateModelList(recetas);
    return recetaMapper.recetaModelsToRecetaResponseDTOs(recetas);
  }

  private RecetaModel findRecetaModelByCodigoReceta(String codigoReceta) {
    RecetaModel model = recetaRepository.findByCodigoReceta(codigoReceta);
    recetaValidator.validateModel(model, codigoReceta);
    return model;
  }

  @Transactional(readOnly = true)
  public RecetaResponseDTO findReceta(String codigoReceta) {
    recetaValidator.validateCodigoReceta(codigoReceta);
    RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
    return recetaMapper.recetaModeltoRecetaResponseDTO(model);
  }

  @Transactional
  public RecetaResponseDTO updateReceta(String codigoReceta, RecetaModifyDTO recetaDTO) {

    RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
    recetaValidator.validateDatosModification(recetaDTO);

    recetaMapper.updateModelFromModifyDTO(recetaDTO, model);
    saveRecetaModel(model);
    return recetaMapper.recetaModeltoRecetaResponseDTO(model);
  }

  @Transactional
  public void deleteReceta(String codigo) {
    RecetaModel receta = findRecetaModelByCodigoReceta(codigo);
    recetaRepository.delete(receta);
  }

  private void saveRecetaModel(RecetaModel model) {
    recetaRepository.save(model);
  }

  @Transactional(readOnly = true)
  public boolean existsByCodigoReceta(String codigoReceta) {
    recetaValidator.validateCodigoReceta(codigoReceta);
    return recetaRepository.existsByCodigoReceta(codigoReceta);
  }

  @Transactional(readOnly = true)
  protected List<RecetaModel> findAllByCreadoPorUsername(String username) {

    return null;
  }

  private void verificarUnicidadCodigoReceta(String codigoReceta) {
    if (recetaRepository.existsByCodigoReceta(codigoReceta)) {
      throw new RecursoNoEncontradoException("Receta ya existente con codigo: " + codigoReceta);
    }
  }

  private void verificarUsuarioExiste(String username) {
    if (usuarioQueryService.existsByUsername(username)) {
      throw new RecursoNoEncontradoException("Usuario no existe con id: " + username);
    }
  }

  private void verificarCreacionValida(RecetaCreateDTO recetaCreateDTO) {
    verificarUnicidadCodigoReceta(recetaCreateDTO.codigoReceta());
    verificarUsuarioExiste(recetaCreateDTO.usernameCreador());
  }

  private RecetaModel crearModelByCreateDTO(RecetaCreateDTO recetaCreateDTO) {
    return recetaMapper.recetaCreateDTOtoModel(recetaCreateDTO);
  }

  @Transactional
  public RecetaResponseDTO addReceta(RecetaCreateDTO receta) {
    // verifico datos de entrada
    verificarCreacionValida(receta);
    // traduzco el dto al model
    RecetaModel model = crearModelByCreateDTO(receta);
    // guardo el model
    recetaRepository.save(model);

    return recetaMapper.recetaModeltoRecetaResponseDTO(model);
  }


}