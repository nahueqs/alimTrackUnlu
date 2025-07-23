package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDto;
import com.unlu.alimtrack.dtos.VersionRecetaDto;
import com.unlu.alimtrack.mappers.RecetaModelToRecetaDtoMapper;
import com.unlu.alimtrack.mappers.VersionRecetaModelToVersionRecetaDtoMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VersionRecetaService {
    @Autowired
    VersionRecetaRespository  versionRecetaRespository;

    @Transactional(readOnly = true)
    public List<VersionRecetaDto> getAllVersiones() {
        List<VersionRecetaModel> versiones =  versionRecetaRespository.findAll();
        return versiones.stream().filter(Objects::nonNull).map(
                VersionRecetaModelToVersionRecetaDtoMapper.mapper::versionRecetaModelToVersionRecetaDto).collect(Collectors.toList());
    }





}
