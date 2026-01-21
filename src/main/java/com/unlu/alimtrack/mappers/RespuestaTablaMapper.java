package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RespuestaTablaMapper {

    @Mapping(target = "idTabla", source = "tabla.id")
    @Mapping(target = "idFila", source = "fila.id")
    @Mapping(target = "idColumna", source = "columna.id")
    @Mapping(target = "tipoDatoColumna", source = "columna.tipoDato", qualifiedByName = "tipoDatoToString")
    @Mapping(target = "nombreFila", source = "fila.nombre")
    @Mapping(target = "nombreColumna", source = "columna.nombre")
    @Mapping(target = "valor", source = "respuesta", qualifiedByName = "obtenerValor")
    @Mapping(target = "timestampRespuesta", source = "timestamp")
    RespuestaCeldaTablaResponseDTO toResponseDTO(RespuestaTablaModel respuesta);

    List<RespuestaCeldaTablaResponseDTO> toResponseDTOList(List<RespuestaTablaModel> respuestas);

    @Named("tipoDatoToString")
    default String convertirTipoDatoAString(TipoDatoCampo tipoDato) {
        return tipoDato != null ? tipoDato.toString() : null;
    }

    @Named("obtenerValor")
    default String obtenerValor(RespuestaTablaModel respuesta) {
        if (respuesta == null || respuesta.getColumna() == null) {
            return null;
        }

        TipoDatoCampo tipo = respuesta.getColumna().getTipoDato();
        Object valor = respuesta.getValor(tipo);

        if (valor == null) {
            return null;
        }

        // Convertir según tipo
        if (tipo == TipoDatoCampo.BOOLEANO && valor instanceof Boolean) {
            return ((Boolean) valor) ? "true" : "false";
        }

        return valor.toString();
    }


}