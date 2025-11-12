package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.*;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.SeccionRepository;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.queries.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.SeccionValidator;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de secciones de versiones de recetas.
 * Maneja la creación y recuperación de secciones con todas sus relaciones.
 */
@Service
public class SeccionService {

    private static final Logger log = LoggerFactory.getLogger(SeccionService.class);

    // Constantes de mensajes de error
    private static final String ERROR_VERSION_NO_ENCONTRADA = "Versión de receta no encontrada con código: %s";
    private static final String ERROR_USUARIO_NO_ENCONTRADO = "Usuario no encontrado con el username: %s";
    private static final String ERROR_CODIGO_VERSION_INVALIDO = "El código de versión no puede ser nulo o vacío";

    private final SeccionRepository seccionRepository;
    private final SeccionValidator seccionValidator;
    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final UsuarioQueryService usuarioQueryService;
    private final SeccionMapper seccionMapper;
    private final CampoSimpleMapper campoSimpleMapper;
    private final GrupoCamposMapper grupoCamposMapper;
    private final TablaMapper tablaMapper;
    private final ColumnaTablaMapper columnaTablaMapper;
    private final FilaTablaMapper filaTablaMapper;


    public SeccionService(
            SeccionRepository seccionRepository,
            SeccionValidator seccionValidator,
            VersionRecetaQueryService versionRecetaQueryService,
            UsuarioQueryService usuarioQueryService,
            SeccionMapper seccionMapper,
            CampoSimpleMapper campoSimpleMapper,
            GrupoCamposMapper grupoCamposMapper,
            TablaMapper tablaMapper,
            @Lazy VersionRecetaMetadataService versionRecetaMetadataService, ColumnaTablaMapper columnaTablaMapper, FilaTablaMapper filaTablaMapper) {
        this.seccionRepository = seccionRepository;
        this.seccionValidator = seccionValidator;
        this.versionRecetaQueryService = versionRecetaQueryService;
        this.usuarioQueryService = usuarioQueryService;
        this.seccionMapper = seccionMapper;
        this.campoSimpleMapper = campoSimpleMapper;
        this.grupoCamposMapper = grupoCamposMapper;
        this.tablaMapper = tablaMapper;
        this.versionRecetaMetadataService = versionRecetaMetadataService;
        this.columnaTablaMapper = columnaTablaMapper;
        this.filaTablaMapper = filaTablaMapper;
    }

    /**
     * Crea una nueva sección para una versión de receta.
     *
     * @param codigoReceta Código de la versión de receta
     * @param seccionDTO   Datos de la sección a crear
     * @return Sección creada y persistida
     * @throws RecursoNoEncontradoException si la versión o usuario no existen
     */
    @Transactional
    public SeccionModel crearSeccion(String codigoReceta, SeccionCreateDTO seccionDTO) {
        log.debug("Creando sección para versión {} por usuario {}", codigoReceta, seccionDTO.usernameCreador());

        validarPrecondicionesCreacion(codigoReceta, seccionDTO.usernameCreador());
        seccionValidator.validarCreacionSeccion(codigoReceta, seccionDTO);

        VersionRecetaModel versionRecetaPadre = versionRecetaMetadataService.findVersionModelByCodigo(codigoReceta);

        SeccionModel seccion = new SeccionModel();
        seccion.setVersionRecetaPadre(versionRecetaPadre);
        seccion.setUsernameCreador(seccionDTO.usernameCreador());
        seccion.setTitulo(seccionDTO.titulo().trim());
        seccion.setOrden(seccionDTO.orden());

        poblarColecciones(seccion, seccionDTO);

        SeccionModel seccionGuardada = seccionRepository.save(seccion);
        log.info("Sección creada exitosamente con ID: {}", seccionGuardada.getIdSeccion());

        return seccionGuardada;
    }

