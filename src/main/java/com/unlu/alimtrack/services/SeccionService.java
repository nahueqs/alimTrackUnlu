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


    public SeccionService(SeccionRepository seccionRepository, SeccionValidator seccionValidator, VersionRecetaQueryService versionRecetaQueryService, UsuarioQueryService usuarioQueryService, SeccionMapper seccionMapper, CampoSimpleMapper campoSimpleMapper, GrupoCamposMapper grupoCamposMapper, TablaMapper tablaMapper, @Lazy VersionRecetaMetadataService versionRecetaMetadataService, ColumnaTablaMapper columnaTablaMapper, FilaTablaMapper filaTablaMapper) {
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
            List<CampoSimpleModel> camposSimples = seccionDTO.camposSimples().stream().map(dto -> {
                CampoSimpleModel campo = campoSimpleMapper.toModel(dto);
                campo.setSeccion(seccion);
                campo.setGrupo(null);
                return campo;
            }).collect(Collectors.toList());
            seccion.setCamposSimples(camposSimples);
            log.debug("Asignados {} campos simples a la sección", camposSimples.size());
        } else {
            seccion.setCamposSimples(new ArrayList<>());
        }
    }

    private void llenarGrupoCampos(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.gruposCampos() != null && !seccionDTO.gruposCampos().isEmpty()) {
            List<GrupoCamposModel> gruposCampos = seccionDTO.gruposCampos().stream().map(dto -> {
                // 1. Mapear DTO a Model (sin relaciones)
                GrupoCamposModel grupo = grupoCamposMapper.toModel(dto);

                // 2. Asignar seccion manualmente
                grupo.setSeccion(seccion);

                // 3. Si el DTO tiene campos, mapearlos
                if (dto.camposSimples() != null && !dto.camposSimples().isEmpty()) {
                    List<CampoSimpleModel> camposDelGrupo = dto.camposSimples().stream().map(campoDTO -> {
                        CampoSimpleModel campo = campoSimpleMapper.toModel(campoDTO);
                        campo.setSeccion(seccion); // FK obligatoria
                        campo.setGrupo(grupo);     // FK opcional (pertenece al grupo)
                        return campo;
                    }).collect(Collectors.toList());

                    grupo.setCampos(camposDelGrupo);
                }

                return grupo;
            }).collect(Collectors.toList());

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
            List<TablaModel> tablas = seccionDTO.tablas().stream().map(dto -> {
                // 1. Mapear DTO a Model (sin relaciones)
                TablaModel tabla = tablaMapper.toModel(dto);

                // 2. Asignar seccion manualmente
                tabla.setSeccion(seccion);

                // 3. Mapear columnas si existen
                if (dto.columnas() != null && !dto.columnas().isEmpty()) {
                    List<ColumnaTablaModel> columnas = dto.columnas().stream().map(colDTO -> {
                        ColumnaTablaModel columna = columnaTablaMapper.toModel(colDTO);
                        columna.setTabla(tabla);
                        return columna;
                    }).collect(Collectors.toList());
                    tabla.setColumnas(columnas);
                }

                // 4. Mapear filas si existen
                if (dto.filas() != null && !dto.filas().isEmpty()) {
                    List<FilaTablaModel> filas = dto.filas().stream().map(filaDTO -> {
                        FilaTablaModel fila = filaTablaMapper.toModel(filaDTO);
                        fila.setTabla(tabla);
                        return fila;
                    }).collect(Collectors.toList());
                    tabla.setFilas(filas);
                }

                return tabla;
            }).collect(Collectors.toList());

            seccion.setTablas(tablas);
            log.debug("Asignadas {} tablas a la sección", tablas.size());
        } else {
            seccion.setTablas(new ArrayList<>());
        }
    }

    /**
     * Obtiene todas las secciones de una versión de receta con TODAS sus relaciones cargadas.
     * Versión CORREGIDA que evita MultipleBagFetchException.
     */
    @Transactional(readOnly = true)
    public List<SeccionModel> obtenerSeccionesCompletasPorVersion(VersionRecetaModel versionReceta) {
        log.debug("🔍 Obteniendo secciones completas para versión ID: {}", versionReceta.getCodigoVersionReceta());

        // 1. Obtener secciones básicas (sin relaciones)
        List<SeccionModel> secciones = seccionRepository.findByVersionRecetaPadre(versionReceta);

        if (secciones.isEmpty()) {
            log.debug("No se encontraron secciones");
            return secciones;
        }

        List<Long> seccionIds = secciones.stream().map(SeccionModel::getIdSeccion).collect(Collectors.toList());

        log.debug("📋 Procesando {} secciones con IDs: {}", secciones.size(), seccionIds);

        // 2. Cargar relaciones PRIMER NIVEL en consultas separadas
        cargarRelacionesPrimerNivel(secciones);

        // 3. Cargar relaciones ANIDADAS (campos dentro de grupos)
        cargarCamposDeGrupos(secciones, seccionIds);

        // 4. Cargar relaciones de tablas (columnas y filas)
        cargarRelacionesDeTablas(secciones);

        // Debug final
        logResultadosCarga(secciones);

        return secciones;
    }

    /**
     * Carga las relaciones de primer nivel: camposSimples, gruposCampos, tablas
     */
    private void cargarRelacionesPrimerNivel(List<SeccionModel> secciones) {
        log.debug("🔄 Cargando relaciones de primer nivel...");

        // Cargar campos simples
        List<SeccionModel> seccionesConCamposSimples = seccionRepository.findWithCamposSimples(secciones);
        log.debug("✅ Campos simples cargados");

        // Cargar grupos (sin campos anidados todavía)
        List<SeccionModel> seccionesConGrupos = seccionRepository.findWithGruposCampos(secciones);
        log.debug("✅ Grupos cargados (sin campos)");

        // Cargar tablas (sin columnas/filas todavía)
        List<SeccionModel> seccionesConTablas = seccionRepository.findWithTablas(secciones);
        log.debug("✅ Tablas cargadas");
    }

    /**
     * Carga los campos dentro de los grupos
     */
    private void cargarCamposDeGrupos(List<SeccionModel> secciones, List<Long> seccionIds) {
        log.debug("🔄 Cargando campos dentro de grupos...");

        List<GrupoCamposModel> gruposConCampos = seccionRepository.findGruposWithCamposBySeccionIds(seccionIds);
        log.debug("📦 Encontrados {} grupos con campos", gruposConCampos.size());

        // Organizar grupos por sección
        Map<Long, List<GrupoCamposModel>> gruposPorSeccion = gruposConCampos.stream().collect(Collectors.groupingBy(grupo -> grupo.getSeccion().getIdSeccion()));

        // Asignar grupos con campos a cada sección
        for (SeccionModel seccion : secciones) {
            List<GrupoCamposModel> gruposDeEstaSeccion = gruposPorSeccion.get(seccion.getIdSeccion());
            if (gruposDeEstaSeccion != null && seccion.getGruposCampos() != null) {
                // Reemplazar los grupos vacíos con los grupos que tienen campos cargados
                Map<Long, GrupoCamposModel> gruposExistentes = seccion.getGruposCampos().stream().collect(Collectors.toMap(GrupoCamposModel::getId, grupo -> grupo));

                for (GrupoCamposModel grupoConCampos : gruposDeEstaSeccion) {
                    GrupoCamposModel grupoExistente = gruposExistentes.get(grupoConCampos.getId());
                    if (grupoExistente != null) {
                        // Mantener la referencia original pero actualizar los campos
                        grupoExistente.setCampos(grupoConCampos.getCampos());
                    }
                }

                log.debug("✅ Sección {}: {} grupos actualizados con campos", seccion.getIdSeccion(), gruposDeEstaSeccion.size());
            }
        }
    }

    /**
     * Carga columnas y filas de las tablas
     */
    private void cargarRelacionesDeTablas(List<SeccionModel> secciones) {
        log.debug("🔄 Cargando relaciones de tablas...");

        List<TablaModel> todasLasTablas = secciones.stream().map(SeccionModel::getTablas).filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());

        if (!todasLasTablas.isEmpty()) {
            seccionRepository.findTablasWithColumnas(todasLasTablas);
            seccionRepository.findTablasWithFilas(todasLasTablas);
            log.debug("✅ {} tablas con columnas y filas cargadas", todasLasTablas.size());
        }
    }

    /**
     * Log de resultados finales
     */
    private void logResultadosCarga(List<SeccionModel> secciones) {
        log.debug("🎉 CARGA COMPLETADA - Resumen:");

        int totalCamposSimples = 0;
        int totalGrupos = 0;
        int totalCamposEnGrupos = 0;
        int totalTablas = 0;

        for (SeccionModel seccion : secciones) {
            log.debug("📄 Sección {}: '{}'", seccion.getIdSeccion(), seccion.getTitulo());

            // Campos simples
            if (seccion.getCamposSimples() != null) {
                totalCamposSimples += seccion.getCamposSimples().size();
                log.debug("   📝 Campos simples: {}", seccion.getCamposSimples().size());
            }

            // Grupos con campos
            if (seccion.getGruposCampos() != null) {
                totalGrupos += seccion.getGruposCampos().size();
                log.debug("   📦 Grupos: {}", seccion.getGruposCampos().size());

                for (GrupoCamposModel grupo : seccion.getGruposCampos()) {
                    if (grupo.getCampos() != null) {
                        totalCamposEnGrupos += grupo.getCampos().size();
                        log.debug("      👉 Grupo {}: '{}' - {} campos", grupo.getId(), grupo.getSubtitulo(), grupo.getCampos().size());
                    }
                }
            }

            // Tablas
            if (seccion.getTablas() != null) {
                totalTablas += seccion.getTablas().size();
                log.debug("   📊 Tablas: {}", seccion.getTablas().size());
            }
        }

        log.debug("📊 TOTALES - Secciones: {}, Campos simples: {}, Grupos: {}, Campos en grupos: {}, Tablas: {}", secciones.size(), totalCamposSimples, totalGrupos, totalCamposEnGrupos, totalTablas);
    }


    @Transactional(readOnly = true)
    public List<SeccionResponseDTO> obtenerSeccionesDTOCompletasPorVersion(String codigoVersion) {
        if (codigoVersion == null || codigoVersion.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_CODIGO_VERSION_INVALIDO);
        }

        log.debug("Obteniendo secciones DTO para versión: {}", codigoVersion);

        VersionRecetaModel versionReceta = versionRecetaMetadataService.findVersionModelByCodigo(codigoVersion);
        List<SeccionModel> seccionesCompletas = obtenerSeccionesCompletasPorVersion(versionReceta);

        List<SeccionResponseDTO> seccionesDTO = seccionMapper.toResponseDTOList(seccionesCompletas);

        log.info("Obtenidas {} secciones DTO para versión {}", seccionesDTO.size(), codigoVersion);

        // Ordenar por orden
        return seccionesDTO.stream().sorted(Comparator.comparingInt(SeccionResponseDTO::orden)).collect(Collectors.toList());
    }
}
