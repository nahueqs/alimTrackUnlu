package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.request.versionRecetaCreateDto;
import com.unlu.alimtrack.mappers.VersionRecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public VersionRecetaService(VersionRecetaRespository versionRecetaRespository,
                                RecetaService recetaService, VersionRecetaModelMapper versionRecetaModelMapper, UsuarioService usuarioService) {
        this.versionRecetaRespository = versionRecetaRespository;
        this.recetaService = recetaService;
        this.versionRecetaModelMapper = versionRecetaModelMapper;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<versionRecetaCreateDto> getAllVersiones() {
        List<VersionRecetaModel> versiones = versionRecetaRespository.findAll();
        return versiones.stream().map(
                VersionRecetaModelMapper.mapper::toVersionRecetaDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public versionRecetaCreateDto getVersionById(Long idReceta, Long idVersion) {
        VersionRecetaModel model = versionRecetaRespository.findByIdRecetaPadreAndIdVersion(idReceta, idVersion);
        return VersionRecetaModelMapper.mapper.toVersionRecetaDto(model);
    }

    @Transactional(readOnly = true)
    public List<versionRecetaCreateDto> getVersionesByIdReceta(Long idReceta) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.getVersionesByIdReceta(idReceta);
        return versiones.stream().map(
                VersionRecetaModelMapper.mapper::toVersionRecetaDto).collect(Collectors.toList());
    }
//.orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
    @Transactional
    public versionRecetaCreateDto saveVersionReceta(Long idReceta, versionRecetaCreateDto dto) {
        // si no tiene receta padre tira exception
        RecetaModel modelRecetaPadre = recetaService.getRecetaModelById(idReceta);
        // obtengo usuario model usando dto.idCreadoPor
        UsuarioModel usuarioCreador = usuarioService.getUsuarioModelById(dto.getIdCreadoPor());
        // mapeo manualmente el dto a un nuevo model
        VersionRecetaModel versionModelFinal = new VersionRecetaModel();
        versionModelFinal.setRecetaPadre(modelRecetaPadre);
        versionModelFinal.setCreadoPor(usuarioCreador);
        versionModelFinal.setFechaCreacion(dto.getFechaCreacion());
        versionRecetaRespository.save(versionModelFinal);
        return dto;
    }
}
