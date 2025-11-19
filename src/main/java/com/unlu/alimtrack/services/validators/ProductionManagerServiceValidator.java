package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoIdentifierConflictException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.repositories.CampoSimpleRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.services.queries.ProduccionQueryService;
import com.unlu.alimtrack.services.queries.UsuarioQueryService;
import com.unlu.alimtrack.services.queries.VersionRecetaQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductionManagerServiceValidator {

    private final ProduccionQueryService produccionQueryService;
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final UsuarioQueryService usuarioQueryService;
    private final CampoSimpleRepository campoSimpleRepository;
    private final ProduccionRepository produccionRepository;

    public ProduccionModel validarProduccionParaEdicion(String codigoProduccion) {
        ProduccionModel produccion = produccionRepository.findByCodigoProduccion(codigoProduccion);

        if (produccion == null) {
            throw new RecursoNoEncontradoException("Producción no encontrada: " + codigoProduccion);
        }

        if (produccion.getEstado() != TipoEstadoProduccion.EN_PROCESO) {
            throw new OperacionNoPermitida(
                    "No se puede modificar una producción en estado: " + produccion.getEstado());
        }

        return produccion;
    }


    public void verificarCreacionProduccion(ProduccionCreateDTO createDTO) {
        verificarCodigoProduccionNoExiste(createDTO.codigoProduccion());
        verificarVersionExiste(createDTO.codigoVersionReceta());
        verificarUsuarioExisteYEstaActivoByEmail(createDTO.usernameCreador());
    }

    public void verificarIntegridadDatosCreacion(String codigoProduccion, ProduccionCreateDTO createDTO) {
        if (!codigoProduccion.equals(createDTO.codigoProduccion())) {
            throw new RecursoIdentifierConflictException(
                    "El codigo de la url no coincide con el cuerpo de la petición. Codigo de produccion: " + codigoProduccion
                            + " Codigo que desea crear: " + createDTO.codigoProduccion());
        }
    }

    public void verificarCodigoProduccionNoExiste(String codigoProduccion) {
        if (produccionQueryService.existsByCodigoProduccion(codigoProduccion)) {
            throw new RecursoDuplicadoException("El codigo de la producción que desea agregar ya ha sido usado.");
        }
    }

    public void verificarVersionExiste(String codigoVersion) {
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoVersion)) {
            throw new RecursoNoEncontradoException(
                    "La producción que desea agregar no corresponde a una version existente.");
        }
    }

    public void verificarUsuarioExisteYEstaActivoByEmail(String email) {
        if (!usuarioQueryService.existsByEmail(email)) {
            log.debug("Usuario no existe con email {}", email);
            throw new RecursoNoEncontradoException("Usuario no existe con id: " + email);
        }


        if (!usuarioQueryService.estaActivoByEmail(email)) {
            throw new OperacionNoPermitida(
                    "El usuario que intenta guardar la producción se encuentra inactivo. username: " + email);
        }
    }

    public CampoSimpleModel validarCampoExiste(Long idCampo) {
        return campoSimpleRepository.findById(idCampo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Campo no encontrado: " + idCampo));
    }


}