    /**
     * Valida que la versión de receta y el usuario existan antes de crear la sección.
     */
    private void validarPrecondicionesCreacion(String codigoReceta, String usernameCreador) {
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoReceta)) {
            throw new RecursoNoEncontradoException(String.format(ERROR_VERSION_NO_ENCONTRADA, codigoReceta));
        }

        if (!usuarioQueryService.existsByUsername(usernameCreador)) {
            throw new RecursoNoEncontradoException(String.format(ERROR_USUARIO_NO_ENCONTRADO, usernameCreador));
        }
    }

    /**
     * Puebla todas las colecciones de la sección desde el DTO.
     */
    private void poblarColecciones(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        llenarCamposSimples(seccion, seccionDTO);
        llenarGrupoCampos(seccion, seccionDTO);
        llenarTablas(seccion, seccionDTO);
    }

    /**
     * Inicializa la colección de campos simples.
     * Si el DTO contiene campos, los mapea y asigna a la sección.
     */
    private void llenarCamposSimples(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.camposSimples() != null && !seccionDTO.camposSimples().isEmpty()) {
            List<CampoSimpleModel> camposSimples = seccionDTO.camposSimples().stream()
                    .map(dto -> {
                        CampoSimpleModel campo = campoSimpleMapper.toModel(dto);
                        campo.setSeccion(seccion);
                        campo.setGrupo(null);
                        return campo;
                    })
                    .collect(Collectors.toList());
            seccion.setCamposSimples(camposSimples);
            log.debug("Asignados {} campos simples a la sección", camposSimples.size());
        } else {
            seccion.setCamposSimples(new ArrayList<>());
        }
    }

    private void llenarGrupoCampos(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.gruposCampos() != null && !seccionDTO.gruposCampos().isEmpty()) {
            List<GrupoCamposModel> gruposCampos = seccionDTO.gruposCampos().stream()
                    .map(dto -> {
                        // 1. Mapear DTO a Model (sin relaciones)
                        GrupoCamposModel grupo = grupoCamposMapper.toModel(dto);

                        // 2. Asignar seccion manualmente
                        grupo.setSeccion(seccion);

                        // 3. Si el DTO tiene campos, mapearlos
                        if (dto.camposSimples() != null && !dto.camposSimples().isEmpty()) {
                            List<CampoSimpleModel> camposDelGrupo = dto.camposSimples().stream()
                                    .map(campoDTO -> {
                                        CampoSimpleModel campo = campoSimpleMapper.toModel(campoDTO);
                                        campo.setSeccion(seccion); // FK obligatoria
                                        campo.setGrupo(grupo);     // FK opcional (pertenece al grupo)
                                        return campo;
                                    })
                                    .collect(Collectors.toList());

                            grupo.setCampos(camposDelGrupo);
                        }

                        return grupo;
                    })
                    .collect(Collectors.toList());

            seccion.setGruposCampos(gruposCampos);
            log.debug("Asignados {} grupos de campos a la sección", gruposCampos.size());
        } else {
            seccion.setGruposCampos(new ArrayList<>());
        }
    }

    /**
     * Inicializa la colección de tablas.
     * Si el DTO contiene tablas, las mapea y asigna a la sección.
     */
    private void llenarTablas(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.tablas() != null && !seccionDTO.tablas().isEmpty()) {
            List<TablaModel> tablas = seccionDTO.tablas().stream()
                    .map(dto -> {
                        // 1. Mapear DTO a Model (sin relaciones)
                        TablaModel tabla = tablaMapper.toModel(dto);

                        // 2. Asignar seccion manualmente
                        tabla.setSeccion(seccion);

                        // 3. Mapear columnas si existen
                        if (dto.columnas() != null && !dto.columnas().isEmpty()) {
                            List<ColumnaTablaModel> columnas = dto.columnas().stream()
                                    .map(colDTO -> {
                                        ColumnaTablaModel columna = columnaTablaMapper.toModel(colDTO);
                                        columna.setTabla(tabla);
                                        return columna;
                                    })
                                    .collect(Collectors.toList());
                            tabla.setColumnas(columnas);
                        }

                        // 4. Mapear filas si existen
                        if (dto.filas() != null && !dto.filas().isEmpty()) {
                            List<FilaTablaModel> filas = dto.filas().stream()
                                    .map(filaDTO -> {
                                        FilaTablaModel fila = filaTablaMapper.toModel(filaDTO);
                                        fila.setTabla(tabla);
                                        return fila;
                                    })
                                    .collect(Collectors.toList());
                            tabla.setFilas(filas);
                        }

                        return tabla;
                    })
                    .collect(Collectors.toList());

            seccion.setTablas(tablas);
            log.debug("Asignadas {} tablas a la sección", tablas.size());
        } else {
            seccion.setTablas(new ArrayList<>());
        }
    }

    /**
     * Obtiene todas las secciones de una versión de receta con todas sus relaciones cargadas.
     *
     * @param versionReceta Versión de receta padre
     * @return Lista de secciones completas
     */
    @Transactional(readOnly = true)
    public List<SeccionModel> obtenerSeccionesCompletasPorVersion(VersionRecetaModel versionReceta) {
        log.debug("Obteniendo secciones completas para versión ID: {}", versionReceta.getCodigoVersionReceta());

        List<SeccionModel> secciones = seccionRepository.findByVersionRecetaPadre(versionReceta);

        if (secciones.isEmpty()) {
            log.debug("No se encontraron secciones para la versión");
            return secciones;
        }

        cargarRelacionesSecciones(secciones);
        log.debug("Cargadas {} secciones con todas sus relaciones", secciones.size());

        return secciones;
    }

    /**
     * Carga todas las relaciones de las secciones incluyendo:
     * - Campos simples
     * - Grupos de campos con sus campos anidados
     * - Tablas con columnas y filas
     *
     * @param secciones Lista de secciones a enriquecer
     */
    private void cargarRelacionesSecciones(List<SeccionModel> secciones) {
        List<Long> idsSecciones = obtenerIdsSecciones(secciones);

        cargarRelacionesPrincipales(secciones);
        cargarCamposDeGrupos(secciones, idsSecciones);
        cargarTablasCompletas(secciones);
        inicializarRelaciones(secciones);
    }

    /**
     * Extrae los IDs de todas las secciones.
     */
    private List<Long> obtenerIdsSecciones(List<SeccionModel> secciones) {
        return secciones.stream()
                .map(SeccionModel::getIdSeccion)
                .collect(Collectors.toList());
    }

    /**
     * Carga las relaciones de primer nivel: campos simples, grupos y tablas.
     */
    private void cargarRelacionesPrincipales(List<SeccionModel> secciones) {
        log.debug("Cargando relaciones principales de {} secciones", secciones.size());
        seccionRepository.findWithCamposSimples(secciones);
        seccionRepository.findWithGruposCampos(secciones);
        seccionRepository.findWithTablas(secciones);
    }

    /**
     * Carga los campos de los grupos de forma anidada y los asigna a los grupos correspondientes.
     */
    private void cargarCamposDeGrupos(List<SeccionModel> secciones, List<Long> idsSecciones) {
        log.debug("Cargando campos de grupos para {} secciones", idsSecciones.size());
        List<GrupoCamposModel> gruposConCampos = seccionRepository.findGruposWithCamposBySeccionIds(idsSecciones);
        asignarCamposAGrupos(secciones, gruposConCampos);
    }

    /**
     * Carga las columnas y filas de todas las tablas en dos pasos separados
     * para evitar el problema de múltiples bags en Hibernate.
     */
    private void cargarTablasCompletas(List<SeccionModel> secciones) {
        List<TablaModel> todasLasTablas = obtenerTodasLasTablas(secciones);

        if (!todasLasTablas.isEmpty()) {
            log.debug("Cargando columnas y filas de {} tablas", todasLasTablas.size());
            seccionRepository.findTablasWithColumnas(todasLasTablas);
            seccionRepository.findTablasWithFilas(todasLasTablas);
        }
    }

    /**
     * Obtiene todas las tablas de todas las secciones.
     */
    private List<TablaModel> obtenerTodasLasTablas(List<SeccionModel> secciones) {
        return secciones.stream()
                .map(SeccionModel::getTablas)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Asigna los campos cargados a sus grupos correspondientes.
     * Utiliza un mapa para búsqueda eficiente por ID.
     */
    private void asignarCamposAGrupos(List<SeccionModel> secciones, List<GrupoCamposModel> gruposConCampos) {
        if (gruposConCampos.isEmpty()) {
            log.debug("No hay grupos con campos para asignar");
            return;
        }

        Map<Long, GrupoCamposModel> gruposMap = crearMapaGrupos(secciones);

        int camposAsignados = 0;
        for (GrupoCamposModel grupoConCampos : gruposConCampos) {
            GrupoCamposModel grupoOriginal = gruposMap.get(grupoConCampos.getId());
            if (grupoOriginal != null && grupoConCampos.getCampos() != null) {
                grupoOriginal.getCampos().addAll(grupoConCampos.getCampos());
                camposAsignados += grupoConCampos.getCampos().size();
            }
        }

        log.debug("Asignados {} campos a {} grupos", camposAsignados, gruposConCampos.size());
    }

    /**
     * Crea un mapa de grupos indexados por ID para búsqueda eficiente.
     * Limpia los campos existentes de cada grupo.
     */
    private Map<Long, GrupoCamposModel> crearMapaGrupos(List<SeccionModel> secciones) {
        Map<Long, GrupoCamposModel> gruposMap = new HashMap<>();

        for (SeccionModel seccion : secciones) {
            if (seccion.getGruposCampos() != null) {
                for (GrupoCamposModel grupo : seccion.getGruposCampos()) {
                    gruposMap.put(grupo.getId(), grupo);
                    grupo.getCampos().clear();
                }
            }
        }

        log.debug("Creado mapa con {} grupos", gruposMap.size());
        return gruposMap;
    }

    /**
     * Inicializa todas las relaciones lazy de las secciones para evitar
     * LazyInitializationException fuera del contexto transaccional.
     */
    private void inicializarRelaciones(List<SeccionModel> secciones) {
        log.debug("Inicializando relaciones lazy de {} secciones", secciones.size());
        for (SeccionModel seccion : secciones) {
            inicializarCamposSimples(seccion);
            inicializarGruposCampos(seccion);
            inicializarTablas(seccion);
        }
    }

    private void inicializarCamposSimples(SeccionModel seccion) {
        if (seccion.getCamposSimples() != null) {
            Hibernate.initialize(seccion.getCamposSimples());
        }
    }

    private void inicializarGruposCampos(SeccionModel seccion) {
        if (seccion.getGruposCampos() != null) {
            Hibernate.initialize(seccion.getGruposCampos());
            for (GrupoCamposModel grupo : seccion.getGruposCampos()) {
                if (grupo.getCampos() != null) {
                    Hibernate.initialize(grupo.getCampos());
                }
            }
        }
    }

    private void inicializarTablas(SeccionModel seccion) {
        if (seccion.getTablas() != null) {
            Hibernate.initialize(seccion.getTablas());
            seccion.getTablas().forEach(tabla -> {
                if (tabla.getColumnas() != null) Hibernate.initialize(tabla.getColumnas());
                if (tabla.getFilas() != null) Hibernate.initialize(tabla.getFilas());
            });
        }
    }

    /**
     * Obtiene todas las secciones de una versión de receta como DTOs completos.
     *
     * @param codigoVersion Código de la versión de receta
     * @return Lista de DTOs de secciones ordenadas por orden
     * @throws IllegalArgumentException     si el código es nulo o vacío
     * @throws RecursoNoEncontradoException si la versión no existe
     */
    @Transactional(readOnly = true)
    public List<SeccionResponseDTO> obtenerSeccionesDTOCompletasPorVersion(String codigoVersion) {
        if (codigoVersion == null || codigoVersion.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_CODIGO_VERSION_INVALIDO);
        }

        log.debug("Obteniendo secciones DTO para versión: {}", codigoVersion);

        VersionRecetaModel versionReceta = versionRecetaMetadataService.findVersionModelByCodigo(codigoVersion);
        List<SeccionModel> seccionesCompletas = obtenerSeccionesCompletasPorVersion(versionReceta);

        verificarEstadoGruposAntesMapeo(seccionesCompletas);

        List<SeccionResponseDTO> seccionesDTO = seccionMapper.toResponseDTOList(seccionesCompletas);

        log.info("Obtenidas {} secciones DTO para versión {}", seccionesDTO.size(), codigoVersion);
        return ordenarSeccionesPorOrden(seccionesDTO);
    }

    /**
     * Verifica y registra el estado de los grupos antes del mapeo a DTO.
     * Útil para debugging y validación.
     */
    private void verificarEstadoGruposAntesMapeo(List<SeccionModel> secciones) {
        if (log.isDebugEnabled()) {
            for (SeccionModel seccion : secciones) {
                log.debug("Sección {} - {}", seccion.getIdSeccion(), seccion.getTitulo());
                if (seccion.getGruposCampos() != null) {
                    for (GrupoCamposModel grupo : seccion.getGruposCampos()) {
                        log.debug("   Grupo {} - Campos: {}",
                                grupo.getId(),
                                grupo.getCampos() != null ? grupo.getCampos().size() : "NULL");
                    }
                }
            }
        }
    }

    /**
     * Ordena las secciones por su campo orden de forma ascendente.
     */
    private List<SeccionResponseDTO> ordenarSeccionesPorOrden(List<SeccionResponseDTO> seccionesDTO) {
        return seccionesDTO.stream()
                .sorted(Comparator.comparingInt(SeccionResponseDTO::orden))
                .collect(Collectors.toList());
    }
}