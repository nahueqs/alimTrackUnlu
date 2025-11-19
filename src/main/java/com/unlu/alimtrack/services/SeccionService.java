package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.*;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.ColumnaTablaRepository;
import com.unlu.alimtrack.repositories.FilaTablaRepository;
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
import java.util.stream.Stream;

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


    private final VersionRecetaMetadataService versionRecetaMetadataService;
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final UsuarioQueryService usuarioQueryService;
    private final ColumnaTablaRepository columnaTablaRepository;
    private final FilaTablaRepository filaTablaRepository;


    private final SeccionRepository seccionRepository;
    private final SeccionValidator seccionValidator;

    private final TablaMapperManual tablaMapper;
    private final SeccionMapperManual seccionMapperManual;
    private final ColumnaTablaMapper columnaTablaMapper;
    private final FilaTablaMapper filaTablaMapper;
    private final CampoSimpleMapper campoSimpleMapper;
    private final GrupoCamposMapper grupoCamposMapper;

    public SeccionService(@Lazy VersionRecetaMetadataService versionRecetaMetadataService, @Lazy VersionRecetaQueryService versionRecetaQueryService, UsuarioQueryService usuarioQueryService, ColumnaTablaRepository columnaTablaRepository, FilaTablaRepository filaTablaRepository, SeccionRepository seccionRepository, SeccionValidator seccionValidator, TablaMapperManual tablaMapper, SeccionMapperManual seccionMapperManual, ColumnaTablaMapper columnaTablaMapper, FilaTablaMapper filaTablaMapper, CampoSimpleMapper campoSimpleMapper, GrupoCamposMapper grupoCamposMapper) {
        this.versionRecetaMetadataService = versionRecetaMetadataService;
        this.versionRecetaQueryService = versionRecetaQueryService;
        this.usuarioQueryService = usuarioQueryService;
        this.columnaTablaRepository = columnaTablaRepository;
        this.filaTablaRepository = filaTablaRepository;
        this.seccionRepository = seccionRepository;
        this.seccionValidator = seccionValidator;
        this.tablaMapper = tablaMapper;
        this.seccionMapperManual = seccionMapperManual;
        this.columnaTablaMapper = columnaTablaMapper;
        this.filaTablaMapper = filaTablaMapper;
        this.campoSimpleMapper = campoSimpleMapper;
        this.grupoCamposMapper = grupoCamposMapper;
    }


    /**
     * Crea una nueva sección para una versión de receta.
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

    private void validarPrecondicionesCreacion(String codigoReceta, String usernameCreador) {
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoReceta)) {
            throw new RecursoNoEncontradoException(String.format(ERROR_VERSION_NO_ENCONTRADA, codigoReceta));
        }

        if (!usuarioQueryService.existsByUsername(usernameCreador)) {
            throw new RecursoNoEncontradoException(String.format(ERROR_USUARIO_NO_ENCONTRADO, usernameCreador));
        }
    }

    private void poblarColecciones(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        llenarCamposSimples(seccion, seccionDTO);
        llenarGrupoCampos(seccion, seccionDTO);
        llenarTablas(seccion, seccionDTO);
    }

    private void llenarCamposSimples(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.camposSimples() != null && !seccionDTO.camposSimples().isEmpty()) {
            List<CampoSimpleModel> camposSimples = seccionDTO.camposSimples().stream().map(dto -> {
                CampoSimpleModel campo = campoSimpleMapper.toModel(dto);
                campo.setSeccion(seccion);
                campo.setGrupo(null);
                return campo;
            }).collect(Collectors.toList());
            seccion.setCamposSimples(camposSimples);
        } else {
            seccion.setCamposSimples(new ArrayList<>());
        }
    }

    private void llenarGrupoCampos(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.gruposCampos() != null && !seccionDTO.gruposCampos().isEmpty()) {
            List<GrupoCamposModel> gruposCampos = seccionDTO.gruposCampos().stream().map(dto -> {
                GrupoCamposModel grupo = grupoCamposMapper.toModel(dto);
                grupo.setSeccion(seccion);

                if (dto.camposSimples() != null && !dto.camposSimples().isEmpty()) {
                    List<CampoSimpleModel> camposDelGrupo = dto.camposSimples().stream().map(campoDTO -> {
                        CampoSimpleModel campo = campoSimpleMapper.toModel(campoDTO);
                        campo.setSeccion(seccion);
                        campo.setGrupo(grupo);
                        return campo;
                    }).collect(Collectors.toList());
                    grupo.setCampos(camposDelGrupo);
                }
                return grupo;
            }).collect(Collectors.toList());
            seccion.setGruposCampos(gruposCampos);
        } else {
            seccion.setGruposCampos(new ArrayList<>());
        }
    }

    private void llenarTablas(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.tablas() != null && !seccionDTO.tablas().isEmpty()) {
            List<TablaModel> tablas = seccionDTO.tablas().stream().map(dto -> {
                TablaModel tabla = tablaMapper.toModel(dto);
                tabla.setSeccion(seccion);

                if (dto.columnas() != null && !dto.columnas().isEmpty()) {
                    List<ColumnaTablaModel> columnas = dto.columnas().stream().map(colDTO -> {
                        ColumnaTablaModel columna = columnaTablaMapper.toModel(colDTO);
                        columna.setTabla(tabla);
                        return columna;
                    }).collect(Collectors.toList());
                    tabla.setColumnas(columnas);
                }

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
        } else {
            seccion.setTablas(new ArrayList<>());
        }
    }

    /*
     * Carga en 3 niveles para evitar MultipleBagFetchException:
     * - Nivel 1: Entidades principales (secciones)
     * - Nivel 2: Colecciones directas (campos simples, grupos, tablas)
     * - Nivel 3: Colecciones anidadas (campos de grupos, columnas/filas de tablas)
     */
    @Transactional(readOnly = true)
    public List<SeccionModel> obtenerSeccionesCompletasPorVersion(VersionRecetaModel versionReceta) {
        log.debug("🔍 Obteniendo secciones completas para versión: {}", versionReceta.getCodigoVersionReceta());

        // ========== NIVEL 1: SECCIONES ==========
        List<SeccionModel> secciones = seccionRepository.findSeccionesBasicas(versionReceta);

        if (secciones.isEmpty()) {
            log.debug("No se encontraron secciones");
            return secciones;
        }

        log.debug("📋 Nivel 1: Cargadas {} secciones básicas", secciones.size());

        List<Long> seccionIds = secciones.stream()
                .map(SeccionModel::getIdSeccion)
                .collect(Collectors.toList());

        // ========== NIVEL 2: COLECCIONES DIRECTAS ==========

        // Campos simples de secciones
        seccionRepository.findSeccionesConCamposSimples(secciones);
        log.debug("✅ Nivel 2: Campos simples cargados");

        // Grupos de secciones (sin campos internos)
        seccionRepository.findSeccionesConGrupos(secciones);
        log.debug("✅ Nivel 2: Grupos cargados");

        // Tablas de secciones (sin columnas/filas)
        seccionRepository.findSeccionesConTablas(secciones);
        log.debug("✅ Nivel 2: Tablas cargadas");

        // ========== NIVEL 3: COLECCIONES ANIDADAS ==========

        // Campos dentro de grupos
        List<GrupoCamposModel> gruposConCampos = seccionRepository.findGruposWithCamposBySeccionIds(seccionIds);
        log.debug("✅ Nivel 3: Campos de {} grupos cargados", gruposConCampos.size());

        List<Long> tablaIds = secciones.stream()
                .flatMap(seccion -> seccion.getTablas() != null ?
                        seccion.getTablas().stream().map(TablaModel::getId) : Stream.empty())
                .collect(Collectors.toList());

        if (!tablaIds.isEmpty()) {
            log.debug("🔧 Cargando relaciones para {} tablas", tablaIds.size());

            // ✅ Cargar columnas desde su propio repository (mantiene relaciones)
            List<ColumnaTablaModel> columnas = columnaTablaRepository.findByTablaIds(tablaIds);
            log.debug("🔧 Columnas cargadas: {} (con relaciones)", columnas.size());

            // ✅ Cargar filas desde su propio repository (mantiene relaciones)
            List<FilaTablaModel> filas = filaTablaRepository.findByTablaIds(tablaIds);
            log.debug("🔧 Filas cargadas: {} (con relaciones)", filas.size());

            // ✅ Asignar manteniendo relaciones bidireccionales
            asignarColumnasConRelaciones(secciones, columnas);
            asignarFilasConRelaciones(secciones, filas);
        }

        // Debug final
        logResultadosCarga(secciones);

        return secciones;
    }

    private void logResultadosCarga(List<SeccionModel> secciones) {
        log.debug("🎉 CARGA COMPLETADA - Resumen:");

        for (SeccionModel seccion : secciones) {
            log.debug("📄 Sección {}: '{}'", seccion.getIdSeccion(), seccion.getTitulo());

            // Campos simples
            if (seccion.getCamposSimples() != null) {
                log.debug("   📝 Campos simples: {}", seccion.getCamposSimples().size());
            }

            // Grupos con campos
            if (seccion.getGruposCampos() != null) {
                log.debug("   📦 Grupos: {}", seccion.getGruposCampos().size());
                for (GrupoCamposModel grupo : seccion.getGruposCampos()) {
                    int numCampos = grupo.getCampos() != null ? grupo.getCampos().size() : 0;
                    log.debug("      👉 Grupo {}: '{}' - {} campos",
                            grupo.getId(), grupo.getSubtitulo(), numCampos);
                }
            }

            // Tablas con columnas y filas - VERIFICAR RELACIONES
            if (seccion.getTablas() != null) {
                log.debug("   📊 Tablas: {}", seccion.getTablas().size());
                for (TablaModel tabla : seccion.getTablas()) {
                    int numColumnas = tabla.getColumnas() != null ? tabla.getColumnas().size() : 0;
                    int numFilas = tabla.getFilas() != null ? tabla.getFilas().size() : 0;

                    log.debug("      📋 Tabla {}: '{}' - {} columnas, {} filas",
                            tabla.getId(), tabla.getNombre(), numColumnas, numFilas);

                    // ✅ VERIFICAR ASIGNACIONES CORRECTAS
                    if (tabla.getColumnas() != null) {
                        tabla.getColumnas().forEach(col -> {
                            boolean relacionCorrecta = col.getTabla() != null && col.getTabla().getId().equals(tabla.getId());
                            log.debug("         📍 Columna {} - Tabla padre: {} {}",
                                    col.getId(),
                                    col.getTabla() != null ? col.getTabla().getId() : "NULL",
                                    relacionCorrecta ? "✅" : "❌");
                        });
                    }

                    if (tabla.getFilas() != null) {
                        tabla.getFilas().forEach(fila -> {
                            boolean relacionCorrecta = fila.getTabla() != null && fila.getTabla().getId().equals(tabla.getId());
                            log.debug("         📍 Fila {} - Tabla padre: {} {}",
                                    fila.getId(),
                                    fila.getTabla() != null ? fila.getTabla().getId() : "NULL",
                                    relacionCorrecta ? "✅" : "❌");
                        });
                    }
                }
            }
        }
    }

    private void asignarColumnasConRelaciones(List<SeccionModel> secciones, List<ColumnaTablaModel> columnas) {
        // Agrupar columnas por tabla ID
        Map<Long, List<ColumnaTablaModel>> columnasPorTabla = columnas.stream()
                .collect(Collectors.groupingBy(columna -> {
                    TablaModel tabla = columna.getTabla();
                    if (tabla != null) {
                        log.debug("🔧 Columna {} pertenece a tabla {}", columna.getId(), tabla.getId());
                        return tabla.getId();
                    } else {
                        log.error("🔧 Columna {} NO tiene tabla asignada", columna.getId());
                        return -1L;
                    }
                }));

        // Crear mapa de tablas por ID para acceso rápido
        Map<Long, TablaModel> mapaTablas = new HashMap<>();
        for (SeccionModel seccion : secciones) {
            if (seccion.getTablas() != null) {
                for (TablaModel tabla : seccion.getTablas()) {
                    mapaTablas.put(tabla.getId(), tabla);
                }
            }
        }

        // Asignar columnas a las tablas correspondientes
        for (Map.Entry<Long, List<ColumnaTablaModel>> entry : columnasPorTabla.entrySet()) {
            Long tablaId = entry.getKey();
            List<ColumnaTablaModel> columnasDeTabla = entry.getValue();

            TablaModel tabla = mapaTablas.get(tablaId);
            if (tabla != null) {
                // Ordenar columnas
                List<ColumnaTablaModel> columnasOrdenadas = columnasDeTabla.stream()
                        .sorted(Comparator.comparingInt(col -> col.getOrden() != null ? col.getOrden() : 0))
                        .collect(Collectors.toList());

                // ✅ VERIFICAR que las relaciones se mantengan
                for (ColumnaTablaModel columna : columnasOrdenadas) {
                    if (columna.getTabla() == null) {
                        log.warn("🔧 Columna {} perdió relación con tabla, reasignando...", columna.getId());
                        columna.setTabla(tabla);
                    } else if (!columna.getTabla().getId().equals(tablaId)) {
                        log.warn("🔧 Columna {} tiene tabla incorrecta: {} vs {}",
                                columna.getId(), columna.getTabla().getId(), tablaId);
                        columna.setTabla(tabla);
                    }
                }

                tabla.setColumnas(columnasOrdenadas);
                log.debug("✅ Asignadas {} columnas a tabla {} con relaciones verificadas",
                        columnasOrdenadas.size(), tablaId);
            } else {
                log.warn("🔧 No se encontró tabla {} para asignar {} columnas", tablaId, columnasDeTabla.size());
            }
        }
    }

    /**
     * ✅ NUEVO MÉTODO: Asignar filas manteniendo relaciones bidireccionales
     */
    private void asignarFilasConRelaciones(List<SeccionModel> secciones, List<FilaTablaModel> filas) {
        // Agrupar filas por tabla ID
        Map<Long, List<FilaTablaModel>> filasPorTabla = filas.stream()
                .collect(Collectors.groupingBy(fila -> {
                    TablaModel tabla = fila.getTabla();
                    if (tabla != null) {
                        log.debug("🔧 Fila {} pertenece a tabla {}", fila.getId(), tabla.getId());
                        return tabla.getId();
                    } else {
                        log.error("🔧 Fila {} NO tiene tabla asignada", fila.getId());
                        return -1L;
                    }
                }));

        // Crear mapa de tablas por ID para acceso rápido
        Map<Long, TablaModel> mapaTablas = new HashMap<>();
        for (SeccionModel seccion : secciones) {
            if (seccion.getTablas() != null) {
                for (TablaModel tabla : seccion.getTablas()) {
                    mapaTablas.put(tabla.getId(), tabla);
                }
            }
        }

        // Asignar filas a las tablas correspondientes
        for (Map.Entry<Long, List<FilaTablaModel>> entry : filasPorTabla.entrySet()) {
            Long tablaId = entry.getKey();
            List<FilaTablaModel> filasDeTabla = entry.getValue();

            TablaModel tabla = mapaTablas.get(tablaId);
            if (tabla != null) {
                // Ordenar filas
                List<FilaTablaModel> filasOrdenadas = filasDeTabla.stream()
                        .sorted(Comparator.comparingInt(fila -> fila.getOrden() != null ? fila.getOrden() : 0))
                        .collect(Collectors.toList());

                // ✅ VERIFICAR que las relaciones se mantengan
                for (FilaTablaModel fila : filasOrdenadas) {
                    if (fila.getTabla() == null) {
                        log.warn("🔧 Fila {} perdió relación con tabla, reasignando...", fila.getId());
                        fila.setTabla(tabla);
                    } else if (!fila.getTabla().getId().equals(tablaId)) {
                        log.warn("🔧 Fila {} tiene tabla incorrecta: {} vs {}",
                                fila.getId(), fila.getTabla().getId(), tablaId);
                        fila.setTabla(tabla);
                    }
                }

                tabla.setFilas(filasOrdenadas);
                log.debug("✅ Asignadas {} filas a tabla {} con relaciones verificadas",
                        filasOrdenadas.size(), tablaId);
            } else {
                log.warn("🔧 No se encontró tabla {} para asignar {} filas", tablaId, filasDeTabla.size());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SeccionResponseDTO> obtenerSeccionesDTOCompletasPorVersion(String codigoVersion) {
        if (codigoVersion == null || codigoVersion.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_CODIGO_VERSION_INVALIDO);
        }

        log.debug("Obteniendo secciones DTO para versión: {}", codigoVersion);

        VersionRecetaModel versionReceta = versionRecetaMetadataService.findVersionModelByCodigo(codigoVersion);
        List<SeccionModel> seccionesCompletas = obtenerSeccionesCompletasPorVersion(versionReceta);

        // ✅ Usar SOLO el mapper manual
        List<SeccionResponseDTO> seccionesDTO = seccionMapperManual.toResponseDTOList(seccionesCompletas);

        log.info("Obtenidas {} secciones DTO para versión {}", seccionesDTO.size(), codigoVersion);

        return seccionesDTO;
    }

    public Integer getCantidadCampos(List<SeccionResponseDTO> secciones) {
        return secciones.stream()
                .mapToInt(seccion -> {
                    int camposSimples = seccion.camposSimples().size();

                    int camposEnGrupos = seccion.gruposCampos().stream()
                            .mapToInt(grupo -> grupo.campos().size())
                            .sum();

                    return camposSimples + camposEnGrupos;
                })
                .sum();
    }

    public Integer getCantidadTablas(List<SeccionResponseDTO> secciones) {
        return secciones.stream().mapToInt(seccion -> seccion.tablas().size()).sum();

    }

    public Integer getCantidadCeldasTablas(List<SeccionResponseDTO> secciones) {
        return secciones.stream()
                .flatMap(seccion -> seccion.tablas().stream())
                .mapToInt(tabla -> tabla.filas().size() * tabla.columnas().size())
                .sum();
    }
}