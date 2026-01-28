package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.mappers.RecetaMapper;
import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.services.RecetaQueryService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de consultas específicas para Recetas.
 * Se enfoca en operaciones de lectura y verificación de existencia para evitar dependencias circulares
 * y separar la lógica de consulta de la lógica de negocio principal.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecetaQueryServiceImpl implements RecetaQueryService {

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper;
    private final VersionRecetaQueryService versionRecetaQueryService;

    /**
     * Verifica si una receta tiene versiones asociadas.
     *
     * @param codigoReceta El código de la receta padre.
     * @return true si tiene versiones, false en caso contrario.
     */
    @Override
    public boolean recetaTieneVersiones(String codigoReceta) {
        log.debug("Verificando si la receta {} tiene versiones asociadas.", codigoReceta);
        boolean tieneVersiones = versionRecetaQueryService.existsByRecetaPadre(codigoReceta);
        log.debug("Receta {} tiene versiones: {}", codigoReceta, tieneVersiones);
        return tieneVersiones;
    }

    /**
     * Verifica si existen recetas creadas por un usuario específico.
     *
     * @param email El email del usuario creador.
     * @return true si el usuario ha creado al menos una receta, false en caso contrario.
     */
    @Override
    public boolean existsByCreadoPorEmail(String email) {
        log.debug("Verificando si existen recetas creadas por el usuario: {}", email);
        boolean exists = recetaRepository.existsByCreadoPor_Email(email);
        log.debug("Usuario {} tiene recetas creadas: {}", email, exists);
        return exists;
    }
}
