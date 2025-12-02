package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.*;
import com.unlu.alimtrack.models.*;
import com.unlu.alimtrack.repositories.SeccionRepository;
import com.unlu.alimtrack.services.SeccionManagementService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaMetadataService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import com.unlu.alimtrack.services.validators.SeccionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeccionManagementServiceImpl implements SeccionManagementService {

    private final VersionRecetaMetadataService versionRecetaMetadataService;
    @Lazy
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final SeccionRepository seccionRepository;
    private final SeccionValidator seccionValidator;
    private final UsuarioService usuarioService;

    // Inyectamos el nuevo mapper de MapStruct
    private final SeccionMapperManual seccionMapperManual;

    // Mappers para la creación (pueden mantenerse si son necesarios)
    private final CampoSimpleMapper campoSimpleMapper;
    private final GrupoCamposMapper grupoCamposMapper;
    private final TablaMapperManual tablaMapper; // Mantener si se usa en crearSeccion
    private final ColumnaTablaMapper columnaTablaMapper; // Mantener si se usa en crearSeccion
    private final FilaTablaMapper filaTablaMapper; // Mantener si se usa en crearSeccion


    @Override
    @Transactional
    public SeccionModel crearSeccion(String codigoReceta, SeccionCreateDTO seccionDTO) {
        log.info("Creando nueva sección para la versión de receta: {}", codigoReceta);

        validarPrecondicionesCreacion(codigoReceta, seccionDTO.emailCreador());
        seccionValidator.validarCreacionSeccion(codigoReceta, seccionDTO);

        VersionRecetaModel versionRecetaPadre = versionRecetaMetadataService.findVersionModelByCodigo(codigoReceta);
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelByEmail(seccionDTO.emailCreador().trim()); // Fetch the UsuarioModel

        SeccionModel seccion = new SeccionModel();
        seccion.setVersionRecetaPadre(versionRecetaPadre);
        seccion.setCreadoPor(usuarioCreador); // Set the UsuarioModel object
        seccion.setTitulo(seccionDTO.titulo().trim());
        seccion.setOrden(seccionDTO.orden());

        poblarColecciones(seccion, seccionDTO);

        SeccionModel seccionGuardada = seccionRepository.save(seccion);
        log.info("Sección creada exitosamente con ID: {} para la versión {}", seccionGuardada.getId(), codigoReceta);

        return seccionGuardada;
    }

    private void validarPrecondicionesCreacion(String codigoReceta, String email) {
        log.debug("Validando precondiciones para la creación de sección en la versión {}", codigoReceta);
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoReceta)) {
            throw new RecursoNoEncontradoException("Versión de receta no encontrada con código: " + codigoReceta);
        }
        if (!usuarioService.existsByEmail(email)) {
            throw new RecursoNoEncontradoException("Usuario creador no encontrado con el email: " + email);
        }
    }

    private void poblarColecciones(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        log.debug("Poblando colecciones (campos, grupos, tablas) para la nueva sección");
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
                if (dto.camposSimples() != null) {
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
                if (dto.columnas() != null) {
                    List<ColumnaTablaModel> columnas = columnaTablaMapper.toModelList(dto.columnas()); // Usar mapper de lista
                    columnas.forEach(columna -> columna.setTabla(tabla)); // Asignar tabla padre
                    tabla.setColumnas(columnas);
                }

                if (dto.filas() != null) {
                    List<FilaTablaModel> filas = filaTablaMapper.toModelList(dto.filas()); // Usar mapper de lista
                    filas.forEach(fila -> fila.setTabla(tabla)); // Asignar tabla padre
                    tabla.setFilas(filas);
                }
                return tabla;
            }).collect(Collectors.toList());
            seccion.setTablas(tablas);
        } else {
            seccion.setTablas(new ArrayList<>());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponseDTO> obtenerSeccionesDTOCompletasPorVersion(String codigoVersion) {
        log.info("Obteniendo DTOs de sección completos para la versión: {}", codigoVersion);
        if (codigoVersion == null || codigoVersion.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de versión no puede ser nulo o vacío");
        }

        VersionRecetaModel versionReceta = versionRecetaMetadataService.findVersionModelByCodigo(codigoVersion);

        // La carga de 3 niveles sigue siendo una buena práctica para evitar MultipleBagFetchException
        List<SeccionModel> seccionesCompletas = obtenerSeccionesCompletasPorVersion(versionReceta);

        // Usamos el nuevo mapper de MapStruct, que es seguro y eficiente
        List<SeccionResponseDTO> seccionesDTO = seccionMapperManual.toResponseDTOList(seccionesCompletas);

        log.info("Mapeo a DTO completado. Retornando {} DTOs de sección para la versión {}", seccionesDTO.size(), codigoVersion);
        return seccionesDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionModel> obtenerSeccionesCompletasPorVersion(VersionRecetaModel versionReceta) {
        log.debug("Iniciando carga optimizada de la estructura para la versión: {}", versionReceta.getCodigoVersionReceta());

        // Nivel 1: Cargar secciones básicas
        List<SeccionModel> secciones = seccionRepository.findSeccionesBasicas(versionReceta);
        if (secciones.isEmpty()) {
            log.debug("No se encontraron secciones para la versión {}", versionReceta.getCodigoVersionReceta());
            return new ArrayList<>();
        }

        // Nivel 2: Cargar colecciones directas (campos, grupos, tablas)
        seccionRepository.findSeccionesConCamposSimples(secciones);
        seccionRepository.findSeccionesConGrupos(secciones);
        seccionRepository.findSeccionesConTablas(secciones);

        // Nivel 3: Cargar colecciones anidadas
        List<Long> seccionIds = secciones.stream().map(SeccionModel::getId).collect(Collectors.toList());
        if (!seccionIds.isEmpty()) {
            seccionRepository.findGruposWithCamposBySeccionIds(seccionIds);
            seccionRepository.findTablasWithColumnasBySeccionIds(seccionIds);
            seccionRepository.findTablasWithFilasBySeccionIds(seccionIds);
        }

        log.info("Carga completa de {} secciones para la versión {}", secciones.size(), versionReceta.getCodigoVersionReceta());
        return secciones;
    }

    @Override
    public Integer getCantidadCampos(List<SeccionResponseDTO> secciones) {
        return secciones.stream()
                .mapToInt(seccion -> seccion.camposSimples().size() +
                        seccion.gruposCampos().stream().mapToInt(grupo -> grupo.campos().size()).sum())
                .sum();
    }

    @Override
    public Integer getCantidadTablas(List<SeccionResponseDTO> secciones) {
        return secciones.stream().mapToInt(seccion -> seccion.tablas().size()).sum();
    }

    @Override
    public Integer getCantidadCeldasTablas(List<SeccionResponseDTO> secciones) {
        return secciones.stream()
                .flatMap(seccion -> seccion.tablas().stream())
                .mapToInt(tabla -> tabla.filas().size() * tabla.columnas().size())
                .sum();
    }
}
