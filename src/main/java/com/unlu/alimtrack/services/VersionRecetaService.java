package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.VersionRecetaCreateDTO;
import com.unlu.alimtrack.dtos.response.VersionRecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.VersionRecetaMapper;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.VersionRecetaRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VersionRecetaService {

    private final VersionRecetaRespository versionRecetaRespository;
    private final RecetaService recetaService;
    private final VersionRecetaMapper versionRecetaMapper;
    @Lazy
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<VersionRecetaResponseDTO> findAllVersiones() {
        List<VersionRecetaModel> versiones = versionRecetaRespository.findAll();
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay versiones guardadas para ninguna receta");
        }
        return versiones.stream().map(
                VersionRecetaMapper.mapper::toVersionRecetaResponseDTO).collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public VersionRecetaResponseDTO findVersionByIdRecetaAndIdVersion(Long idReceta, Long idVersion) {
        VersionRecetaModel model = versionRecetaRespository.findByIdRecetaPadreAndIdVersion(idReceta, idVersion);
        if (model == null) {
            throw new RecursoNoEncontradoException("La versión no existe");
        }
        return VersionRecetaMapper.mapper.toVersionRecetaResponseDTO(model);
    }

    @Transactional(readOnly = true)
    public VersionRecetaResponseDTO findVersionRecetaByCodigoVersion(String codigoReceta, String CodigoVersion) {
        VersionRecetaModel model = versionRecetaRespository.findByCodigoVersionReceta(CodigoVersion);
        if (model == null) {
            throw new RecursoNoEncontradoException("No hay versiones guardadas con ese código");
        }
        return VersionRecetaMapper.mapper.toVersionRecetaResponseDTO(model);
    }

    @Transactional(readOnly = true)
    public List<VersionRecetaResponseDTO> findAllVersionesByIdRecetaPadre(Long idReceta) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.getVersionesByIdRecetaPadre(idReceta);
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay recetas guardadas");
        }
        return versiones.stream().map(
                VersionRecetaMapper.mapper::toVersionRecetaResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    protected List<VersionRecetaModel> findAllVersionesByCodigoRecetaPadre(String codigoRecetaPadre) {
        List<VersionRecetaModel> versiones = versionRecetaRespository.findAllVersionesByCodigoRecetaPadre(codigoRecetaPadre);
        if (versiones.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay versiones guardadas para la receta codigo " + codigoRecetaPadre);
        }
        return versiones;
    }

    @Transactional
    public VersionRecetaResponseDTO saveVersionReceta(String codigoRecetaPadre, VersionRecetaCreateDTO versionRecetaCreateDto) {

        if (!recetaService.existsByCodigoReceta(codigoRecetaPadre)) {
            throw new RecursoNoEncontradoException("Receta padre no encontrada");
        }
        // obtengo usuario model usando dto.idCreadoPor
        UsuarioModel modelUsuarioCreador = usuarioService.getUsuarioModelById(versionRecetaCreateDto.idUsuarioCreador());
        if (modelUsuarioCreador == null) {
            throw new RecursoNoEncontradoException("Usuario no encontrado");
        }

        // mapeo el dto a un nuevo model
        VersionRecetaModel versionModelFinal;
        versionModelFinal = versionRecetaMapper.toVersionRecetaModel(versionRecetaCreateDto);
        //versionModelFinal.setRecetaPadre(modelRecetaPadre);
        if (versionRecetaCreateDto.codigoVersionReceta() == null) {
            versionModelFinal.setCodigoVersionReceta(generarCodigoUnicoVersionReceta());
        }

        versionRecetaRespository.save(versionModelFinal);

        return versionRecetaMapper.toVersionRecetaResponseDTO(versionModelFinal);
    }

    protected List<VersionRecetaModel> findAllByCreadoPorId(Long id) {
        return versionRecetaRespository.findAllByCreadaPorId(id);
    }

    private String generarCodigoUnicoVersionReceta() {
        // RC- + 4 dígitos aleatorios
        return "V-" + String.format("%04d", (int) (Math.random() * 10000));
    }

    protected VersionRecetaModel findVersionModelByCodigo(String codigoVersionReceta) {
        VersionRecetaModel model = versionRecetaRespository.findByCodigoVersionReceta(codigoVersionReceta);
        if (model == null) {
            throw new RecursoNoEncontradoException("No existe ninguna version con el codigo " + codigoVersionReceta);
        }
        return model;
    }

    private VersionRecetaResponseDTO converToResponseDTO(VersionRecetaModel model) {
        return versionRecetaMapper.toVersionRecetaResponseDTO(model);
    }

    public VersionRecetaResponseDTO findVersionRecetaByCodigoVersionAndCodigoReceta(String codigoVersion, String codigoReceta) {
        return null;
    }

    public List<VersionRecetaResponseDTO> findAllVersionesByCodigoReceta(String codigoReceta) {

        return null;
    }

    public List<VersionRecetaResponseDTO> findAllByCreadoPorUsername(String username) {
        return null;
    }
}
