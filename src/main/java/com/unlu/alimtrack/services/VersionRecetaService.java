package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.request.VersionRecetaCreateDTO;
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
    public List<VersionRecetaCreateDTO> getAllVersiones() {
        List<VersionRecetaModel> versiones = versionRecetaRespository.findAll();
        return versiones.stream().map(
                VersionRecetaModelMapper.mapper::toVersionRecetaDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VersionRecetaCreateDTO getVersionById(Long idReceta, Long idVersion) {
        VersionRecetaModel model = versionRecetaRespository.findByIdRecetaPadreAndIdVersion(idReceta, idVersion);
        return VersionRecetaModelMapper.mapper.toVersionRecetaDto(model);
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaCreateDTO> getVersionesByIdReceta(Long idReceta) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.getVersionesByIdReceta(idReceta);
        return versiones.stream().map(
                VersionRecetaModelMapper.mapper::toVersionRecetaDto).collect(Collectors.toList());
    }

    @Transactional
    public VersionRecetaCreateDTO saveVersionReceta(Long idReceta, VersionRecetaCreateDTO versionRecetaCreateDto) {
        // si no tiene receta padre tira exception
        RecetaModel modelRecetaPadre = recetaService.getRecetaModelById(idReceta);
        // obtengo usuario model usando dto.idCreadoPor
        UsuarioModel modelUsuarioCreador = usuarioService.getUsuarioModelById(versionRecetaCreateDto.idCreadoPor());
        // mapeo manualmente el dto a un nuevo model
        VersionRecetaModel versionModelFinal = new VersionRecetaModel();
        versionModelFinal.setRecetaPadre(modelRecetaPadre);
        versionModelFinal.setCreadoPor(modelUsuarioCreador);
        versionModelFinal.setFechaCreacion(versionRecetaCreateDto.fechaCreacion());
        versionRecetaRespository.save(versionModelFinal);
        return versionRecetaCreateDto;
    }
}
