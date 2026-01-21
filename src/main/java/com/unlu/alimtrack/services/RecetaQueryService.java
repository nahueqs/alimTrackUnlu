package com.unlu.alimtrack.services;

public interface RecetaQueryService {

    boolean recetaTieneVersiones(String codigoReceta);

    boolean existsByCreadoPorEmail(String email);
}
