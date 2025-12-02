package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.Usuario.UsuarioResponseDTO;
import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCeldaTablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = RespuestaTablaModel.class)
public interface RespuestaTablaMapper {


    @Mapping(target = "idTabla", source = "idTabla.id")
    @Mapping(target = "idFila", source = "fila.id")
    @Mapping(target = "idColumna", source = "columna.id")
    @Mapping(target = "tipoDatoColumna", source = "columna.tipoDato", qualifiedByName = "tipoDatoToString")
    @Mapping(target = "nombreFila", source = "fila.nombre")
    @Mapping(target = "nombreColumna", source = "columna.nombre")
    @Mapping(target = "timestampRespuesta", source = "timestamp")
    RespuestaCeldaTablaResponseDTO toResponseDTO(RespuestaTablaModel respuesta);

    List<RespuestaCeldaTablaResponseDTO> toResponseDTOList(List<RespuestaTablaModel> respuestas);


    UsuarioResponseDTO toResponseDTO(UsuarioModel model);

    @Named("tipoDatoToString")
    default String convertirTipoDatoAString(TipoDatoCampo tipoDato) {
        return tipoDato != null ? tipoDato.name() : null;
    }
}
