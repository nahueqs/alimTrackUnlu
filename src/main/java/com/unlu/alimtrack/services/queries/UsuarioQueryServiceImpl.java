package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioQueryServiceImpl implements UsuarioQueryService {

    private final RecetaRepository recetaRepository;
    private final VersionRecetaRespository versionRecetaRespository;

    @Override
    public boolean usuarioPuedeSerEliminado(String username) {
        return !usuarioTieneRecetasAsociadas(username) &&
                !usuarioTieneVersionesRecetasAsociadas(username);
    }

    @Override
    public boolean usuarioTieneRecetasAsociadas(String username) {
        return recetaRepository.existsByCreadoPorUsername(username);
    }

    @Override
    public boolean usuarioTieneVersionesRecetasAsociadas(String username) {
        return versionRecetaRespository.existsByCreadaPorUsername(username);
    }
}
