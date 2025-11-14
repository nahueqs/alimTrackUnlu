package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.CampoSimpleResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.GrupoCamposResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.TablaResponseDTO;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.GrupoCamposModel;
import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.models.TablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CampoSimpleMapper.class, GrupoCamposMapper.class, TablaMapper.class})
public interface SeccionMapper {

    // ✅ Mapper principal que ordena automáticamente
    @Mapping(target = "camposSimples", source = "camposSimples", qualifiedByName = "ordenarCamposSimples")
    @Mapping(target = "gruposCampos", source = "gruposCampos", qualifiedByName = "ordenarGruposCampos")
    @Mapping(target = "tablas", source = "tablas", qualifiedByName = "ordenarTablas")
    @Mapping(target = "id", source = "idSeccion")
    @Mapping(target = "codigoVersion", source = "versionRecetaPadre.codigoVersionReceta")
    SeccionResponseDTO toResponseDTO(SeccionModel seccion);

    // ✅ Lista de estructura
    List<SeccionResponseDTO> toResponseDTOList(List<SeccionModel> secciones);


    @Named("ordenarCamposSimples")
    default List<CampoSimpleResponseDTO> ordenarCamposSimples(List<CampoSimpleModel> campos) {
        if (campos == null) return List.of();

        return campos.stream()
                .filter(campo -> campo.getGrupo() == null) // Solo campos SIN grupo
                .sorted(Comparator.comparingInt(campo -> campo.getOrden() != null ? campo.getOrden() : 0))
                .map(this::mapCampoSimple)
                .collect(Collectors.toList());
    }


    @Named("ordenarCamposDeGrupo")
    default List<CampoSimpleResponseDTO> ordenarCamposDeGrupo(List<CampoSimpleModel> campos) {
        System.out.println("🔄 SeccionMapper.ordenarCamposDeGrupo: " + (campos != null ? campos.size() : 0) + " campos recibidos");

        if (campos == null) return List.of();

        List<CampoSimpleResponseDTO> camposOrdenados = campos.stream()
                .sorted(Comparator.comparingInt(campo -> campo.getOrden() != null ? campo.getOrden() : 0))
                .map(campo -> {
                    System.out.println("   📋 Mapeando campo " + campo.getId() + " - " + campo.getNombre() +
                            " (seccion: " + (campo.getSeccion() != null ? campo.getSeccion().getIdSeccion() : "NULL") +
                            ", grupo: " + (campo.getGrupo() != null ? campo.getGrupo().getId() : "NULL") + ")");
                    return this.mapCampoSimple(campo);
                })
                .collect(Collectors.toList());

        System.out.println("   ✅ Campos mapeados: " + camposOrdenados.size());
        return camposOrdenados;
    }

    @Named("ordenarGruposCampos")
    default List<GrupoCamposResponseDTO> ordenarGruposCampos(List<GrupoCamposModel> grupos) {
        System.out.println("🔄 SeccionMapper.ordenarGruposCampos: " + (grupos != null ? grupos.size() : 0) + " grupos");

        if (grupos == null) return List.of();

        return grupos.stream()
                .sorted(Comparator.comparingInt(grupo -> grupo.getOrden() != null ? grupo.getOrden() : 0))
                .map(grupo -> {
                    System.out.println("   📋 Procesando grupo " + grupo.getId() + " - " + grupo.getSubtitulo());
                    System.out.println("      📍 Sección del grupo: " + (grupo.getSeccion() != null ? grupo.getSeccion().getIdSeccion() : "NULL"));
                    System.out.println("      📊 Campos en el grupo: " + (grupo.getCampos() != null ? grupo.getCampos().size() : "NULL"));

                    if (grupo.getCampos() != null) {
                        grupo.getCampos().forEach(campo ->
                                System.out.println("         • Campo " + campo.getId() + ": " + campo.getNombre() +
                                        " (seccion: " + (campo.getSeccion() != null ? campo.getSeccion().getIdSeccion() : "NULL") +
                                        ", grupo: " + (campo.getGrupo() != null ? campo.getGrupo().getId() : "NULL") + ")")
                        );
                    }

                    GrupoCamposResponseDTO grupoDTO = mapGrupoCampos(grupo);
                    System.out.println("      ✅ Grupo mapeado - idSeccion en DTO: " + grupoDTO.idSeccion());
                    System.out.println("      ✅ Campos en DTO: " + (grupoDTO.campos() != null ? grupoDTO.campos().size() : "NULL"));

                    if (grupoDTO.campos() != null) {
                        grupoDTO.campos().forEach(campoDTO ->
                                System.out.println("         🎯 Campo DTO: " + campoDTO.id() + " - " + campoDTO.nombre() +
                                        " (idSeccion: " + campoDTO.idSeccion() + ", idGrupo: " + campoDTO.idGrupo() + ")")
                        );
                    }

                    return grupoDTO;
                })
                .collect(Collectors.toList());
    }


    @Named("ordenarTablas")
    default List<TablaResponseDTO> ordenarTablas(List<TablaModel> tablas) {
        if (tablas == null) return List.of();
        return tablas.stream()
                .sorted(Comparator.comparingInt(tabla -> tabla.getOrden() != null ? tabla.getOrden() : 0))
                .map(this::mapTabla)
                .collect(Collectors.toList());
    }

    @Mapping(target = "idSeccion", source = "seccion.idSeccion")
    @Mapping(target = "campos", expression = "java(mapearCamposConSeccion(grupo))")
    GrupoCamposResponseDTO mapGrupoCampos(GrupoCamposModel grupo);

    default List<CampoSimpleResponseDTO> mapearCamposConSeccion(GrupoCamposModel grupo) {
        if (grupo.getCampos() == null) {
            return List.of();
        }

        Long idSeccion = grupo.getSeccion() != null ? grupo.getSeccion().getIdSeccion() : null;

        return grupo.getCampos().stream()
                .sorted(Comparator.comparingInt(campo -> campo.getOrden() != null ? campo.getOrden() : 0))
                .map(campo -> new CampoSimpleResponseDTO(
                        campo.getId(),
                        idSeccion,  // ← Usar el idSeccion del grupo
                        grupo.getId(),  // ← idGrupo
                        campo.getNombre(),
                        campo.getTipoDato(),
                        campo.getOrden()
                ))
                .collect(Collectors.toList());
    }

    @Mapping(target = "idSeccion", source = "seccion.idSeccion")
    @Mapping(target = "idGrupo", source = "grupo.id")
    CampoSimpleResponseDTO mapCampoSimple(CampoSimpleModel campo);


    @Mapping(target = "idSeccion", source = "seccion.idSeccion")
    @Mapping(target = "id", source = "tabla.id")
    TablaResponseDTO mapTabla(TablaModel tabla);
}