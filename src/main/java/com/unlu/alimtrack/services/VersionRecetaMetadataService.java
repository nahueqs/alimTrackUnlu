package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.VersionRecetaMetadataResponseDTO;
import com.unlu.alimtrack.exceptions.BorradoFallidoException;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMetadataMapper;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.queries.ProduccionQueryService;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VersionRecetaMetadataService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final RecetaService recetaService;
    private final VersionRecetaMetadataMapper versionRecetaMetadataMapper;
    private final UsuarioQueryService usuarioQueryService;
    private final VersionRecetaValidator versionRecetaValidator;
    private final ProduccionQueryService produccionQueryService;
    @Lazy
    private final SeccionService seccionService;


    @Transactional(readOnly = true)
    public List<VersionRecetaMetadataResponseDTO> findAllVersiones() {
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAll();
        verificarListaVersionesObtenida(versiones);
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTOList(versiones);
    }

    private void verificarListaVersionesObtenida(List<VersionRecetaModel> versiones) {
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay versiones guardadas para ninguna receta");
        }
    }

    private void verificarListaVersionesObtenidaByCodigoReceta(List<VersionRecetaModel> versiones,
                                                               String codigoReceta) {
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException(
                    "No hay versiones guardadas para la receta con codigo " + codigoReceta);
        }
    }

    @Transactional(readOnly = true)
    public VersionRecetaMetadataResponseDTO findByCodigoVersion(String codigoVersion) {
        VersionRecetaModel model = versionRecetaRepository.findByCodigoVersionReceta(codigoVersion);
        verificarVersionModelNotNull(model, codigoVersion);
        return VersionRecetaMetadataMapper.mapper.toVersionRecetaResponseDTO(model);
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaMetadataResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre) {
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAllVersionesByCodigoRecetaPadre(
                codigoRecetaPadre);
        verificarListaVersionesObtenidaByCodigoReceta(versiones, codigoRecetaPadre);
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTOList(versiones);
    }

    private void verificarCreacionVersionReceta(String codigoRecetaPadre,
                                                VersionRecetaCreateDTO versionRecetaCreateDTO) {
        verificarIntegridadDatosCreacion(codigoRecetaPadre, versionRecetaCreateDTO);
        verificarVersionRepetida(versionRecetaCreateDTO.codigoVersionReceta());
        verificarRecetaExistente(versionRecetaCreateDTO.codigoRecetaPadre());
        verificarUsuarioExiste(versionRecetaCreateDTO.usernameCreador());
    }

    private void verificarIntegridadDatosCreacion(String codigoRecetaPadre,
                                                  VersionRecetaCreateDTO versionRecetaCreateDTO) {
        if (!codigoRecetaPadre.equals(versionRecetaCreateDTO.codigoRecetaPadre())) {
            throw new ModificacionInvalidaException(
                    "El código de la url no coincide con el código de receta en el cuerpo de la petición.");
        }
    }

    private void verificarRecetaExistente(String codigoReceta) {
        if (!recetaService.existsByCodigoReceta(codigoReceta)) {
            throw new RecursoNoEncontradoException("Receta no existe");
        }
    }

    private void verificarUsuarioExiste(String username) {
        if (!usuarioQueryService.existsByUsername(username)) {
            throw new RecursoNoEncontradoException(
                    "No existe un usuario registrado con el username " + username);
        }
    }

    private void verificarVersionRepetida(String codigoVersion) {
        if (versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion)) {
            throw new RecursoNoEncontradoException(
                    "Ya existe una version receta con el codigo " + codigoVersion);
        }
    }

    @Transactional
    public VersionRecetaMetadataResponseDTO saveVersionReceta(String codigoRecetaPadre,
                                                              VersionRecetaCreateDTO versionRecetaCreateDTO) {
        // verifico que no exista una version con ese codigo
        // verifico que exista la receta padre
        // verifico que exista el usuario creador
        verificarCreacionVersionReceta(codigoRecetaPadre, versionRecetaCreateDTO);
        // mapeo el dto a un nuevo model
        VersionRecetaModel versionModelFinal = versionRecetaMetadataMapper.toVersionRecetaModel(
                versionRecetaCreateDTO);
        versionRecetaRepository.save(versionModelFinal);
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTO(versionModelFinal);
    }

    private void verificarVersionModelNotNull(VersionRecetaModel model, String codigoVersion) {
        if (model == null) {
            throw new RecursoNoEncontradoException(
                    "No existe ninguna version con el codigo " + codigoVersion);
        }
    }

    public VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta) {
        VersionRecetaModel model = versionRecetaRepository.findByCodigoVersionReceta(
                codigoVersionReceta);
        verificarVersionModelNotNull(model, codigoVersionReceta);
        return model;
    }

    public VersionRecetaMetadataResponseDTO updateVersionReceta(String codigoReceta, VersionRecetaModifyDTO modificacion) {
        versionRecetaValidator.validateModification(modificacion);
        VersionRecetaModel model = findVersionModelByCodigo(codigoReceta);
        versionRecetaMetadataMapper.updateModelFromModifyDTO(modificacion, model);
        saveVersionModel(model);
        return versionRecetaMetadataMapper.toVersionRecetaResponseDTO(model);
    }

    private void saveVersionModel(VersionRecetaModel model) {
        versionRecetaRepository.save(model);
    }

    private void validateDelete(String codigoVersion) {
        validateNoTieneProduccionesHijas(codigoVersion);
    }

    private void validateNoTieneProduccionesHijas(String codigoVersion) {
        if (produccionQueryService.existsByVersionRecetaPadre(codigoVersion)) {
            throw new BorradoFallidoException(
                    "La version tiene producciones hijas existentes. codigo version: " + codigoVersion);
        }
    }

    public void deleteVersionReceta(String codigoVersion) {
        VersionRecetaModel receta = findVersionModelByCodigo(codigoVersion);
        validateDelete(codigoVersion);
        versionRecetaRepository.delete(receta);
    }


}
