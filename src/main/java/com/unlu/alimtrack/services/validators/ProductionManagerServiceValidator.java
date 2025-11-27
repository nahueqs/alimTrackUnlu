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
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import com.unlu.alimtrack.services.impl.UsuarioServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductionManagerServiceValidator {

    private final ProduccionQueryService produccionQueryService;
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final CampoSimpleRepository campoSimpleRepository;
    private final ProduccionRepository produccionRepository;
    @Lazy
    private final UsuarioServiceImpl usuarioServiceImpl;

    public ProduccionModel validarProduccionParaEdicion(String codigoProduccion) {
        log.debug("Validando si la producción {} es editable", codigoProduccion);
        ProduccionModel produccion = produccionRepository.findByCodigoProduccion(codigoProduccion)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producción no encontrada: " + codigoProduccion));

        if (produccion.getEstado() != TipoEstadoProduccion.EN_PROCESO) {
            log.warn("Intento de modificar la producción {} que no está en estado EN_PROCESO (estado actual: {})", codigoProduccion, produccion.getEstado());
            throw new OperacionNoPermitida("No se puede modificar una producción en estado: " + produccion.getEstado());
        }
        log.debug("La producción {} es válida para edición", codigoProduccion);
        return produccion;
    }

    public void verificarCreacionProduccion(ProduccionCreateDTO createDTO) {
        log.debug("Iniciando validaciones para la creación de la producción {}", createDTO.codigoProduccion());
        verificarCodigoProduccionNoExiste(createDTO.codigoProduccion());
        verificarVersionExiste(createDTO.codigoVersionReceta());
        verificarUsuarioExisteYEstaActivoByEmail(createDTO.emailCreador());
        log.debug("Validaciones para la creación de la producción {} superadas", createDTO.codigoProduccion());
    }

    public void verificarIntegridadDatosCreacion(String codigoProduccion, ProduccionCreateDTO createDTO) {
        if (!codigoProduccion.equals(createDTO.codigoProduccion())) {
            throw new RecursoIdentifierConflictException(
                    "El código de la URL no coincide con el del cuerpo de la petición. URL: " + codigoProduccion
                            + ", Cuerpo: " + createDTO.codigoProduccion());
        }
    }

    private void verificarCodigoProduccionNoExiste(String codigoProduccion) {
        if (produccionQueryService.existsByCodigoProduccion(codigoProduccion)) {
            throw new RecursoDuplicadoException("El código de producción '" + codigoProduccion + "' ya está en uso.");
        }
    }

    private void verificarVersionExiste(String codigoVersion) {
        if (!versionRecetaQueryService.existsByCodigoVersion(codigoVersion)) {
            throw new RecursoNoEncontradoException("La versión de receta especificada no existe: " + codigoVersion);
        }
    }

    private void verificarUsuarioExisteYEstaActivoByEmail(String email) {
        if (!usuarioServiceImpl.existsByEmail(email)) {
            log.warn("Intento de crear producción con un usuario no existente: {}", email);
            throw new RecursoNoEncontradoException("El usuario creador especificado no existe: " + email);
        }
        if (!usuarioServiceImpl.estaActivoByEmail(email)) {
            log.warn("Intento de crear producción con un usuario inactivo: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado se encuentra inactivo: " + email);
        }
    }

    public CampoSimpleModel validarCampoExiste(Long idCampo) {
        log.debug("Validando existencia del campo con ID: {}", idCampo);
        return campoSimpleRepository.findById(idCampo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Campo no encontrado con ID: " + idCampo));
    }
}
