package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.create.RecetaCreateDTO;
import com.unlu.alimtrack.dtos.modify.RecetaModifyDTO;
import com.unlu.alimtrack.dtos.response.RecetaResponseDTO;
import com.unlu.alimtrack.exception.ModificacionInvalidaException;
import com.unlu.alimtrack.exception.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.RecetaModelMapper;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.models.UsuarioModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final RecetaModelMapper mapper;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> findAllRecetasResponseDTOS() {

        List<RecetaModel> recetas = recetaRepository.findAll();
        if (recetas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron recetas");
        }
        return convertToResponseDTOList(recetas);
    }

    public RecetaResponseDTO findRecetaByCodigoReceta(String codigoReceta) {
        return null;
    }

    @Transactional(readOnly = true)
    protected List<RecetaModel> findAllModelsByCodigoReceta(String codigoReceta) {
        List<RecetaModel> recetas = recetaRepository.findAllByCodigoReceta(codigoReceta);
        if (recetas.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron recetas con ese codigo " + codigoReceta);
        }
        return recetas;
    }

    @Transactional
    public RecetaResponseDTO updateReceta(String codigoReceta, RecetaModifyDTO recetaDTO) {
        RecetaModel model = findRecetaModelByCodigoReceta(codigoReceta);
        validateModification(recetaDTO);
        updateModelFromDTO(recetaDTO, model);
        saveModel(model);
        return convertToResponseDTO(model);
    }   

    protected RecetaModel findRecetaModelByCodigoReceta(String codigoReceta) {
        RecetaModel modelo = recetaRepository.findByCodigoReceta(codigoReceta);
        if (modelo == null) {
            throw new RecursoNoEncontradoException("Receta no encontrada con ID: " + codigoReceta);
        }
        return modelo;
    }

    public RecetaResponseDTO findRecetaByCodigo(String codigo){
        RecetaModel modelo = recetaRepository.findByCodigoReceta(codigo);
        if (modelo == null) {
            throw new RecursoNoEncontradoException("Receta no encontrada con ID: " + codigo);
        }
        return convertToResponseDTO(modelo);
    }


    private void updateModelFromDTO(RecetaModifyDTO recetaDTO, RecetaModel model) {
        mapper.updateModelFromModifyDTO(recetaDTO, model);
    }

    private void validateModification(RecetaModifyDTO receta) {
        if (receta.nombre() != null) {
            validateNombre(receta.nombre());
        }
        if (receta.descripcion() != null) {
            validateDescripcion(receta.descripcion());
        }
    }

    private void validateNombre(String nombre) {
        if (nombre.isBlank()) {
            throw new ModificacionInvalidaException("El nombre no puede estar vacío");
        }
        if (nombre.length() < 2 || nombre.length() > 100) {
            throw new ModificacionInvalidaException("Nombre debe tener 2-100 caracteres");
        }
    }

    private void validateDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 255) {
            throw new ModificacionInvalidaException("Descripción no puede exceder 255 caracteres");
        }
    }

    public void deleteRecetaByID(Long id) {
        recetaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada con ID: " + id));
        recetaRepository.deleteById(id);
    }

    @Transactional
    public void deleteRecetaByCodigoReceta(String codigo) {
        RecetaModel receta = findRecetaModelByCodigoReceta(codigo);
        recetaRepository.delete(receta);
    }

    private RecetaResponseDTO saveAndReturnResponse(RecetaModel model) {
        recetaRepository.save(model);
        return mapper.recetaModeltoRecetaResponseDTO(model);
    }

    private void saveModel(RecetaModel model) {
        recetaRepository.save(model);
    }

    @Transactional(readOnly = true)
    protected List<RecetaModel> findAllByCreadoPorId(Long id) {
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

        UsuarioModel usuario = usuarioService.getUsuarioModelById(receta.idUsuarioCreador());
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario no existe con id: " + receta.idUsuarioCreador());
        }

        model = mapper.recetaCreateDTOtoModel(receta);

        recetaRepository.save(model);

        return mapper.recetaModeltoRecetaResponseDTO(model);
    }

    private List<RecetaResponseDTO> convertToResponseDTOList(List<RecetaModel> recetas) {
        return recetas.stream().map(mapper::recetaModeltoRecetaResponseDTO).collect(Collectors.toList());
    }

    private RecetaResponseDTO convertToResponseDTO(RecetaModel receta) {
        return mapper.recetaModeltoRecetaResponseDTO(receta);
    }

}