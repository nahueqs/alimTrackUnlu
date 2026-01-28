package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de consultas para Versiones de Receta.
 * Proporciona métodos optimizados para verificar existencia y recuperar metadatos
 * sin cargar estructuras complejas innecesarias.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VersionRecetaQueryServiceImpl implements VersionRecetaQueryService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final VersionRecetaMapper versionRecetaMapper;
    private final VersionRecetaValidator versionRecetaValidator;

    /**
     * Verifica si existen versiones de receta creadas por un usuario específico.
     *
     * @param email Email del usuario creador.
     * @return true si existen versiones, false en caso contrario.
     */
    @Override
    public boolean existsByCreadaPorEmail(String email) {
        log.debug("Verificando existencia de versiones creadas por: {}", email);
        boolean exists = versionRecetaRepository.existsByCreadaPorEmail(email);
        log.debug("Usuario {} tiene versiones creadas: {}", email, exists);
        return exists;
    }

    /**
     * Obtiene la metadata de todas las versiones creadas por un usuario.
     *
     * @param email Email del usuario creador.
     * @return Lista de DTOs con la metadata de las versiones.
     */
    @Override
    public List<VersionMetadataResponseDTO> findAllByCreadoPorEmail(String email) {
        log.info("Buscando versiones de receta creadas por: {}", email);
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAllByCreadoPorEmail(email);
        
        if (versiones.isEmpty()) {
            log.info("No se encontraron versiones para el usuario {}", email);
        } else {
            versionRecetaValidator.validarVersionRecetaList(versiones);
            log.debug("Encontradas {} versiones para el usuario {}", versiones.size(), email);
        }
        
        return versionRecetaMapper.toMetadataResponseDTOList(versiones);
    }

    /**
     * Verifica si existen versiones asociadas a una receta padre.
     *
     * @param codigoRecetaPadre Código de la receta padre.
     * @return true si existen versiones, false en caso contrario.
     */
    @Override
    public boolean existsByRecetaPadre(String codigoRecetaPadre) {
        log.debug("Verificando existencia de versiones para la receta padre: {}", codigoRecetaPadre);
        boolean exists = versionRecetaRepository.existsByRecetaPadre_CodigoReceta(codigoRecetaPadre);
        log.debug("Receta {} tiene versiones: {}", codigoRecetaPadre, exists);
        return exists;
    }

    /**
     * Verifica si existe una versión de receta con un código específico.
     *
     * @param codigoVersion Código de la versión de receta.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsByCodigoVersion(String codigoVersion) {
        log.debug("Verificando existencia de versión con código: {}", codigoVersion);
        return versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion);
    }
}
