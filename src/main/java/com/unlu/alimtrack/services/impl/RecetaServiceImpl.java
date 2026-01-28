package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import com.unlu.alimtrack.services.RecetaQueryService;
import com.unlu.alimtrack.services.RecetaService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.validators.RecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de recetas.
 * Maneja operaciones CRUD y validaciones relacionadas con las recetas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper;
    private final RecetaValidator recetaValidator;
    private final RecetaQueryService recetaQueryService;
    private final UsuarioService usuarioService;

    /**
     * Obtiene todas las recetas registradas en el sistema.
     *
     * @return Lista de DTOs con la metadata de las recetas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RecetaMetadataResponseDTO> findAllRecetas() {
        log.info("Obteniendo todas las recetas del sistema.");
        List<RecetaModel> recetas = recetaRepository.findAll();
        
        if (recetas.isEmpty()) {
            log.warn("No se encontraron recetas en la base de datos.");
        } else {
            recetaValidator.validateModelList(recetas);
        }
        
        log.debug("Retornando {} recetas.", recetas.size());
        return recetaMapper.recetaModelsToRecetaResponseDTOs(recetas);
    }

    /**
     * Busca una receta por su código único.
     *
     * @param codigoReceta El código de la receta a buscar.
     * @return DTO con la metadata de la receta encontrada.
     * @throws RecursoNoEncontradoException Si no se encuentra la receta.
     */
    @Override
    @Transactional(readOnly = true)
    public RecetaMetadataResponseDTO findReceta(String codigoReceta) {
        log.info("Buscando receta con código: {}", codigoReceta);
        recetaValidator.validateCodigoReceta(codigoReceta);
        RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
        log.debug("Receta {} encontrada. Mapeando a DTO.", codigoReceta);
        return recetaMapper.recetaModeltoRecetaResponseDTO(model);
    }

    /**
     * Método auxiliar para buscar el modelo de una receta.
     *
     * @param codigoReceta Código de la receta.
     * @return El modelo de la receta.
     * @throws RecursoNoEncontradoException Si la receta no existe.
     */
    private RecetaModel findRecetaModelByCodigoReceta(String codigoReceta) {
        log.debug("Buscando RecetaModel en repositorio con código: {}", codigoReceta);
        RecetaModel model = recetaRepository.findByCodigoReceta(codigoReceta);
        
        if (model == null) {
            log.error("No se encontró la receta con código: {}", codigoReceta);
            // Asumiendo que validateModel lanza la excepción si es null, o lanzándola aquí explícitamente
            // Si validateModel no lanza excepción para null, deberíamos lanzarla aquí.
            // Por seguridad, lanzamos si es null antes de validar.
             throw new RecursoNoEncontradoException("No se encontró la receta con código: " + codigoReceta);
        }
        
        recetaValidator.validateModel(model, codigoReceta);
        return model;
    }

    /**
     * Crea una nueva receta en el sistema.
     *
     * @param receta DTO con los datos de creación de la receta.
     * @return DTO con la metadata de la receta creada.
     * @throws RecursoDuplicadoException Si ya existe una receta con el mismo código.
     * @throws OperacionNoPermitida Si el usuario creador no existe o no está activo.
     */
    @Override
    @Transactional
    public RecetaMetadataResponseDTO addReceta(RecetaCreateDTO receta) {
        log.info("Iniciando creación de nueva receta con código: {}", receta.codigoReceta());
        
        try {
            verificarCreacionValida(receta.codigoReceta(), receta);
            RecetaModel model = crearModelByCreateDTO(receta);
            recetaRepository.save(model);
            log.info("Receta {} creada y guardada exitosamente.", model.getCodigoReceta());
            return recetaMapper.recetaModeltoRecetaResponseDTO(model);
        } catch (Exception e) {
            log.error("Error al crear la receta {}: {}", receta.codigoReceta(), e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza los datos de una receta existente.
     *
     * @param codigoReceta Código de la receta a actualizar.
     * @param recetaDTO DTO con los datos a modificar.
     * @return DTO con la metadata de la receta actualizada.
     * @throws RecursoNoEncontradoException Si la receta no existe.
     */
    @Override
    @Transactional
    public RecetaMetadataResponseDTO updateReceta(String codigoReceta, RecetaModifyDTO recetaDTO) {
        log.info("Actualizando receta con código: {}", codigoReceta);
        
        RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
        recetaValidator.validateDatosModification(recetaDTO);
        
        log.debug("Aplicando cambios al modelo de la receta {}.", codigoReceta);
        recetaMapper.updateModelFromModifyDTO(recetaDTO, model);
        
        recetaRepository.save(model);
        log.info("Receta {} actualizada exitosamente.", model.getCodigoReceta());
        return recetaMapper.recetaModeltoRecetaResponseDTO(model);
    }

    /**
     * Elimina una receta del sistema.
     *
     * @param codigoReceta Código de la receta a eliminar.
     * @throws BorradoFallidoException Si la receta tiene versiones asociadas y no puede ser eliminada.
     * @throws RecursoNoEncontradoException Si la receta no existe.
     */
    @Override
    @Transactional
    public void deleteReceta(String codigoReceta) {
        log.info("Intentando eliminar receta con código: {}", codigoReceta);
        
        RecetaModel receta = findRecetaModelByCodigoReceta(codigoReceta);
        validateDelete(codigoReceta);
        
        recetaRepository.delete(receta);
        log.info("Receta {} eliminada exitosamente.", codigoReceta);
    }

    private void validateDelete(String codigoReceta) {
        log.debug("Validando restricciones de borrado para la receta {}", codigoReceta);
        if (recetaQueryService.recetaTieneVersiones(codigoReceta)) {
            log.warn("Intento de eliminar receta {} fallido: tiene versiones asociadas.", codigoReceta);
            throw new BorradoFallidoException("La receta " + codigoReceta + " no puede ser eliminada ya que tiene versiones hijas existentes.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodigoReceta(String codigoReceta) {
        log.debug("Verificando existencia de receta con código: {}", codigoReceta);
        recetaValidator.validateCodigoReceta(codigoReceta);
        boolean exists = recetaRepository.existsByCodigoReceta(codigoReceta);
        log.debug("Existencia de receta {}: {}", codigoReceta, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaMetadataResponseDTO> findAllByCreadoPorEmail(String email) {
        log.info("Buscando recetas creadas por el usuario: {}", email);
        List<RecetaModel> recetas = recetaRepository.findAllByCreadoPor_Email(email);
        
        if (recetas.isEmpty()) {
            log.info("No se encontraron recetas para el usuario {}", email);
        } else {
            log.debug("Encontradas {} recetas para el usuario {}", recetas.size(), email);
        }
        
        return recetaMapper.recetaModelsToRecetaResponseDTOs(recetas);
    }

    @Override
    public RecetaModel findRecetaModelByCodigo(String codigoRecetaPadre) {
        return findRecetaModelByCodigoReceta(codigoRecetaPadre);
    }

    private void verificarCreacionValida(String codigoReceta, RecetaCreateDTO recetaCreateDTO) {
        log.debug("Validando datos para creación de receta {}", codigoReceta);
        verificarConsistenciaCodigoReceta(codigoReceta, recetaCreateDTO.codigoReceta());
        verificarUnicidadCodigoReceta(recetaCreateDTO.codigoReceta());
        verificarUsuarioExiste(recetaCreateDTO.emailCreador());
        log.debug("Validaciones exitosas para receta {}", codigoReceta);
    }

    private void verificarConsistenciaCodigoReceta(String codigoReceta, String codigoRecetaDTO) {
        if (!codigoReceta.equals(codigoRecetaDTO)) {
            log.error("Inconsistencia en códigos de receta: URL={} vs Body={}", codigoReceta, codigoRecetaDTO);
            throw new IllegalArgumentException("El código de la receta en la URL (" + codigoReceta + ") no coincide con el del cuerpo de la petición (" + codigoRecetaDTO + ")");
        }
    }

    private void verificarUnicidadCodigoReceta(String codigoReceta) {
        if (recetaRepository.existsByCodigoReceta(codigoReceta)) {
            log.warn("Intento de crear receta duplicada: {}", codigoReceta);
            throw new RecursoDuplicadoException("Ya existe una receta con el código: " + codigoReceta);
        }
    }

    private void verificarUsuarioExiste(String email) {
        if (!usuarioService.existsByEmail(email)) {
            log.warn("Intento de crear receta con usuario inexistente: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado no existe: " + email);
        }

        if (!usuarioService.estaActivoByEmail(email)) {
            log.warn("Intento de crear receta con usuario inactivo: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado no está activo: " + email);
        }
    }

    private RecetaModel crearModelByCreateDTO(RecetaCreateDTO recetaCreateDTO) {
        log.debug("Mapeando DTO a modelo para receta {}", recetaCreateDTO.codigoReceta());
        return recetaMapper.recetaCreateDTOtoModel(recetaCreateDTO);
    }
}
