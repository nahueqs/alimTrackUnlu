package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.DTOS.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
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
    public boolean existsByCreadaPorUsername(String username) {
        return versionRecetaRepository.existsByCreadaPorUsername(username);
    }

    @Override
    public List<VersionRecetaResponseDTO> findAllByCreadoPorUsername(String username) {
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAllByCreadoPorUsername(
                username);
        versionRecetaValidator.validarVersionRecetaList(versiones);
        return versionRecetaMapper.toVersionRecetaResponseDTOList(versiones);
    }

    @Override
    public boolean existsByRecetaPadre(String codigoRecetaPadre) {
        return versionRecetaRepository.existsByRecetaPadre_CodigoReceta(codigoRecetaPadre);
    }

    @Override
    public boolean existsByCodigoVersion(String codigoVersion) {
        return versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion);
    }

    @Override
    public boolean versionTieneProducciones(String codigoVersion) {
        return produccionQueryService.existsByVersionRecetaPadre(codigoVersion);
    }


}
