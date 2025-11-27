package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.produccion.publico.ProduccionEstadoPublicaResponseDTO;
import com.unlu.alimtrack.DTOS.response.produccion.protegido.ProduccionMetadataResponseDTO;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProduccionQueryServiceImpl implements ProduccionQueryService {

    private final ProduccionRepository produccionRepository;
    private final ProduccionMapper produccionMapper;
    private final ProduccionQueryServiceValidator produccionQueryServiceValidator;

    @Override
    @Cacheable(value = "produccionByCodigo", key = "#codigo")
    public ProduccionMetadataResponseDTO findByCodigoProduccion(String codigo) {
        log.info("Buscando producción con código: {}", codigo);
        ProduccionModel model = produccionRepository.findByCodigoProduccion(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada con código: " + codigo));
        log.debug("Producción {} encontrada. Mapeando a DTO.", codigo);
        return produccionMapper.modelToResponseDTO(model);
    }

    @Override
    @Cacheable(value = "produccionesList", key = "#filtros.hashCode()") // Cachea la lista de producciones por filtros
    public List<ProduccionMetadataResponseDTO> getAllProduccionesMetadata(@Valid ProduccionFilterRequestDTO filtros) {
        log.info("Buscando producciones con los filtros: {}", filtros);
        List<ProduccionModel> producciones = buscarProduccionesPorFiltros(filtros);
        log.debug("Encontradas {} producciones con los filtros aplicados", producciones.size());
        return produccionMapper.modelListToResponseDTOList(producciones);
    }

    @Override
    @Cacheable(value = "produccionPublic", key = "#codigoProduccion")
    public ProduccionEstadoPublicaResponseDTO getProduccionPublic(String codigoProduccion) {
        log.info("Buscando información pública de la producción con código: {}", codigoProduccion);
        return produccionRepository.findProduccionPublicByCodigoProduccion(codigoProduccion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada con código: " + codigoProduccion));
    }

    private List<ProduccionModel> buscarProduccionesPorFiltros(ProduccionFilterRequestDTO filtros) {
        log.debug("Procesando y convirtiendo filtros de búsqueda");
        LocalDateTime fechaInicio = produccionQueryServiceValidator.convertToStartOfDay(filtros.fechaInicio());
        LocalDateTime fechaFin = produccionQueryServiceValidator.convertToEndOfDay(filtros.fechaFin());

        TipoEstadoProduccion estado = filtros.estado() != null
                ? TipoEstadoProduccion.valueOf(filtros.estado().toUpperCase())
                : null;

        return produccionRepository.findByAdvancedFilters(
                filtros.codigoVersionReceta(),
                filtros.lote(),
                filtros.encargado(),
                estado,
                fechaInicio,
                fechaFin
        );
    }

    @Override
    @Cacheable(value = "produccionExistsByVersion", key = "#codigoReceta")
    public boolean existsByVersionRecetaPadre(String codigoReceta) {
        log.debug("Verificando si existen producciones para la versión de receta: {}", codigoReceta);
        return produccionRepository.existsByVersionReceta_CodigoVersionReceta(codigoReceta);
    }

    @Override
    @Cacheable(value = "produccionExistsByCodigo", key = "#codigoProduccion")
    public boolean existsByCodigoProduccion(String codigoProduccion) {
        log.debug("Verificando si existe una producción con el código: {}", codigoProduccion);
        return produccionRepository.existsByCodigoProduccion(codigoProduccion);
    }
}
