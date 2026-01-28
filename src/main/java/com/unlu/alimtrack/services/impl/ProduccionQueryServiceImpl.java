package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.protegido.ProduccionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.EstadoProduccionPublicoResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.validators.ProduccionQueryServiceValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio de consultas para Producciones.
 * Se encarga de recuperar información de producciones aplicando filtros y transformaciones necesarias.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProduccionQueryServiceImpl implements ProduccionQueryService {

    private final ProduccionRepository produccionRepository;
    private final ProduccionMapper produccionMapper;
    private final ProduccionQueryServiceValidator produccionQueryServiceValidator;

    /**
     * Busca una producción por su código único y retorna su metadata.
     *
     * @param codigo Código de la producción.
     * @return DTO con la metadata de la producción.
     * @throws RecursoNoEncontradoException Si la producción no existe.
     */
    @Override
    public ProduccionMetadataResponseDTO findByCodigoProduccion(String codigo) {
        log.info("Buscando producción con código: {}", codigo);
        ProduccionModel model = produccionRepository.findByCodigoProduccion(codigo)
                .orElseThrow(() -> {
                    log.error("Producción no encontrada con código: {}", codigo);
                    return new RecursoNoEncontradoException("Producción no encontrada con código: " + codigo);
                });
        log.debug("Producción {} encontrada. Mapeando a DTO.", codigo);
        return produccionMapper.modelToResponseDTO(model);
    }

    /**
     * Obtiene una lista de producciones que cumplen con los filtros especificados.
     *
     * @param filtros DTO con los criterios de filtrado.
     * @return Lista de DTOs con la metadata de las producciones encontradas.
     */
    @Override
    public List<ProduccionMetadataResponseDTO> getAllProduccionesMetadata(@Valid ProduccionFilterRequestDTO filtros) {
        log.info("Iniciando búsqueda de producciones con filtros: {}", filtros);
        
        List<ProduccionModel> producciones = buscarProduccionesPorFiltros(filtros);
        
        if (producciones.isEmpty()) {
            log.info("No se encontraron producciones con los filtros aplicados.");
        } else {
            log.debug("Encontradas {} producciones con los filtros aplicados.", producciones.size());
        }
        
        return produccionMapper.modelListToResponseDTOList(producciones);
    }

    /**
     * Obtiene el estado público de una producción.
     *
     * @param codigoProduccion Código de la producción.
     * @return DTO con la información pública de la producción.
     * @throws RecursoNoEncontradoException Si la producción no existe.
     */
    @Override
    public EstadoProduccionPublicoResponseDTO getEstadoProduccion(String codigoProduccion) {
        log.info("Buscando información pública de la producción con código: {}", codigoProduccion);
        return produccionRepository.findProduccionPublicByCodigoProduccion(codigoProduccion)
                .orElseThrow(() -> {
                    log.error("Producción pública no encontrada con código: {}", codigoProduccion);
                    return new RecursoNoEncontradoException("Producción no encontrada con código: " + codigoProduccion);
                });
    }

    private List<ProduccionModel> buscarProduccionesPorFiltros(ProduccionFilterRequestDTO filtros) {
        log.debug("Procesando filtros de búsqueda para producciones.");
        LocalDateTime fechaInicio = produccionQueryServiceValidator.convertToStartOfDay(filtros.fechaInicio());
        LocalDateTime fechaFin = produccionQueryServiceValidator.convertToEndOfDay(filtros.fechaFin());

        TipoEstadoProduccion estado = null;
        if (filtros.estado() != null) {
            try {
                estado = TipoEstadoProduccion.valueOf(filtros.estado().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Estado de producción inválido en filtro: {}", filtros.estado());
                // Podríamos lanzar excepción o ignorar el filtro de estado
            }
        }

        return produccionRepository.findByAdvancedFilters(
                filtros.codigoVersionReceta(),
                filtros.lote(),
                filtros.encargado(),
                estado,
                fechaInicio,
                fechaFin
        );
    }

    /**
     * Verifica si existen producciones asociadas a una versión de receta específica.
     *
     * @param codigoReceta Código de la versión de receta.
     * @return true si existen producciones, false en caso contrario.
     */
    @Override
    public boolean existsByVersionRecetaPadre(String codigoReceta) {
        log.debug("Verificando existencia de producciones para la versión de receta: {}", codigoReceta);
        boolean exists = produccionRepository.existsByVersionReceta_CodigoVersionReceta(codigoReceta);
        log.debug("Existen producciones para versión {}: {}", codigoReceta, exists);
        return exists;
    }

    /**
     * Verifica si existe una producción con un código específico.
     *
     * @param codigoProduccion Código de la producción.
     * @return true si existe, false en caso contrario.
     */
    @Override
    public boolean existsByCodigoProduccion(String codigoProduccion) {
        log.debug("Verificando existencia de producción con código: {}", codigoProduccion);
        return produccionRepository.existsByCodigoProduccion(codigoProduccion);
    }
}
