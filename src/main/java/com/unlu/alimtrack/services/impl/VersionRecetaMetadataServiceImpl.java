package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMetadataMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.RecetaService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VersionRecetaMetadataServiceImpl implements VersionRecetaMetadataService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final RecetaService recetaService;
    private final VersionRecetaMetadataMapper versionRecetaMetadataMapper;
    private final VersionRecetaValidator versionRecetaValidator;
    private final ProduccionQueryService produccionQueryService;
    private final UsuarioService usuarioService;

    @Override
    @Transactional(readOnly = true)
    public List<VersionRecetaMetadataResponseDTO> findAllVersiones() {
        log.info("Obteniendo todas las versiones de recetas");
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAll();
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron versiones de recetas.");
        }
        log.debug("Retornando {} versiones de recetas", versiones.size());
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTOList(versiones);
    }

    @Override
    @Transactional(readOnly = true)
    public VersionRecetaMetadataResponseDTO findByCodigoVersion(String codigoVersion) {
        log.info("Buscando versión de receta con código: {}", codigoVersion);
        VersionRecetaModel model = findVersionModelByCodigo(codigoVersion);
        log.debug("Versión de receta {} encontrada. Mapeando a DTO.", codigoVersion);
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTO(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersionRecetaMetadataResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre) {
        log.info("Buscando todas las versiones para la receta con código: {}", codigoRecetaPadre);
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAllVersionesByCodigoRecetaPadre(codigoRecetaPadre);
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron versiones para la receta con código " + codigoRecetaPadre);
        }
        log.debug("Retornando {} versiones para la receta {}", versiones.size(), codigoRecetaPadre);
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTOList(versiones);
    }

    @Override
    @Transactional
    @CacheEvict(value = "versionRecetaEstructura", key = "#versionRecetaCreateDTO.codigoVersionReceta()")
    // Invalida la caché de la estructura
    public VersionRecetaMetadataResponseDTO saveVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDTO) {
        log.info("Creando nueva versión de receta para la receta padre: {}", codigoRecetaPadre);
        verificarCreacionVersionReceta(codigoRecetaPadre, versionRecetaCreateDTO);
        VersionRecetaModel versionModelFinal = versionRecetaMetadataMapper.toVersionRecetaModel(versionRecetaCreateDTO);
        versionRecetaRepository.save(versionModelFinal);
        log.info("Versión de receta {} creada y guardada exitosamente", versionModelFinal.getCodigoVersionReceta());
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTO(versionModelFinal);
    }

    @Override
    @Transactional
    @CacheEvict(value = "versionRecetaEstructura", key = "#codigoVersion") // Invalida la caché de la estructura
    public VersionRecetaMetadataResponseDTO updateVersionReceta(String codigoVersion, VersionRecetaModifyDTO modificacion) {
        log.info("Actualizando versión de receta con código: {}", codigoVersion);
        versionRecetaValidator.validateModification(modificacion);
        VersionRecetaModel model = findVersionModelByCodigo(codigoVersion);
        versionRecetaMetadataMapper.updateModelFromModifyDTO(modificacion, model);
        versionRecetaRepository.save(model);
        log.info("Versión de receta {} actualizada exitosamente", model.getCodigoVersionReceta());
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTO(model);
    }

    @Override
    @Transactional
    @CacheEvict(value = "versionRecetaEstructura", key = "#codigoVersion") // Invalida la caché de la estructura
    public void deleteVersionReceta(String codigoVersion) {
        log.info("Intentando eliminar la versión de receta con código: {}", codigoVersion);
        VersionRecetaModel receta = findVersionModelByCodigo(codigoVersion);
        validateDelete(codigoVersion);
        versionRecetaRepository.delete(receta);
        log.info("Versión de receta {} eliminada exitosamente", codigoVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta) {
        log.debug("Buscando VersionRecetaModel con código: {}", codigoVersionReceta);
        return versionRecetaRepository.findByCodigoVersionReceta(codigoVersionReceta)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ninguna versión de receta con el código " + codigoVersionReceta));
    }

    private void verificarCreacionVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO dto) {
        log.debug("Iniciando validaciones para la creación de la versión de receta {}", dto.codigoVersionReceta());
        verificarIntegridadDatosCreacion(codigoRecetaPadre, dto);
        verificarVersionRepetida(dto.codigoVersionReceta());
        verificarRecetaExistente(dto.codigoRecetaPadre());
        verificarUsuarioExiste(dto.emailCreador());
        log.debug("Validaciones para la creación de la versión {} superadas", dto.codigoVersionReceta());
    }


    private void verificarIntegridadDatosCreacion(String codigoRecetaPadre, VersionRecetaCreateDTO dto) {
        if (!codigoRecetaPadre.equals(dto.codigoRecetaPadre())) {
            throw new ModificacionInvalidaException("El código de la receta en la URL (" + codigoRecetaPadre + ") no coincide con el del cuerpo de la petición (" + dto.codigoRecetaPadre() + ")");
        }
    }

    private void verificarRecetaExistente(String codigoReceta) {
        if (!recetaService.existsByCodigoReceta(codigoReceta)) {
            throw new RecursoNoEncontradoException("La receta padre especificada no existe: " + codigoReceta);
        }
    }

    private void verificarUsuarioExiste(String email) {
        if (!usuarioService.existsByEmail(email)) {
            throw new RecursoNoEncontradoException("El usuario creador especificado no existe: " + email);
        }
    }

    private void verificarVersionRepetida(String codigoVersion) {
        if (versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion)) {
            throw new RecursoDuplicadoException("Ya existe una versión de receta con el código " + codigoVersion);
        }
    }

    private void validateDelete(String codigoVersion) {
        log.debug("Validando si la versión de receta {} puede ser eliminada", codigoVersion);
        if (produccionQueryService.existsByVersionRecetaPadre(codigoVersion)) {
            throw new BorradoFallidoException("La versión " + codigoVersion + " tiene producciones asociadas y no puede ser eliminada.");
        }
    }
}
