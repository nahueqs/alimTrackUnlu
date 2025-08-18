package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final RecetaModelMapper mapper;
    private final UsuarioService usuarioService;

    public RecetaService(RecetaRepository recetaRepository, RecetaModelMapper mapper, UsuarioService usuarioService) {
        this.recetaRepository = recetaRepository;
        this.mapper = mapper;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> getAllRecetasResponseDTOS() {

        List<RecetaModel> recetas = recetaRepository.findAll();
        if (recetas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron recetas");
        }

        return recetas.stream().map(
                mapper::recetaModeltoRecetaResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecetaResponseDTO getRecetaResponseDTOById(Long id) {
        RecetaModel recetaModel = recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        return mapper.recetaModeltoRecetaResponseDTO(recetaModel);
    }

    @Transactional(readOnly = true)
    public RecetaModel getRecetaModelById(Long id) {
        return recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
    }

    public RecetaResponseDTO updateReceta(RecetaModifyDTO receta) {
        RecetaModel model = recetaRepository.findByCodigoReceta((receta.codigoReceta()));
        if (model == null) {
            throw new RecursoNoEncontradoException("Receta no encontrada con ID: " + receta.codigoReceta());
        }

        mapper.updateModelFromCreateDTO(receta, model);
        recetaRepository.save(model);
        return mapper.recetaModeltoRecetaResponseDTO(model);
    }

    public void deleteRecetaByID(Long id) {
        recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        recetaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<RecetaModel> findAllByCreadoPorId(Long id) {
        if (usuarioService.getUsuarioModelById(id) == null) {
            throw new RecursoNoEncontradoException("Usuario no existente");
        }
        return recetaRepository.findAllByCreadoPorId(id);

    }

    private String generarCodigoUnicoReceta() {
        // RC- + 4 dígitos aleatorios
        return "RC-" + String.format("%04d", (int) (Math.random() * 10000));
    }

    public RecetaResponseDTO addReceta(RecetaCreateDTO receta) {
        RecetaModel model = recetaRepository.findByCodigoReceta((receta.codigoReceta()));
        if (model != null) {
            throw new RecursoNoEncontradoException("Receta ya existente con codigo: " + receta.codigoReceta());
        }

        model = mapper.recetaCreateDTOtoModel(receta);

        return mapper.recetaModeltoRecetaResponseDTO(model);
    }
}
