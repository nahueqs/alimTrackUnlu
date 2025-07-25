package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaModelToDtoMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final RecetaModelToDtoMapper mapper;

    @Autowired
    public RecetaService(RecetaRepository recetaRepository, RecetaModelToDtoMapper mapper) {
        this.recetaRepository = recetaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<RecetaDto> getAllRecetasDTOS() {
        List<RecetaModel> recetas = recetaRepository.findAll();
        return recetas.stream().map(
                mapper::recetaModelToRecetaDTO).collect(Collectors.toList());
    }

    public RecetaDto getRecetaDtoById(Long id) {
        RecetaModel recetaModel = recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return mapper.recetaModelToRecetaDTO(recetaModel);
    }

    public RecetaModel getRecetaModelById(Long id) {
        RecetaModel recetaModel = recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return recetaModel;
    }


    public void updateReceta(RecetaDto receta) {
        RecetaModel model = recetaRepository.findById(receta.getId()).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + receta.getId()));
        mapper.recetaDTOToRecetaModel(receta);
        recetaRepository.save(model);
    }

    public void deleteRecetaByID(Long id) {
        recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        recetaRepository.deleteById(id);
    }
}
