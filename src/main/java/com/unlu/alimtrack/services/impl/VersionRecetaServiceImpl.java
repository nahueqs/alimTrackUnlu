package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.DTOS.create.VersionRecetaLlenaCreateDTO;
import com.unlu.alimtrack.DTOS.modify.VersionRecetaModifyDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.protegido.VersionMetadataResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionEstructuraPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.VersionMetadataPublicResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.*;
import com.unlu.alimtrack.mappers.PublicMapper;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.*;
import com.unlu.alimtrack.services.validators.VersionRecetaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación unificada del servicio para la gestión de versiones de recetas.
 * Maneja tanto la metadata como la estructura completa (secciones, campos, tablas).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VersionRecetaServiceImpl implements VersionRecetaService {

    private final VersionRecetaRepository versionRecetaRepository;
    private final RecetaService recetaService;
    private final VersionRecetaMapper versionRecetaMapper;
    private final VersionRecetaValidator versionRecetaValidator;
    private final ProduccionQueryService produccionQueryService;
    private final UsuarioService usuarioService;
    private final SeccionManagementService seccionManagementService;
    private final PublicMapper publicMapper;

    // --- Métodos de Metadata ---

    @Override
    @Transactional(readOnly = true)
    public List<VersionMetadataResponseDTO> findAllVersiones() {
        log.info("Obteniendo todas las versiones de recetas del sistema.");
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAll();
        
        if (versiones.isEmpty()) {
            log.warn("No se encontraron versiones de recetas en la base de datos.");
            throw new RecursoNoEncontradoException("No se encontraron versiones de recetas.");
        }
        
        log.debug("Retornando {} versiones de recetas.", versiones.size());
        return versionRecetaMapper.toMetadataResponseDTOList(versiones);
    }

    @Override
    @Transactional(readOnly = true)
    public VersionMetadataResponseDTO findByCodigoVersion(String codigoVersion) {
        log.info("Buscando versión de receta con código: {}", codigoVersion);
        VersionRecetaModel model = findVersionModelByCodigo(codigoVersion);
        log.debug("Versión de receta {} encontrada. Mapeando a DTO.", codigoVersion);
        return versionRecetaMapper.toMetadataResponseDTO(model);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VersionMetadataResponseDTO> findAllByCodigoReceta(String codigoRecetaPadre) {
        log.info("Buscando todas las versiones para la receta padre: {}", codigoRecetaPadre);
        List<VersionRecetaModel> versiones = versionRecetaRepository.findAllVersionesByCodigoRecetaPadre(codigoRecetaPadre);
        
        if (versiones.isEmpty()) {
            log.warn("No se encontraron versiones para la receta {}", codigoRecetaPadre);
            throw new RecursoNoEncontradoException("No se encontraron versiones para la receta con código " + codigoRecetaPadre);
        }
        
        log.debug("Retornando {} versiones para la receta {}", versiones.size(), codigoRecetaPadre);
        return versionRecetaMapper.toMetadataResponseDTOList(versiones);
    }

    @Override
    @Transactional
    public VersionMetadataResponseDTO saveVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDTO) {
        log.info("Iniciando creación de nueva versión de receta para la receta padre: {}", codigoRecetaPadre);
        
        verificarCreacionVersionReceta(codigoRecetaPadre, versionRecetaCreateDTO);

        VersionRecetaModel versionModel = versionRecetaMapper.toModel(versionRecetaCreateDTO);
        RecetaModel recetaPadre = recetaService.findRecetaModelByCodigo(codigoRecetaPadre);
        UsuarioModel creador = usuarioService.getUsuarioModelByEmail(versionRecetaCreateDTO.emailCreador());
        
        versionModel.setRecetaPadre(recetaPadre);
        versionModel.setCreadoPor(creador);

        VersionRecetaModel versionGuardada = versionRecetaRepository.save(versionModel);
        log.info("Versión de receta {} creada y guardada exitosamente con ID: {}", versionGuardada.getCodigoVersionReceta(), versionGuardada.getId());
        
        return versionRecetaMapper.toMetadataResponseDTO(versionGuardada);
    }

    @Override
    @Transactional
    public VersionMetadataResponseDTO saveVersionRecetaCompleta(String codigoRecetaPadre, VersionRecetaLlenaCreateDTO dto) {
        log.info("Iniciando creación de nueva versión de receta COMPLETA para la receta padre: {}", codigoRecetaPadre);

        VersionRecetaCreateDTO simpleDto = new VersionRecetaCreateDTO(
                dto.codigoRecetaPadre(),
                dto.codigoVersionReceta(),
                dto.nombre(),
                dto.descripcion(),
                dto.emailCreador()
        );
        
        verificarCreacionVersionReceta(codigoRecetaPadre, simpleDto);

        VersionRecetaModel versionModel = versionRecetaMapper.toModel(simpleDto);
        RecetaModel recetaPadre = recetaService.findRecetaModelByCodigo(codigoRecetaPadre);
        UsuarioModel creador = usuarioService.getUsuarioModelByEmail(dto.emailCreador());

        versionModel.setRecetaPadre(recetaPadre);
        versionModel.setCreadoPor(creador);

        VersionRecetaModel versionGuardada = versionRecetaRepository.save(versionModel);
        log.info("Metadata de versión {} guardada. Procediendo a crear estructura.", versionGuardada.getCodigoVersionReceta());

        if (dto.secciones() != null && !dto.secciones().isEmpty()) {
            log.debug("Procesando {} secciones para la versión {}", dto.secciones().size(), dto.codigoVersionReceta());
            dto.secciones().forEach(seccionDTO -> {
                seccionManagementService.crearSeccion(versionGuardada.getCodigoVersionReceta(), seccionDTO);
            });
        }

        log.info("Versión completa {} creada exitosamente.", versionGuardada.getCodigoVersionReceta());
        return versionRecetaMapper.toMetadataResponseDTO(versionGuardada);
    }

    @Override
    @Transactional
    public VersionMetadataResponseDTO updateVersionReceta(String codigoVersion, VersionRecetaModifyDTO modificacion) {
        log.info("Actualizando versión de receta con código: {}", codigoVersion);
        
        versionRecetaValidator.validateModification(modificacion);
        VersionRecetaModel model = findVersionModelByCodigo(codigoVersion);
        
        log.debug("Aplicando cambios al modelo de la versión {}.", codigoVersion);
        versionRecetaMapper.updateModelFromModifyDTO(modificacion, model);
        
        versionRecetaRepository.save(model);
        log.info("Versión de receta {} actualizada exitosamente.", model.getCodigoVersionReceta());
        
        return versionRecetaMapper.toMetadataResponseDTO(model);
    }

    @Override
    @Transactional
    public void deleteVersionReceta(String codigoVersion) {
        log.info("Intentando eliminar la versión de receta con código: {}", codigoVersion);
        
        VersionRecetaModel receta = findVersionModelByCodigo(codigoVersion);
        validateDelete(codigoVersion);
        
        versionRecetaRepository.delete(receta);
        log.info("Versión de receta {} eliminada exitosamente.", codigoVersion);
    }

    @Override
    @Transactional(readOnly = true)
    public VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta) {
        log.debug("Buscando VersionRecetaModel en repositorio con código: {}", codigoVersionReceta);
        return versionRecetaRepository.findByCodigoVersionReceta(codigoVersionReceta)
                .orElseThrow(() -> {
                    log.error("Versión de receta no encontrada con código: {}", codigoVersionReceta);
                    return new RecursoNoEncontradoException("No existe ninguna versión de receta con el código " + codigoVersionReceta);
                });
    }

    // --- Métodos de Estructura ---

    @Override
    @Transactional(readOnly = true)
    public VersionEstructuraPublicResponseDTO getVersionRecetaCompletaResponseDTOByCodigo(String codigoVersion) {
        log.info("Iniciando recuperación de estructura completa para la versión de receta: {}", codigoVersion);
        
        // 1. Obtener metadatos básicos (Reutilizamos el método interno)
        VersionMetadataPublicResponseDTO metadata = publicMapper.metadataVersionToPublicDTO(
                findByCodigoVersion(codigoVersion)
        );
        log.debug("Metadatos recuperados para versión: {}", codigoVersion);

        // 2. Obtener estructura detallada (secciones)
        List<SeccionResponseDTO> seccionesCompletas = seccionManagementService.obtenerSeccionesDTOCompletasPorVersion(codigoVersion);
        log.debug("Secciones recuperadas: {}", seccionesCompletas.size());

        // 3. Calcular totales
        Integer totalCampos = seccionManagementService.getCantidadCampos(seccionesCompletas);
        Integer totalCeldas = seccionManagementService.getCantidadCeldasTablas(seccionesCompletas);
        log.debug("Totales calculados - Campos: {}, Celdas: {}", totalCampos, totalCeldas);

        log.info("Estructura completa de versión {} construida exitosamente.", codigoVersion);

        return new VersionEstructuraPublicResponseDTO(
                metadata,
                seccionesCompletas,
                totalCampos,
                totalCeldas
        );
    }

    // --- Validaciones Privadas ---

    private void verificarCreacionVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO dto) {
        log.debug("Validando datos para creación de versión de receta {}", dto.codigoVersionReceta());
        verificarIntegridadDatosCreacion(codigoRecetaPadre, dto);
        verificarVersionRepetida(dto.codigoVersionReceta());
        verificarRecetaExistente(dto.codigoRecetaPadre());
        verificarUsuarioExiste(dto.emailCreador());
        log.debug("Validaciones exitosas para versión {}", dto.codigoVersionReceta());
    }

    private void verificarIntegridadDatosCreacion(String codigoRecetaPadre, VersionRecetaCreateDTO dto) {
        if (!codigoRecetaPadre.equals(dto.codigoRecetaPadre())) {
            log.error("Inconsistencia en códigos de receta padre: URL={} vs Body={}", codigoRecetaPadre, dto.codigoRecetaPadre());
            throw new ModificacionInvalidaException("El código de la receta en la URL (" + codigoRecetaPadre + ") no coincide con el del cuerpo de la petición (" + dto.codigoRecetaPadre() + ")");
        }
    }

    private void verificarRecetaExistente(String codigoReceta) {
        if (!recetaService.existsByCodigoReceta(codigoReceta)) {
            log.warn("Intento de crear versión para receta inexistente: {}", codigoReceta);
            throw new RecursoNoEncontradoException("La receta padre especificada no existe: " + codigoReceta);
        }
    }

    private void verificarUsuarioExiste(String email) {
        if (!usuarioService.existsByEmail(email)) {
            log.warn("Intento de crear versión con usuario inexistente: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado no existe: " + email);
        }
        if (!usuarioService.estaActivoByEmail(email)) {
            log.warn("Intento de crear versión con usuario inactivo: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado no está activo: " + email);
        }
    }

    private void verificarVersionRepetida(String codigoVersion) {
        if (versionRecetaRepository.existsByCodigoVersionReceta(codigoVersion)) {
            log.warn("Intento de crear versión duplicada: {}", codigoVersion);
            throw new RecursoDuplicadoException("Ya existe una versión de receta con el código " + codigoVersion);
        }
    }

    private void validateDelete(String codigoVersion) {
        log.debug("Validando restricciones de borrado para la versión {}", codigoVersion);
        if (produccionQueryService.existsByVersionRecetaPadre(codigoVersion)) {
            log.warn("Intento de eliminar versión {} fallido: tiene producciones asociadas.", codigoVersion);
            throw new BorradoFallidoException("La versión " + codigoVersion + " tiene producciones asociadas y no puede ser eliminada.");
        }
    }
}
