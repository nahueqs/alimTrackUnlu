package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.ProduccionCreateDTO;
import com.unlu.alimtrack.dtos.modify.ProduccionCambioEstadoRequestDTO;
import com.unlu.alimtrack.dtos.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.dtos.response.ProduccionResponseDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.OperacionNoPermitida;
import com.unlu.alimtrack.exception.RecursoDuplicadoException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.queries.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.ProduccionValidator;
import jakarta.validation.Valid;
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
  private final UsuarioQueryService usuarioQueryService;
  private final VersionRecetaQueryService versionRecetaQueryService;

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
    return produccionMapper.modelToResponseDTO(model);
  }

  public List<ProduccionResponseDTO> findAllByFilters(@Valid ProduccionFilterRequestDTO filtros) {

    // asigno fechas
    LocalDateTime fechaInicio = produccionValidator.convertToStartOfDay(filtros.fechaInicio());
    LocalDateTime fechaFin = produccionValidator.convertToEndOfDay(filtros.fechaFin());

    List<ProduccionModel> producciones = hacerConsultaAvanzada(
        filtros.codigoVersionReceta(),
        filtros.lote(),
        filtros.encargado(),
        fechaInicio,
        fechaFin,
        filtros.estado()
    );

    return produccionMapper.modelListToResponseDTOList(producciones);
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

  private void verificarCreacionProduccion(String codigoProduccion, ProduccionCreateDTO createDTO) {
    verificarIntegridadDatosCreacion(codigoProduccion, createDTO);
    verificarCodigoProduccionNoExiste(codigoProduccion);
    verificarVersionExiste(createDTO.codigoVersionReceta());
    verificarUsuarioExisteYEstaActivo(createDTO.usernameCreador());

  }

  private void verificarIntegridadDatosCreacion(String codigoProduccion, ProduccionCreateDTO createDTO) {
    if (!codigoProduccion.equals(createDTO.codigoProduccion())) {
      throw new ModificacionInvalidaException("El codigo de la url no coincide con el cuerpo de la petición.");
    }
  }

  private void verificarCodigoProduccionNoExiste(String codigoProduccion) {
    if (codigoProduccion.equals(produccionRepository.existsByCodigoProduccion(codigoProduccion))) {
      throw new RecursoDuplicadoException("El codigo de la producción que desea agregar ya ha sido usado.");
    }
  }

  private void verificarVersionExiste(String codigoVersion) {
    if (!versionRecetaQueryService.existsByCodigoVersion(codigoVersion)) {
      throw new ModificacionInvalidaException(
          "La producción que desea agregar no corresponde a una version existente.");
    }
  }

  private void verificarUsuarioExisteYEstaActivo(String username) {
    if (!usuarioQueryService.existsByUsername(username)) {
      throw new RecursoNoEncontradoException("Usuario no existe con id: " + username);
    }
    if (!usuarioQueryService.estaActivoByUsername(username)) {
      throw new OperacionNoPermitida(
          "El usuario que intenta guardar la producción se encuentra inactivo. username: " + username);
    }

  }

  public ProduccionResponseDTO saveProduccion(String codigoProduccion, ProduccionCreateDTO createDTO) {

    // verifico la que el cuerpo del dto coincida con la url de la peticion
    // verifico que no exista una produccion con el mismo codigo
    // verifico que exista el usuario creador
    // verifico que la version padre exista
    verificarCreacionProduccion(codigoProduccion,  createDTO);
    ProduccionModel modelFinal = produccionMapper.createDTOtoModel(createDTO);
    produccionRepository.save(modelFinal);
    return produccionMapper.modelToResponseDTO(modelFinal);
  }


}
