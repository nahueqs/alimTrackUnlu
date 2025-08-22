package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.UsuarioModelMapper;
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
    private final UsuarioModelMapper usuarioModelMapper;

    public VersionRecetaService(VersionRecetaRespository versionRecetaRespository,
                                RecetaService recetaService, VersionRecetaModelMapper versionRecetaModelMapper, UsuarioService usuarioService, UsuarioModelMapper usuarioModelMapper) {
        this.versionRecetaRespository = versionRecetaRespository;
        this.recetaService = recetaService;
        this.versionRecetaModelMapper = versionRecetaModelMapper;
        this.usuarioService = usuarioService;
        this.usuarioModelMapper = usuarioModelMapper;
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
    public VersionRecetaResponseDTO saveVersionReceta(Long idRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDto) {
        // si no tiene receta padre tira exception
        RecetaModel modelRecetaPadre = recetaService.getRecetaModelById(idRecetaPadre);
        if (modelRecetaPadre == null) {
            throw new RecursoNoEncontradoException("Receta padre no encontrada");
        }
        // obtengo usuario model usando dto.idCreadoPor
        UsuarioModel modelUsuarioCreador = usuarioService.getUsuarioModelById(versionRecetaCreateDto.idUsuarioCreador());
        if (modelUsuarioCreador == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        // mapeo el dto a un nuevo model
        VersionRecetaModel versionModelFinal;
        versionModelFinal = versionRecetaModelMapper.toVersionRecetaModel(versionRecetaCreateDto);
        versionModelFinal.setRecetaPadre(modelRecetaPadre);
        if (versionRecetaCreateDto.codigoVersionReceta() == null) {
            versionModelFinal.setCodigoVersionReceta(generarCodigoUnicoVersionReceta());
        }

        versionRecetaRespository.save(versionModelFinal);

        return versionRecetaModelMapper.toVersionRecetaResponseDTO(versionModelFinal);
    }

    public  List<VersionRecetaModel> findAllByCreadoPorId(Long id) {
        return versionRecetaRespository.findAllByCreadaPorId(id);
    }

    private String generarCodigoUnicoVersionReceta() {
        // RC- + 4 dígitos aleatorios
        return "V-" + String.format("%04d", (int) (Math.random() * 10000));
    }

    public VersionRecetaModel getVersionByCodigo(String codigoVersionReceta) {
        VersionRecetaModel model = versionRecetaRespository.findByCodigoVersionReceta(codigoVersionReceta);
        if (model == null) {
            throw new RecursoNoEncontradoException("No existe ninguna version con el codigo " + codigoVersionReceta);
        }
        return model;
    }
}
