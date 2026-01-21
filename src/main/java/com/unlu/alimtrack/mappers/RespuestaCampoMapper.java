package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.RespuestaCampoResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RespuestaCampoMapper {


    @Mapping(source = "id", target = "idRespuesta")
    @Mapping(source = "idCampo.id", target = "idCampo")
    @Mapping(source = "timestamp", target = "timestamp")
    @Mapping(source = "idCampo.tipoDato", target = "tipo")
    @Mapping(source = "creadoPor.email", target = "creadoPor")
    @Mapping(target = "valor", source = "respuesta", qualifiedByName = "obtenerValorComoString")
    RespuestaCampoResponseDTO toResponseDTO(RespuestaCampoModel respuesta);


    List<RespuestaCampoResponseDTO> toResponseDTOList(List<RespuestaCampoModel> respuestas);

    @Named("obtenerValorComoString")
    default String obtenerValorComoString(RespuestaCampoModel respuesta) {
        if (respuesta == null || respuesta.getIdCampo() == null) {
            return null;
        }

        TipoDatoCampo tipo = respuesta.getIdCampo().getTipoDato();
        if (tipo == null) {
            return null;
        }

        Object valor = obtenerValorObjeto(respuesta, tipo);

        if (valor == null) {
            return null;
        }

        // Convertir a String según tipo
        return switch (tipo) {
            case TEXTO -> (String) valor;
            case DECIMAL, ENTERO -> {
                BigDecimal numero = (BigDecimal) valor;
                yield numero.toPlainString();
            }
            case FECHA -> {
                java.time.LocalDateTime fecha = (java.time.LocalDateTime) valor;
                yield fecha.toString();
            }
            case HORA -> {
                java.time.LocalDateTime hora = (java.time.LocalDateTime) valor;
                yield hora.toLocalTime().toString();
            }
            case BOOLEANO -> {
                Boolean bool = (Boolean) valor;
                yield bool.toString();
            }
            default -> valor.toString();
        };
    }

    private Object obtenerValorObjeto(RespuestaCampoModel respuesta, TipoDatoCampo tipo) {
        return switch (tipo) {
            case TEXTO -> respuesta.getValorTexto();
            case DECIMAL, ENTERO -> respuesta.getValorNumerico();
            case FECHA, HORA -> respuesta.getValorFecha();
            case BOOLEANO -> respuesta.getValorNumerico() != null ?
                    respuesta.getValorNumerico().compareTo(BigDecimal.ONE) == 0 :
                    null;
            default -> null;
        };
    }
}