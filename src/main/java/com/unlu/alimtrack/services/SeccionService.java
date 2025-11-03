package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.CampoSimpleMapper;
import com.unlu.alimtrack.mappers.GrupoCamposMapper;
import com.unlu.alimtrack.mappers.SeccionMapper;
import com.unlu.alimtrack.mappers.TablaMapper;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.SeccionRepository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.queries.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.SeccionValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Servicio para la gestión de secciones
 */
@Service
public class SeccionService {

    private final SeccionRepository seccionRepository;
    private final SeccionValidator seccionValidator;
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final UsuarioService usuarioService;
    private final UsuarioQueryService usuarioQueryService;
    private final SeccionMapper seccionMapper;
    private final CampoSimpleMapper campoSimpleMapper;
    private final GrupoCamposMapper grupoCamposMapper;
    private final TablaMapper tablaMapper;
    private final VersionRecetaService versionRecetaService;

    public SeccionService(SeccionRepository seccionRepository, SeccionValidator seccionValidator, VersionRecetaQueryService versionRecetaQueryService, UsuarioService usuarioService, UsuarioQueryService usuarioQueryService, SeccionMapper seccionMapper, CampoSimpleMapper campoSimpleMapper, GrupoCamposMapper grupoCamposMapper, TablaMapper tablaMapper, @Lazy VersionRecetaService versionRecetaService) {
        this.seccionRepository = seccionRepository;
        this.seccionValidator = seccionValidator;
        this.versionRecetaQueryService = versionRecetaQueryService;
        this.usuarioService = usuarioService;
        this.usuarioQueryService = usuarioQueryService;
        this.seccionMapper = seccionMapper;
        this.campoSimpleMapper = campoSimpleMapper;
        this.grupoCamposMapper = grupoCamposMapper;
        this.tablaMapper = tablaMapper;
        this.versionRecetaService = versionRecetaService;
    }


    @Transactional
    public SeccionModel crearSeccion(String codigoReceta, SeccionCreateDTO seccionDTO) {

        // valido que exista la version
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoReceta))
            throw new RecursoNoEncontradoException("Version de receta no encontrada con codigo: " + codigoReceta);

        // valido que exista el username creador
        if (!usuarioQueryService.existsByUsername(seccionDTO.usernameCreador())) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        // valido que no exista una seccion con el mismo titulo y orden en la version receta
        // valido consistencia de datos
        seccionValidator.validarCreacionSeccion(codigoReceta, seccionDTO);

        // obtengo version model
        VersionRecetaModel versionRecetaPadre = versionRecetaService.findVersionModelByCodigo(codigoReceta);

        // obtengo usuario model
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelByUsername(seccionDTO.usernameCreador());

        // Crear la sección
        SeccionModel seccion = new SeccionModel();
        seccion.setVersionRecetaPadre(versionRecetaPadre);
        seccion.setUsernameCreador(seccionDTO.usernameCreador());
        seccion.setTitulo(seccionDTO.titulo().trim());
        seccion.setOrden(seccionDTO.orden());

        // Inicializar y llenar colecciones
        llenarCamposSimples(seccion, seccionDTO);
        llenarGrupoCampos(seccion, seccionDTO);
        llenarTablas(seccion, seccionDTO);

        return seccionRepository.save(seccion);
    }

    private void llenarCamposSimples(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.camposSimples() != null) {
            List<CampoSimpleModel> camposSimples = new ArrayList<>();
            seccion.setCamposSimples(camposSimples);
        }
    }

    private void llenarGrupoCampos(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.gruposCampos() != null) {
            List<GrupoCamposModel> gruposCampos = new ArrayList<>();
            seccion.setGruposCampos(gruposCampos);
        }
    }

    private void llenarTablas(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.tablas() != null) {
            List<TablaModel> tablas = new ArrayList<>();
            seccion.setTablas(tablas);
        }
    }


    /**
     * Crea múltiples secciones para una versión de receta
     *
     * @param codigoVersion Versión de receta a la que pertenecen las secciones
     * @param seccionesDTO  Lista de DTOs con los datos de las secciones
     * @return Lista de secciones creadas
     */
    @Transactional
    public List<SeccionModel> crearSecciones(String codigoVersion, List<SeccionCreateDTO> seccionesDTO) {
        return seccionesDTO.stream()
                .map(seccionDTO -> crearSeccion(codigoVersion, seccionDTO))
                .toList();
    }

    /**
     * Obtiene una sección por su ID con todas sus relaciones
     *
     * @param id ID de la sección
     * @return Sección encontrada
     * @throws EntityNotFoundException si la sección no existe
     */
    public SeccionModel obtenerSeccionConRelaciones(Long id) {
        return seccionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Sección no encontrada con ID: " + id));
    }

    /**
     * Obtiene todas las secciones de una versión de receta
     *
     * @param versionReceta Versión de receta
     * @return Lista de secciones
     */
    public List<SeccionModel> obtenerSeccionesPorVersion(VersionRecetaModel versionReceta) {
        return seccionRepository.findByVersionRecetaPadre(versionReceta);
    }

    public SeccionResponseDTO obtenerSeccionDTOConRelaciones(Long id) {
        return seccionMapper.toResponseDTO(
                obtenerSeccionConRelaciones(id),
                campoSimpleMapper.toResponseDTOList(obtenerSeccionConRelaciones(id).getCamposSimples()),
                grupoCamposMapper.toResponseDTOList(obtenerSeccionConRelaciones(id).getGruposCampos()),
                tablaMapper.toResponseDTOList(obtenerSeccionConRelaciones(id).getTablas())
        );
    }

    /**
     * Obtiene todas las secciones de una versión de receta
     *
     * @param codigoVersion codigo de Version Receta
     * @return Lista de secciones
     */
    public List<SeccionResponseDTO> obtenerSeccionesDTOPorVersion(String codigoVersion) {
        List<SeccionResponseDTO> secciones = seccionMapper.toResponseDTOList(obtenerSeccionesPorVersion(versionRecetaService.findVersionModelByCodigo(codigoVersion)));
        secciones.sort(Comparator.comparingInt(SeccionResponseDTO::orden));

        return secciones;
    }


//    /**
//     * Actualiza una sección existente
//     *
//     * @param id         ID de la sección a actualizar
//     * @param seccionDTO Datos actualizados
//     * @return Sección actualizada
//     */
//    @Transactional
//    public SeccionModel actualizarSeccion(Long id, String codigoVersion, SeccionCreateDTO seccionDTO) {
//        // Validar los datos de entrada
//        seccionValidator.validarActualizacionSeccion(id, versionReceta, seccionDTO);
//
//        // Obtener la sección existente
//        SeccionModel seccion = seccionRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Sección no encontrada con ID: " + id));
//
//        // Actualizar los campos
//        seccion.setTitulo(seccionDTO.getNombre().trim());
//        seccion.setDescripcion(seccionDTO.getDescripcion() != null ? seccionDTO.getDescripcion().trim() : null);
//        seccion.setOrden(seccionDTO.getOrden());
//
//        return seccionRepository.save(seccion);
//    }
//
//    /**
//     * Elimina una sección por su ID
//     *
//     * @param id ID de la sección a eliminar
//     */
//    @Transactional
//    public void eliminarSeccion(Long id) {
//        if (!seccionRepository.existsById(id)) {
//            throw new EntityNotFoundException("Sección no encontrada con ID: " + id);
//        }
//        seccionRepository.deleteById(id);
//    }


}
