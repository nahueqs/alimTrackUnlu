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
    @Autowired
    RecetaRepository recetaRepository;

    @Transactional(readOnly = true)
    public List<RecetaDto> getAllRecetasDTOS() {
        List<RecetaModel> recetas =  recetaRepository.findAll();
        return recetas.stream().map(
                RecetaModelToDtoMapper.mapper::recetaModelToRecetaDTO).collect(Collectors.toList());
    }

    public RecetaDto getRecetaDtoById(Long id) {
        RecetaModel recetaModel = recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return RecetaModelToDtoMapper.mapper.recetaModelToRecetaDTO(recetaModel);
    }

    public void updateReceta(RecetaModel receta) {
        recetaRepository.save(receta);
    }

    public void deleteRecetaByID(Long id) {
        recetaRepository.deleteById(id);
    }
}
