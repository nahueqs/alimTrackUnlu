package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.DTOS.request.ProduccionFilterRequestDTO;
import com.unlu.alimtrack.DTOS.response.VersionReceta.ProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.mappers.ProduccionMapper;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.validators.ProduccionQueryServiceValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProduccionQueryServiceImpl implements ProduccionQueryService {

    private final ProduccionRepository produccionRepository;
    private final ProduccionMapper produccionMapper;
    private final ProduccionQueryServiceValidator produccionQueryServiceValidator;


    public ProduccionResponseDTO findByCodigoProduccion(String codigo) {
        ProduccionModel model = produccionRepository.findByCodigoProduccion(codigo);
        if (model == null) {
            throw new RecursoNoEncontradoException("Produccion no encontrada con ID: " + codigo);
        }
        return produccionMapper.modelToResponseDTO(model);
    }

    public List<ProduccionResponseDTO> findAllByFilters(@Valid ProduccionFilterRequestDTO filtros) {
        List<ProduccionModel> producciones = buscarProduccionesPorFiltros(filtros);
        return produccionMapper.modelListToResponseDTOList(producciones);
    }

    private List<ProduccionModel> buscarProduccionesPorFiltros(ProduccionFilterRequestDTO filtros) {
        LocalDateTime fechaInicio = produccionQueryServiceValidator.convertToStartOfDay(filtros.fechaInicio());
        LocalDateTime fechaFin = produccionQueryServiceValidator.convertToEndOfDay(filtros.fechaFin());

        TipoEstadoProduccion estado = filtros.estado() != null
                ? TipoEstadoProduccion.valueOf(filtros.estado().toUpperCase())
                : null;

        return produccionRepository.findByAdvancedFilters(
                filtros.codigoVersionReceta(),
                filtros.lote(),
                filtros.encargado(),
                estado,
                fechaInicio,
                fechaFin
        );
    }


    @Override
    public boolean existsByVersionRecetaPadre(String codigoReceta) {
        return produccionRepository.existsByVersionReceta_CodigoVersionReceta(codigoReceta);
    }

    @Override
    public boolean existsByCodigoProduccion(String codigoProduccion) {
        return produccionRepository.existsByCodigoProduccion(codigoProduccion);
    }

}
