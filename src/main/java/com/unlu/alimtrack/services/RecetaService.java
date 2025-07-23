package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.mappers.RecetaModelToRecetaDtoMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaService {
    @Autowired
    RecetaRepository recetaRepository;

    //@Transactional(readOnly = true)
    public List<RecetaDto> getAllRecetasDTOS() {
        List<RecetaModel> recetas =  recetaRepository.findAll();
        List<RecetaDto> recetasDtos = recetas.stream().map(
                RecetaModelToRecetaDtoMapper.mapper::recetaModelToRecetaDTO2).collect(Collectors.toList());
        return recetasDtos;
    }

    public RecetaDto getRecetaDtoById(Long id) {
        RecetaModel recetaModel = recetaRepository.findById(id).orElse(null);
        return RecetaModelToRecetaDtoMapper.mapper.recetaModelToRecetaDTO2(recetaModel);
    }
}
