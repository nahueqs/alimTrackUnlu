package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.RecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.RecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.Receta.RecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final RecetaMapper recetaMapper;
    private final RecetaValidator recetaValidator;
    private final RecetaQueryService recetaQueryService;
    private final UsuarioService usuarioService;

    @Override
    @Transactional(readOnly = true)
    public List<RecetaMetadataResponseDTO> findAllRecetas() {
        log.info("Obteniendo todas las recetas");
        List<RecetaModel> recetas = recetaRepository.findAll();
        recetaValidator.validateModelList(recetas);
        log.debug("Retornando {} recetas", recetas.size());
        return recetaMapper.recetaModelsToRecetaResponseDTOs(recetas);
    }

    @Override
    @Transactional(readOnly = true)
    public RecetaMetadataResponseDTO findReceta(String codigoReceta) {
        log.info("Buscando receta con código: {}", codigoReceta);
        recetaValidator.validateCodigoReceta(codigoReceta);
        RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
        log.debug("Receta {} encontrada. Mapeando a DTO.", codigoReceta);
        return recetaMapper.recetaModeltoRecetaResponseDTO(model);
    }

    private RecetaModel findRecetaModelByCodigoReceta(String codigoReceta) {
        log.debug("Buscando RecetaModel con código: {}", codigoReceta);
        RecetaModel model = recetaRepository.findByCodigoReceta(codigoReceta);
        recetaValidator.validateModel(model, codigoReceta);
        return model;
    }

    @Override
    @Transactional
    public RecetaMetadataResponseDTO addReceta(RecetaCreateDTO receta) {
        log.info("Creando nueva receta con código: {}", receta.codigoReceta());
        verificarCreacionValida(receta.codigoReceta(), receta);
        RecetaModel model = crearModelByCreateDTO(receta);
        recetaRepository.save(model);
        log.info("Receta {} creada y guardada exitosamente", model.getCodigoReceta());
        return recetaMapper.recetaModeltoRecetaResponseDTO(model);
    }

    @Override
    @Transactional
    public RecetaMetadataResponseDTO updateReceta(String codigoReceta, RecetaModifyDTO recetaDTO) {
        log.info("Actualizando receta con código: {}", codigoReceta);
        RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
        recetaValidator.validateDatosModification(recetaDTO);
        recetaMapper.updateModelFromModifyDTO(recetaDTO, model);
        recetaRepository.save(model);
        log.info("Receta {} actualizada exitosamente", model.getCodigoReceta());
        return recetaMapper.recetaModeltoRecetaResponseDTO(model);
    }

    @Override
    @Transactional
    public void deleteReceta(String codigoReceta) {
        log.info("Intentando eliminar receta con código: {}", codigoReceta);
        RecetaModel receta = findRecetaModelByCodigoReceta(codigoReceta);
        validateDelete(codigoReceta);
        recetaRepository.delete(receta);
        log.info("Receta {} eliminada exitosamente", codigoReceta);
    }

    private void validateDelete(String codigoReceta) {
        log.debug("Validando si la receta {} puede ser eliminada", codigoReceta);
        if (recetaQueryService.recetaTieneVersiones(codigoReceta)) {
            throw new BorradoFallidoException("La receta " + codigoReceta + " no puede ser eliminada ya que tiene versiones hijas existentes.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodigoReceta(String codigoReceta) {
        log.debug("Verificando si existe una receta con código: {}", codigoReceta);
        recetaValidator.validateCodigoReceta(codigoReceta);
        return recetaRepository.existsByCodigoReceta(codigoReceta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecetaMetadataResponseDTO> findAllByCreadoPorEmail(String email) {
        log.info("Buscando todas las recetas creadas por el usuario: {}", email);
        List<RecetaModel> recetas = recetaRepository.findAllByCreadoPor_Email(email);
        log.debug("Encontradas {} recetas para el usuario {}", recetas.size(), email);
        return recetaMapper.recetaModelsToRecetaResponseDTOs(recetas);
    }

    @Override
    public RecetaModel findRecetaModelByCodigo(String codigoRecetaPadre) {
        return findRecetaModelByCodigoReceta(codigoRecetaPadre);
    }

    private void verificarCreacionValida(String codigoReceta, RecetaCreateDTO recetaCreateDTO) {
        log.debug("Iniciando validaciones para la creación de la receta {}", codigoReceta);
        verificarConsistenciaCodigoReceta(codigoReceta, recetaCreateDTO.codigoReceta());
        verificarUnicidadCodigoReceta(recetaCreateDTO.codigoReceta());
        verificarUsuarioExiste(recetaCreateDTO.emailCreador());
        log.debug("Validaciones para la creación de la receta {} superadas", codigoReceta);
    }

    private void verificarConsistenciaCodigoReceta(String codigoReceta, String codigoRecetaDTO) {
        if (!codigoReceta.equals(codigoRecetaDTO)) {
            throw new IllegalArgumentException("El código de la receta en la URL (" + codigoReceta + ") no coincide con el del cuerpo de la petición (" + codigoRecetaDTO + ")");
        }
    }

    private void verificarUnicidadCodigoReceta(String codigoReceta) {
        if (recetaRepository.existsByCodigoReceta(codigoReceta)) {
            throw new RecursoDuplicadoException("Ya existe una receta con el código: " + codigoReceta);
        }
    }

    private void verificarUsuarioExiste(String email) {
        if (!usuarioService.existsByEmail(email)) {
            throw new OperacionNoPermitida("El usuario creador especificado no existe: " + email);
        }

        if (!usuarioService.estaActivoByEmail(email)) {
            throw new OperacionNoPermitida("El usuario creador especificado no está activo: " + email);
        }


    }

    private RecetaModel crearModelByCreateDTO(RecetaCreateDTO recetaCreateDTO) {
        log.debug("Mapeando RecetaCreateDTO a RecetaModel para {}", recetaCreateDTO.codigoReceta());
        return recetaMapper.recetaCreateDTOtoModel(recetaCreateDTO);
    }
}
