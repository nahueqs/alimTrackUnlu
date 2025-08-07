package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.exception.DatabaseException;
import com.unlu.alimtrack.exception.InternalServiceException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final RecetaModelMapper mapper;

    public RecetaService(RecetaRepository recetaRepository, RecetaModelMapper mapper) {
        this.recetaRepository = recetaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> getAllRecetasResponseDTOS() {
        try {
            List<RecetaModel> recetas = recetaRepository.findAll();
            if (recetas.isEmpty()) {
                throw new RecursoNoEncontradoException("No se encontraron recetas");
            }
            return recetas.stream().map(
                    mapper::recetaModeltoRecetaResponseDTO).collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new DatabaseException("Error accediendo a la base de datos");
        } catch (Exception e) {
            throw new InternalServiceException("Error inesperado al obtener recetas");
        }
    }

    public RecetaResponseDTO getRecetaResponseDTOById(Long id) {
        RecetaModel recetaModel = recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return mapper.recetaModeltoRecetaResponseDTO(recetaModel);
    }

    public RecetaModel getRecetaModelById(Long id) {
        return recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
    }

    public RecetaResponseDTO updateReceta(RecetaDto receta) {
        RecetaModel model = recetaRepository.findById(receta.getId()).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + receta.getId()));
        mapper.recetaDTOToRecetaModel(receta);
        recetaRepository.save(model);
        return mapper.recetaModeltoRecetaResponseDTO(model);
    }

    public void deleteRecetaByID(Long id) {
        recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        recetaRepository.deleteById(id);
    }

    public boolean existsByCreadoPor(Long id) {
        return false;
    }
}
