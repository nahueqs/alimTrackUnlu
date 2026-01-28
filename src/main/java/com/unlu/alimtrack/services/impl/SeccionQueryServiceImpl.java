package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.repositories.SeccionRepository;
import com.unlu.alimtrack.services.SeccionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de consultas para Secciones.
 * Proporciona métodos optimizados para verificar la existencia de secciones
 * basándose en diferentes criterios, evitando la carga completa de entidades.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeccionQueryServiceImpl implements SeccionQueryService {

    private final SeccionRepository seccionRepository;

    /**
     * Verifica si existe una sección con un orden específico en una versión de receta.
     *
     * @param codigoVersion Código de la versión de receta padre.
     * @param orden         Orden a verificar.
     * @return true si ya existe una sección con ese orden, false en caso contrario.
     */
    @Override
    public boolean existsByVersionRecetaPadreAndOrden(String codigoVersion, Integer orden) {
        log.debug("Verificando existencia de sección con orden {} en versión {}", orden, codigoVersion);
        boolean exists = seccionRepository.existsByVersionRecetaPadre_CodigoVersionRecetaAndOrden(codigoVersion, orden);
        log.debug("Existe sección con orden {} en {}: {}", orden, codigoVersion, exists);
        return exists;
    }

    /**
     * Verifica si existe una sección con un título específico en una versión de receta.
     *
     * @param codigoVersion Código de la versión de receta padre.
     * @param titulo        Título a verificar.
     * @return true si ya existe una sección con ese título, false en caso contrario.
     */
    @Override
    public boolean existsByVersionRecetaPadreAndTitulo(String codigoVersion, String titulo) {
        log.debug("Verificando existencia de sección con título '{}' en versión {}", titulo, codigoVersion);
        boolean exists = seccionRepository.existsByVersionRecetaPadre_CodigoVersionRecetaAndTitulo(codigoVersion, titulo);
        log.debug("Existe sección con título '{}' en {}: {}", titulo, codigoVersion, exists);
        return exists;
    }
}
