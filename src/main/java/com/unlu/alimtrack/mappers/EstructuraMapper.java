package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.*;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EstructuraMapper {

    // --- Mapeos Principales ---

    List<SeccionResponseDTO> toSeccionResponseDTOList(List<SeccionModel> secciones);


    @Mapping(target = "camposSimples", source = "camposSimples")
    @Mapping(target = "gruposCampos", source = "gruposCampos")
    @Mapping(target = "tablas", source = "tablas")
    @Mapping(target = "codigoVersion", source = "versionRecetaPadre.codigoVersionReceta")
    @Mapping(target = "id", source = "id")
    SeccionResponseDTO toSeccionResponseDTO(SeccionModel seccion);

    // --- Mapeos para Campos Simples ---
    List<CampoSimpleResponseDTO> toCampoSimpleResponseDTOList(List<CampoSimpleModel> campos);

    @Mapping(target = "idSeccion", source = "seccion.id")
    @Mapping(target = "idGrupo", source = "grupo.id")
    @Mapping(target = "tipoDato", source = "tipoDato", qualifiedByName = "tipoDatoToString")
    CampoSimpleResponseDTO toCampoSimpleResponseDTO(CampoSimpleModel campo);

    // --- Mapeos para Grupos de Campos ---
    List<GrupoCamposResponseDTO> toGrupoCamposResponseDTOList(List<GrupoCamposModel> grupos);

    @Mapping(target = "idSeccion", source = "seccion.id")
    @Mapping(target = "campos", source = "campos")
    GrupoCamposResponseDTO toGrupoCamposResponseDTO(GrupoCamposModel grupo);

    // --- Mapeos para Tablas ---
    List<TablaResponseDTO> toTablaResponseDTOList(List<TablaModel> tablas);

    @Mapping(target = "idSeccion", source = "seccion.id")
    @Mapping(target = "columnas", source = "columnas")
    @Mapping(target = "filas", source = "filas")
    TablaResponseDTO toTablaResponseDTO(TablaModel tabla);

    // --- Mapeos para Columnas de Tabla ---
    List<ColumnaTablaResponseDTO> toColumnaTablaResponseDTOList(List<ColumnaTablaModel> columnas);

    @Mapping(target = "idTabla", source = "tabla.id")
    ColumnaTablaResponseDTO toColumnaTablaResponseDTO(ColumnaTablaModel columna);

    // --- Mapeos para Filas de Tabla ---
    List<FilaTablaResponseDTO> toFilaTablaResponseDTOList(List<FilaTablaModel> filas);

    @Mapping(target = "idTabla", source = "tabla.id")
    FilaTablaResponseDTO toFilaTablaResponseDTO(FilaTablaModel fila);

    // --- Método de conversión para TipoDato ---
    @Named("tipoDatoToString")
    default String tipoDatoToString(TipoDatoCampo tipoDato) {
        return tipoDato != null ? tipoDato.getValue() : null;
    }
}
