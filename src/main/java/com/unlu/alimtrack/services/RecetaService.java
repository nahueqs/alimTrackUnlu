package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.services.validators.RecetaValidator;
import com.unlu.alimtrack.services.validators.UsuarioValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final RecetaModelMapper mapper;
    private final UsuarioService usuarioService;
    private final RecetaValidator recetaValidator;
    private final UsuarioValidator usuarioValidator;

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> findAllRecetas() {
        List<RecetaModel> recetas = recetaRepository.findAll();
        recetaValidator.validateModelList(recetas);
        return convertToResponseDTOList(recetas);
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
        return convertToResponseDTO(model);
    }

    @Transactional
    public RecetaResponseDTO updateReceta(String codigoReceta, RecetaModifyDTO recetaDTO) {
        RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
        recetaValidator.validateModification(recetaDTO);
        updateModelFromDTO(recetaDTO, model);
        saveModel(model);
        return convertToResponseDTO(model);
    }

    @Transactional
    public void deleteReceta(String codigo) {
        RecetaModel receta = findRecetaModelByCodigoReceta(codigo);
        recetaRepository.delete(receta);
    }

    private void saveModel(RecetaModel model) {
        recetaRepository.save(model);
    }

    @Transactional(readOnly = true)
    public boolean existsByCodigoReceta(String codigoReceta) {
        recetaValidator.validateCodigoReceta(codigoReceta);
        return recetaRepository.existsByCodigoReceta(codigoReceta);
    }

    @Transactional(readOnly = true)
    protected List<RecetaModel> findAllByCreadoPorUsername(String username) {
        if (usuarioService.existsByUsername(username)) {
        }
        return null;
    }

    @Transactional
    public RecetaResponseDTO addReceta(RecetaCreateDTO receta) {
        RecetaModel model = recetaRepository.findByCodigoReceta((receta.codigoReceta()));
        if (model != null) {
            throw new RecursoNoEncontradoException("Receta ya existente con codigo: " + receta.codigoReceta());
        }

        UsuarioModel usuario = usuarioService.getUsuarioModelById(receta.idUsuarioCreador());
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario no existe con id: " + receta.idUsuarioCreador());
        }

        model = mapper.recetaCreateDTOtoModel(receta);

        recetaRepository.save(model);

        return mapper.recetaModeltoRecetaResponseDTO(model);
    }

    private void updateModelFromDTO(RecetaModifyDTO recetaDTO, RecetaModel model) {
        mapper.updateModelFromModifyDTO(recetaDTO, model);
    }

    private List<RecetaResponseDTO> convertToResponseDTOList(List<RecetaModel> recetas) {
        return recetas.stream().map(mapper::recetaModeltoRecetaResponseDTO).collect(Collectors.toList());
    }

    private RecetaResponseDTO convertToResponseDTO(RecetaModel receta) {
        return mapper.recetaModeltoRecetaResponseDTO(receta);
    }

}