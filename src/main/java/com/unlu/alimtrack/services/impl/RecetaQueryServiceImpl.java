package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.services.RecetaQueryService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecetaQueryServiceImpl implements RecetaQueryService {

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper;
    private final VersionRecetaQueryService versionRecetaQueryService;

    @Override
    public boolean recetaTieneVersiones(String codigoReceta) {
        return versionRecetaQueryService.existsByRecetaPadre(codigoReceta);

    }

    @Override
    public boolean recetaPerteneceAUsuario(String codigoReceta, String email) {
        return recetaRepository.existsByCodigoRecetaAndCreadoPorEmail(codigoReceta, email);
    }

    @Override
    public List<RecetaMetadataResponseDTO> findAllByCreadoPorEmail(String email) {
        List<RecetaModel> recetas = recetaRepository.findAllByCreadoPor_Email(email);
        if (recetas == null) {
            throw new RecursoNoEncontradoException(
                    "No existen el receta creadas por el email " + email);
        }

        return recetaMapper.recetaModelsToRecetaResponseDTOs(recetas);
    }

    @Override
    public boolean existsByCreadoPorEmail(String email) {
        return recetaRepository.existsByCreadoPor_Email(email);
    }

}
