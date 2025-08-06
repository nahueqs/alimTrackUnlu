package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.request.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VersionRecetaService {

    private final VersionRecetaRespository versionRecetaRespository;
    private final RecetaService recetaService;
    private final VersionRecetaModelMapper versionRecetaModelMapper;
    private final UsuarioService usuarioService;

    public VersionRecetaService(VersionRecetaRespository versionRecetaRespository,
                                RecetaService recetaService, VersionRecetaModelMapper versionRecetaModelMapper, UsuarioService usuarioService) {
        this.versionRecetaRespository = versionRecetaRespository;
        this.recetaService = recetaService;
        this.versionRecetaModelMapper = versionRecetaModelMapper;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaResponseDTO> getAllVersiones() {
        List<VersionRecetaModel> versiones = versionRecetaRespository.findAll();
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay recetas guardadas");
        }
        return versiones.stream().map(
                VersionRecetaModelMapper.mapper::toVersionRecetaResponseDTO).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public VersionRecetaResponseDTO getVersionById(Long idReceta, Long idVersion) {
        VersionRecetaModel model = versionRecetaRespository.findByIdRecetaPadreAndIdVersion(idReceta, idVersion);
        if (model == null) {
            throw new RecursoNoEncontradoException("La versión no existe");
        }
        return VersionRecetaModelMapper.mapper.toVersionRecetaResponseDTO(model);
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaResponseDTO> getVersionesByIdRecetaPadre(Long idReceta) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.getVersionesByIdRecetaPadre(idReceta);
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay recetas guardadas");
        }
        return versiones.stream().map(
                VersionRecetaModelMapper.mapper::toVersionRecetaResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public VersionRecetaResponseDTO saveVersionReceta(Long idReceta, VersionRecetaCreateDTO versionRecetaCreateDto) {
        // si no tiene receta padre tira exception
        RecetaModel modelRecetaPadre = recetaService.getRecetaModelById(idReceta);
        if (modelRecetaPadre == null) { throw new RecursoNoEncontradoException("Receta padre no encontrada"); }
        // obtengo usuario model usando dto.idCreadoPor
        UsuarioModel modelUsuarioCreador = usuarioService.getUsuarioModelById(versionRecetaCreateDto.idUsuarioCreador());
        if (modelUsuarioCreador == null) {throw new RecursoNoEncontradoException("Usuario no encontrado");}
        // mapeo manualmente el dto a un nuevo model
        VersionRecetaModel versionModelFinal = new VersionRecetaModel();
        versionModelFinal.setRecetaPadre(modelRecetaPadre);
        versionModelFinal.setCreadoPor(modelUsuarioCreador);
        versionModelFinal.setFechaCreacion(versionRecetaCreateDto.fechaCreacion());
        versionRecetaRespository.save(versionModelFinal);

        return versionRecetaModelMapper.toVersionRecetaResponseDTO(versionModelFinal);
    }
}
