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
import java.util.Set;
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
    private final SeccionMapperManual seccionMapperManual;
    private final CampoSimpleMapper campoSimpleMapper;
    private final GrupoCamposMapper grupoCamposMapper;
    private final TablaMapperManual tablaMapper;
    private final ColumnaTablaMapper columnaTablaMapper;
    private final FilaTablaMapper filaTablaMapper;

    @Override
    @Transactional
    public SeccionModel crearSeccion(String codigoReceta, SeccionCreateDTO seccionDTO) {
        log.info("Creando nueva sección para la versión de receta: {}", codigoReceta);

        validarPrecondicionesCreacion(codigoReceta, seccionDTO.emailCreador());
        seccionValidator.validarCreacionSeccion(codigoReceta, seccionDTO);

        VersionRecetaModel versionRecetaPadre = versionRecetaMetadataService.findVersionModelByCodigo(codigoReceta);
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelByEmail(seccionDTO.emailCreador().trim());

        SeccionModel seccion = new SeccionModel();
        seccion.setVersionRecetaPadre(versionRecetaPadre);
        seccion.setCreadoPor(usuarioCreador);
        seccion.setTitulo(seccionDTO.titulo().trim());
        seccion.setOrden(seccionDTO.orden());

        poblarColecciones(seccion, seccionDTO);

        SeccionModel seccionGuardada = seccionRepository.save(seccion);
        log.info("Sección creada exitosamente con ID: {} para la versión {}", seccionGuardada.getId(), codigoReceta);

        return seccionGuardada;
    }

    private void validarPrecondicionesCreacion(String codigoReceta, String email) {
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoReceta)) {
            throw new RecursoNoEncontradoException("Versión de receta no encontrada con código: " + codigoReceta);
        }
        if (!usuarioService.existsByEmail(email)) {
            throw new RecursoNoEncontradoException("Usuario creador no encontrado con el email: " + email);
        }
    }

    private void poblarColecciones(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        llenarCamposSimples(seccion, seccionDTO);
        llenarGrupoCampos(seccion, seccionDTO);
        llenarTablas(seccion, seccionDTO);
    }

    private void llenarCamposSimples(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.camposSimples() != null && !seccionDTO.camposSimples().isEmpty()) {
            Set<CampoSimpleModel> camposSimples = seccionDTO.camposSimples().stream().map(dto -> {
                CampoSimpleModel campo = campoSimpleMapper.toModel(dto);
                campo.setSeccion(seccion);
                campo.setGrupo(null);
                return campo;
            }).collect(Collectors.toSet());
            seccion.setCamposSimples(camposSimples);
        }
    }

    private void llenarGrupoCampos(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.gruposCampos() != null && !seccionDTO.gruposCampos().isEmpty()) {
            Set<GrupoCamposModel> gruposCampos = seccionDTO.gruposCampos().stream().map(dto -> {
                GrupoCamposModel grupo = grupoCamposMapper.toModel(dto);
                grupo.setSeccion(seccion);
                if (dto.camposSimples() != null) {
                    Set<CampoSimpleModel> camposDelGrupo = dto.camposSimples().stream().map(campoDTO -> {
                        CampoSimpleModel campo = campoSimpleMapper.toModel(campoDTO);
                        campo.setSeccion(seccion);
                        campo.setGrupo(grupo);
                        return campo;
                    }).collect(Collectors.toSet());
                    grupo.setCampos(camposDelGrupo);
                }
                return grupo;
            }).collect(Collectors.toSet());
            seccion.setGruposCampos(gruposCampos);
        }
    }

    private void llenarTablas(SeccionModel seccion, SeccionCreateDTO seccionDTO) {
        if (seccionDTO.tablas() != null && !seccionDTO.tablas().isEmpty()) {
            Set<TablaModel> tablas = seccionDTO.tablas().stream().map(dto -> {
                TablaModel tabla = tablaMapper.toModel(dto);
                tabla.setSeccion(seccion);
                if (dto.columnas() != null) {
                    Set<ColumnaTablaModel> columnas = dto.columnas().stream()
                            .map(columnaTablaMapper::toModel)
                            .collect(Collectors.toSet());
                    columnas.forEach(columna -> columna.setTabla(tabla));
                    tabla.setColumnas(columnas);
                }
                if (dto.filas() != null) {
                    Set<FilaTablaModel> filas = dto.filas().stream()
                            .map(filaTablaMapper::toModel)
                            .collect(Collectors.toSet());
                    filas.forEach(fila -> fila.setTabla(tabla));
                    tabla.setFilas(filas);
                }
                return tabla;
            }).collect(Collectors.toSet());
            seccion.setTablas(tablas);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponseDTO> obtenerSeccionesDTOCompletasPorVersion(String codigoVersion) {
        log.info("Iniciando obtención de DTOs de sección completos para la versión: {}", codigoVersion);
        VersionRecetaModel versionReceta = versionRecetaMetadataService.findVersionModelByCodigo(codigoVersion);
        List<SeccionModel> seccionesCompletas = obtenerSeccionesCompletasPorVersion(versionReceta);
        return seccionMapperManual.toResponseDTOList(seccionesCompletas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionModel> obtenerSeccionesCompletasPorVersion(VersionRecetaModel versionReceta) {
        log.debug("Iniciando carga SECUENCIAL de la estructura para la versión: {}", versionReceta.getCodigoVersionReceta());

        // Paso 1: Cargar las secciones básicas.
        List<SeccionModel> secciones = seccionRepository.findSeccionesBasicas(versionReceta);
        if (secciones.isEmpty()) {
            return new ArrayList<>();
        }

        // Paso 2: Cargar colecciones de primer nivel en consultas separadas.
        seccionRepository.fetchCamposSimples(secciones);
        seccionRepository.fetchGruposCampos(secciones);
        // NUEVO: Cargar explícitamente los campos dentro de los grupos de campos
        seccionRepository.fetchCamposInGruposCampos(secciones);
        seccionRepository.fetchTablas(secciones);

        // Paso 3: Dejar que @BatchSize se encargue del resto al momento del mapeo.
        log.info("Carga secuencial de {} secciones y sus colecciones de primer nivel completada.", secciones.size());
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