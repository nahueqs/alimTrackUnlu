package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioQueryServiceImpl implements UsuarioQueryService {

  private final UsuarioRepository usuarioRepository;
  private final RecetaQueryService recetaQueryService;
  private final VersionRecetaQueryService versionRecetaQueryService;

  @Override
  public boolean usuarioPuedeSerEliminado(String username) {
    return !usuarioTieneRecetasAsociadas(username) &&
        !usuarioTieneVersionesRecetasAsociadas(username);
  }

  @Override
  public boolean usuarioTieneRecetasAsociadas(String username) {
    return recetaQueryService.existsByCreadoPorUsername(username);
  }

  @Override
  public boolean usuarioTieneVersionesRecetasAsociadas(String username) {
    return versionRecetaQueryService.existsByCreadaPorUsername(username);
  }

  @Override
  public boolean existsByUsername(String username) {
    return usuarioRepository.existsByUsername(username);
  }
}
