package com.unlu.alimtrack.mappers;

import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.CampoSimpleResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.GrupoCamposResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.SeccionResponseDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.publico.estructura.TablaResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.GrupoCamposModel;
import com.unlu.alimtrack.models.SeccionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SeccionMapperManual {

    @Autowired
    private TablaMapperManual tablaMapperManual;

    public SeccionResponseDTO toResponseDTO(SeccionModel seccion) {
        if (seccion == null) {
            return null;
        }

        String codigoVersion = seccion.getVersionRecetaPadre() != null ?
                seccion.getVersionRecetaPadre().getCodigoVersionReceta() : null;

        // ✅ Mapear campos simples
        List<CampoSimpleResponseDTO> camposSimplesDTO = mapCamposSimples(seccion.getCamposSimples());

        // ✅ Mapear grupos de campos
        List<GrupoCamposResponseDTO> gruposCamposDTO = mapGruposCampos(seccion.getGruposCampos());

        // ✅ Mapear tablas usando el mapper manual
        List<TablaResponseDTO> tablasDTO = tablaMapperManual.toResponseDTOList(seccion.getTablas());

        return new SeccionResponseDTO(
                seccion.getId(),
                codigoVersion,
                seccion.getTitulo(),
                seccion.getOrden(),
                camposSimplesDTO,
                gruposCamposDTO,
                tablasDTO
        );
    }

    private List<CampoSimpleResponseDTO> mapCamposSimples(Set<CampoSimpleModel> campos) {
        if (campos == null || campos.isEmpty()) {
            return List.of();
        }

        // Solo campos que NO pertenecen a un grupo
        return campos.stream()
                .filter(campo -> campo.getGrupo() == null)
                .sorted(Comparator.comparingInt(campo -> campo.getOrden() != null ? campo.getOrden() : 0))
                .map(this::mapCampoSimple)
                .collect(Collectors.toList());
    }

    private CampoSimpleResponseDTO mapCampoSimple(CampoSimpleModel campo) {
        Long idSeccion = campo.getSeccion() != null ? campo.getSeccion().getId() : null;
        Long idGrupo = campo.getGrupo() != null ? campo.getGrupo().getId() : null;
        TipoDatoCampo tipoDato = campo.getTipoDato() != null ? campo.getTipoDato() : null;

        return new CampoSimpleResponseDTO(
                campo.getId(),
                idSeccion,
                idGrupo,
                campo.getNombre(),
                tipoDato != null ? tipoDato.getValue() : null,
                campo.getOrden()
        );
    }

    private List<GrupoCamposResponseDTO> mapGruposCampos(Set<GrupoCamposModel> grupos) {
        if (grupos == null || grupos.isEmpty()) {
            return List.of();
        }

        return grupos.stream()
                .sorted(Comparator.comparingInt(grupo -> grupo.getOrden() != null ? grupo.getOrden() : 0))
                .map(this::mapGrupoCampos)
                .collect(Collectors.toList());
    }

    private GrupoCamposResponseDTO mapGrupoCampos(GrupoCamposModel grupo) {
        Long idSeccion = grupo.getSeccion() != null ? grupo.getSeccion().getId() : null;

        // Mapear campos del grupo
        List<CampoSimpleResponseDTO> camposDTO = grupo.getCampos() != null ?
                grupo.getCampos().stream()
                        .sorted(Comparator.comparingInt(campo -> campo.getOrden() != null ? campo.getOrden() : 0))
                        .map(campo -> new CampoSimpleResponseDTO(
                                campo.getId(),
                                idSeccion,
                                grupo.getId(),
                                campo.getNombre(),
                                campo.getTipoDato() != null ? campo.getTipoDato().getValue() : null,
                                campo.getOrden()
                        ))
                        .collect(Collectors.toList()) : List.of();

        return new GrupoCamposResponseDTO(
                grupo.getId(),
                idSeccion,
                grupo.getSubtitulo(),
                grupo.getOrden(),
                camposDTO
        );
    }

    public List<SeccionResponseDTO> toResponseDTOList(List<SeccionModel> secciones) {
        if (secciones == null || secciones.isEmpty()) {
            return List.of();
        }

        return secciones.stream()
                .sorted(Comparator.comparingInt(seccion -> seccion.getOrden() != null ? seccion.getOrden() : 0))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
