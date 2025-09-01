package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionModelMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.helpers.DateHelper;
import com.unlu.alimtrack.services.validators.ProduccionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduccionService {
    private final RecetaService recetaService;
    private final VersionRecetaService versionRecetaService;
    private final ProduccionRepository produccionRepository;
    private final ProduccionModelMapper produccionModelMapper;
    private final DateHelper dateHelper;
    private final ProduccionValidator produccionValidator;

    public ProduccionCreateDTO addProduccion(ProduccionCreateDTO productionDTO) {
        // Validar que la receta exista
        // Verificar disponibilidad de insumos
        // Calcular costos estimados
        // Crear la producción
        return null;
    }

    public ProduccionCambioEstadoRequestDTO updateEstado(Long productionId, ProduccionCambioEstadoRequestDTO nuevoEstado) {
        // Validar transiciones de estado válidas
        // Ej: No se puede cancelar una producción completada
        return null;
    }

    public ProduccionResponseDTO findByCodigoProduccion(String codigo) {
        ProduccionModel model = produccionRepository.findByCodigoProduccion(codigo);
        if (model == null) {
            throw new RecursoNoEncontradoException("No se encontró la produccion codigo " + codigo);
        }
        return produccionModelMapper.produccionToProduccionResponseDTO(model);
    }

    public List<ProduccionResponseDTO> findAllProduccionesEnCurso() {
        List<ProduccionModel> producciones = produccionRepository.findAllByEstado(TipoEstadoProduccion.EN_CURSO.getValorBaseDatos());

        if (producciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron producciones en curso");
        }

        return producciones.stream().map(produccionModelMapper::produccionToProduccionResponseDTO).collect(Collectors.toList());
    }

    public List<ProduccionResponseDTO> findAllProduccionesFinalizadas() {
        List<ProduccionModel> producciones = produccionRepository.findAllByEstado(TipoEstadoProduccion.FINALIZADA.getValorBaseDatos());

        if (producciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron producciones finalizadas");
        }

        return producciones.stream().map(produccionModelMapper::produccionToProduccionResponseDTO).collect(Collectors.toList());
    }

    public List<ProduccionResponseDTO> findAllProduccionesByCodigoReceta(String codigoReceta) {

        log.debug("Buscando versiones para receta: {}", codigoReceta);

        // obtener todas las versiones de la receta
        List<VersionRecetaModel> versiones = versionRecetaService.findAllVersionesByCodigoRecetaPadre(codigoReceta);

        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron versiones para la receta: " + codigoReceta);
        }
        List<ProduccionResponseDTO> producciones = new ArrayList<>();
        for (VersionRecetaModel version : versiones) {
            try {
                producciones = findAllProduccionesByCodigoVersionReceta(version.getCodigoVersionReceta());
            } catch (Exception e) {
                log.error("Error obteniendo producciones para versión {}: {}",
                        version.getCodigoVersionReceta(), e.getMessage());
            }
        }

        if (producciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay producciones para la receta " + codigoReceta);
        }

        return producciones;
    }

    public List<ProduccionResponseDTO> findAllProduccionesByCodigoVersionReceta(String codigoVersionReceta) {
        VersionRecetaModel version = versionRecetaService.findVersionModelByCodigo(codigoVersionReceta);
        if (version == null) {
            throw new RecursoNoEncontradoException("No existe ninguna version con el codigo " + codigoVersionReceta);
        }
        List<ProduccionModel> producciones = produccionRepository.findAllByVersionReceta(version);
        if (producciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron producciones para el codigo de version receta " + codigoVersionReceta);
        }
        return convertToResponseDTOList(producciones);
    }

    /* * Busca producciones por versión de receta y rango de fechas
     * @param codigoVersionReceta Código de versión de receta
     * @param fechaInicioStr Fecha de inicio mínima
     * @param fechaFinStr Fecha de inicio máxima
     * @return Lista de producciones que cumplen ambos criterios
     */
    public List<ProduccionResponseDTO> findByVersionRecetaAndFechaRange(String codigoVersionReceta, String fechaInicioStr, String fechaFinStr) {

        Instant fechaInicio = dateHelper.parseAndValidateFecha(fechaInicioStr);
        Instant fechaFin = dateHelper.parseAndValidateFecha(fechaFinStr);

        System.out.println("Buscando producciones por versión {} y rango {} - {}" + codigoVersionReceta + fechaInicio + fechaFin);

        return convertToResponseDTOList(produccionRepository.findByVersionRecetaAndFechaRange(codigoVersionReceta, fechaInicio, fechaFin));
    }

    // convierte una lista de models a otra de responseDTO
    private List<ProduccionResponseDTO> convertToResponseDTOList(List<ProduccionModel> producciones) {
        return producciones.stream().map(produccionModelMapper::produccionToProduccionResponseDTO).collect(Collectors.toList());
    }

    /*
     * Obtiene todas las producciones con filtros opcionales
     * Método principal para búsquedas flexibles
     *
     * @param codigoVersionReceta Filtro por código de versión (opcional)
     * @param lote Filtro por número de lote (opcional)
     * @param encargado Filtro por encargado (opcional)
     * @param fechaInicioStr Filtro por fecha inicio mínima (opcional)
     * @param fechaFinStr Filtro por fecha inicio máxima (opcional)
     * @return Lista de producciones filtradas como DTOs
     * @throws IllegalArgumentException Si el formato de fecha es inválido
     */
    public List<ProduccionResponseDTO> findProduccionesByFilters(String codigoVersionReceta, String lote, String encargado, String fechaInicioStr, String fechaFinStr) {

        log.debug("Buscando producciones con filtros: codigoVersionReceta={}, lote={}, encargado={}, fechaInicio={}, fechaFin={}", codigoVersionReceta, lote, encargado, fechaInicioStr, fechaFinStr);

        // valido fechas
        Instant fechaInicio = dateHelper.parseAndValidateFecha(fechaInicioStr);
        Instant fechaFin = dateHelper.parseAndValidateFecha(fechaFinStr);
        dateHelper.validateRangoFechas(fechaInicio, fechaFin, "fechaInicio", "fechaFin");

        //valido las referencias sino tira exception RecursoNoEncontrado
        produccionValidator.validateReferencias(codigoVersionReceta, lote, encargado);

        List<ProduccionModel> producciones = produccionRepository.findByAdvancedFilters(codigoVersionReceta, lote, encargado, fechaInicio, fechaFin);

        return convertToResponseDTOList(producciones);
    }
}
