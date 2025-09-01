package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VersionRecetaQueryServiceImpl implements VersionRecetaQueryService {

    private final VersionRecetaRespository versionRecetaRespository;
    private final VersionRecetaMapper versionRecetaMapper;
    private final VersionRecetaValidator versionRecetaValidator;

    @Override
    public boolean existenVersionesPorUsuario(String username) {
        return versionRecetaRespository.existsByCreadaPorUsername(username);
    }

    @Override
    public List<VersionRecetaResponseDTO> findAllByCreadoPorUsername(String username) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.findAllByCreadoPorUsername(username);
        versionRecetaValidator.validarVersionRecetaList(versiones);
        return versionRecetaMapper.toVersionRecetaResponseDTOList(versiones);
    }


}
