package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionModelMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduccionService {
    private final ProduccionService produccionService;
    private final RecetaService recetaService;
    private final ProduccionRepository produccionRepository;
    private final ProduccionModelMapper produccionModelMapper;

    public ProduccionService(@Lazy ProduccionService produccionService, @Lazy RecetaService recetaService, ProduccionRepository produccionRepository, ProduccionModelMapper produccionModelMapper) {
        this.produccionService = produccionService;
        this.recetaService = recetaService;
        this.produccionRepository = produccionRepository;
        this.produccionModelMapper = produccionModelMapper;
    }

    public ProduccionCreateDTO crearProduccion(ProduccionCreateDTO productionDTO) {
        // Validar que la receta exista
        // Verificar disponibilidad de insumos
        // Calcular costos estimados
        // Crear la producción
        return null;
    }

    public ProduccionCambioEstadoRequestDTO actualizarEstado(Long productionId, ProduccionCambioEstadoRequestDTO nuevoEstado) {
        // Validar transiciones de estado válidas
        // Ej: No se puede cancelar una producción completada
        return null;
    }

    public List<ProduccionResponseDTO> obtenerProduccionesPorFecha(Date fechaInicio, Date fechaFin) {
        // Lógica para filtrar por rango de fechas
        return null;
    }

    public List<ProduccionResponseDTO> getAllProducciones() {
        List<ProduccionModel> producciones =  produccionRepository.findAll();
        if (producciones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron producciones");
        }

        return producciones.stream().map(
                produccionModelMapper::produccionToProduccionResponseDTO).collect(Collectors.toList());
    }

    public ProduccionResponseDTO getByCodigoProduccion(String codigo) {
        ProduccionModel model = produccionRepository.findByCodigoProduccion(codigo);
        if (model == null) {
            throw new RecursoNoEncontradoException("No se encontró la produccion codigo " + codigo);
        }
        return  produccionModelMapper.produccionToProduccionResponseDTO(model);

    }
}