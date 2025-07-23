package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.dtos.VersionRecetaDto;
import com.unlu.alimtrack.mappers.VersionRecetaModelToDtoMapper;
import com.unlu.alimtrack.models.RecetaModel;
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

    @Autowired
    public VersionRecetaService(VersionRecetaRespository versionRecetaRespository,
                                RecetaService recetaService) {
        this.versionRecetaRespository = versionRecetaRespository;
        this.recetaService = recetaService;
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaDto> getAllVersiones() {
        List<VersionRecetaModel> versiones =  versionRecetaRespository.findAll();
        return versiones.stream().map(
                VersionRecetaModelToDtoMapper.mapper::versionRecetaModelToVersionRecetaDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VersionRecetaDto getVersionById(Long idReceta, Long idVersion) {
        VersionRecetaModel model = versionRecetaRespository.findByIdRecetaPadreAndIdVersion(idReceta,idVersion);
        return VersionRecetaModelToDtoMapper.mapper.versionRecetaModelToVersionRecetaDto(model);
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaDto> getVersionesByIdReceta(Long idReceta) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.getVersionesByIdReceta(idReceta);
        return versiones.stream().map(
                VersionRecetaModelToDtoMapper.mapper::versionRecetaModelToVersionRecetaDto).collect(Collectors.toList());
    }

    public VersionRecetaDto saveVersionReceta(Long idReceta, VersionRecetaDto dto) {
        RecetaDto dtoReceta = recetaService.getRecetaDtoById(idReceta);
        VersionRecetaModelToDtoMapper mapper = VersionRecetaModelToDtoMapper.mapper;
        VersionRecetaModel model = mapper.versionRecetaDtoToVersionRecetaModel(dto);
        versionRecetaRespository.save(model);
        return dto;
    }
}
