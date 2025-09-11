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
  private final VersionRecetaQueryService versionRecetaQueryService;
  private final UsuarioQueryService usuarioQueryService;


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
//    VersionRecetaCreateDTO versionRecetaCreateDTO) {
//      // verifico la que el cuerpo coincida con la url de la peticion
//      // verifico que no exista una produccion con el mismo codigo
//      // verifico que exista el usuario creador
    // verifico que la version padre exista

//      verificarCreacionVersionReceta(codigoRecetaPadre, versionRecetaCreateDTO);
//
//      // mapeo el dto a un nuevo model
//      VersionRecetaModel versionModelFinal = versionRecetaMapper.toVersionRecetaModel(
//          versionRecetaCreateDTO);
//
//      versionRecetaRespository.save(versionModelFinal);
//
//      return versionRecetaMapper.toVersionRecetaResponseDTO(versionModelFinal);
    return null;
  }


}
