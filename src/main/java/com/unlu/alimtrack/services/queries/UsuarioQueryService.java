package com.unlu.alimtrack.services.queries;

public interface UsuarioQueryService {

  boolean usuarioPuedeSerEliminado(String username);

  boolean usuarioTieneRecetasAsociadas(String username);

  boolean usuarioTieneVersionesRecetasAsociadas(String username);

  boolean existsByUsername(String username);

  boolean estaActivoByUsername(String username);
}
