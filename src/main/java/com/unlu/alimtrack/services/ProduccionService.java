package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.dtos.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.validators.ProduccionValidator;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProduccionService {

  private final ProduccionRepository produccionRepository;
  private final ProduccionMapper produccionMapper;
  private final ProduccionValidator produccionValidator;

  public ProduccionCreateDTO addProduccion(ProduccionCreateDTO productionDTO) {
    // Validar que la receta exista
    // Verificar disponibilidad de insumos
    // Calcular costos estimados
    // Crear la producción
    return null;
  }

  public ProduccionCambioEstadoRequestDTO updateEstado(Long productionId,
      ProduccionCambioEstadoRequestDTO nuevoEstado) {
    // Validar transiciones de estado válidas
    // Ej: No se puede cancelar una producción completada
    return null;
  }

  private void verificarProduccionBuscadaByCodigo(ProduccionModel produccion, String codigoProduccion) {
    if (produccion == null) {
      throw new RecursoNoEncontradoException("No se encontró la produccion codigo " + codigoProduccion);
    }
  }


  public ProduccionResponseDTO findByCodigoProduccion(String codigo) {
    ProduccionModel model = produccionRepository.findByCodigoProduccion(codigo);
    verificarProduccionBuscadaByCodigo(model, codigo);
    return produccionMapper.produccionToProduccionResponseDTO(model);
  }

  public List<ProduccionResponseDTO> findAllByFilters(@Valid ProduccionFilterRequestDTO filtros) {

    // asigno fechas
    LocalDateTime fechaInicio = convertToStartOfDay(filtros.fechaInicio());
    LocalDateTime fechaFin = convertToEndOfDay(filtros.fechaFin());

    List<ProduccionModel> producciones = hacerConsultaAvanzada(
        filtros.codigoVersionReceta(),
        filtros.lote(),
        filtros.encargado(),
        fechaInicio,
        fechaFin,
        filtros.estado()
    );

    return produccionMapper.toProduccionResponseDTOList(producciones);
  }

  private LocalDateTime convertToStartOfDay(LocalDate date) {
    return date != null ? date.atStartOfDay() : null;
  }

  private LocalDateTime convertToEndOfDay(LocalDate date) {
    return date != null ? date.atTime(23, 59, 59) : null;
  }

  private List<ProduccionModel> hacerConsultaAvanzada(
      String codigoVersionReceta,
      String lote,
      String encargado,
      LocalDateTime fechaInicio,
      LocalDateTime fechaFin,
      String estado) {

    return produccionRepository.findByAdvancedFilters(
        codigoVersionReceta, lote, encargado, estado, fechaInicio, fechaFin
    );
  }

}
