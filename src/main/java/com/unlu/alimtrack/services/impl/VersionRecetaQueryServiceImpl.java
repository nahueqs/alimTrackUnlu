package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VersionRecetaQueryServiceImpl implements VersionRecetaQueryService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final VersionRecetaMapper versionRecetaMapper;
    private final VersionRecetaValidator versionRecetaValidator;
    private final ProduccionQueryService produccionQueryService;

    @Override
    public boolean existsByCreadaPorEmail(String email) {
        return versionRecetaRepository.existsByCreadaPorEmail(email);
    }


    @Override
    public List<VersionMetadataResponseDTO> findAllByCreadoPorEmail(String email) {
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAllByCreadoPorEmail(
                email);
        versionRecetaValidator.validarVersionRecetaList(versiones);
        return versionRecetaMapper.toMetadataResponseDTOList(versiones);
    }

    @Override
    public boolean existsByRecetaPadre(String codigoRecetaPadre) {
        return versionRecetaRepository.existsByRecetaPadre_CodigoReceta(codigoRecetaPadre);
    }

    @Override
    public boolean existsByCodigoVersion(String codigoVersion) {
        return versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion);
    }


}
