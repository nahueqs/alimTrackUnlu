package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.DTOS.create.ProduccionCreateDTO;
import com.unlu.alimtrack.DTOS.modify.ProduccionMetadataModifyRequestDTO;
import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import com.unlu.alimtrack.exceptions.ModificacionInvalidaException;
import com.unlu.alimtrack.exceptions.OperacionNoPermitida;
import com.unlu.alimtrack.exceptions.RecursoDuplicadoException;
import com.unlu.alimtrack.exceptions.RecursoNoEncontradoException;
import com.unlu.alimtrack.models.CampoSimpleModel;
import com.unlu.alimtrack.models.ProduccionModel;
import com.unlu.alimtrack.models.TablaModel;
import com.unlu.alimtrack.models.VersionRecetaModel;
import com.unlu.alimtrack.repositories.CampoSimpleRepository;
import com.unlu.alimtrack.repositories.ProduccionRepository;
import com.unlu.alimtrack.repositories.TablaRepository;
import com.unlu.alimtrack.services.ProduccionQueryService;
import com.unlu.alimtrack.services.UsuarioService;
import com.unlu.alimtrack.services.VersionRecetaQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductionManagerServiceValidator {

    private final ProduccionQueryService produccionQueryService;
    private final VersionRecetaQueryService versionRecetaQueryService;
    private final CampoSimpleRepository campoSimpleRepository;
    private final ProduccionRepository produccionRepository;
    private final UsuarioService usuarioService;
    private final TablaRepository tablaRepository;

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
        verificarUsuarioExisteYEstaActivoByEmail(createDTO.emailCreador());
        verificarCodigoProduccionNoExiste(createDTO.codigoProduccion());
        verificarVersionExiste(createDTO.codigoVersionReceta());
        log.debug("Validaciones para la creación de la producción {} superadas", createDTO.codigoProduccion());
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
        if (!usuarioService.existsByEmail(email)) { // Changed to usuarioService
            log.warn("Intento de crear producción con un usuario no existente: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado no existe: " + email);
        }
        if (!usuarioService.estaActivoByEmail(email)) { // Changed to usuarioService
            log.warn("Intento de crear producción con un usuario inactivo: {}", email);
            throw new OperacionNoPermitida("El usuario creador especificado se encuentra inactivo: " + email);
        }
    }

    public CampoSimpleModel validarCampoExiste(Long idCampo) {
        log.debug("Validando existencia del campo con ID: {}", idCampo);

        CampoSimpleModel campo = campoSimpleRepository.findById(idCampo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Campo no encontrado con ID: " + idCampo));

        log.debug("Campo encontrado : {}", campo);
        log.debug("ID SECCION {}", campo.getSeccion().toString());


        return campo;
    }

    public void combinacionFilaColumnaPerteneceTabla(Long idFila, Long idColumna, Long idTabla) {
        validarFilaPerteneceATabla(idFila, idTabla);
        validarColumnaPerteneceATabla(idColumna, idTabla);
    }

    private TablaModel findTablaModelById(Long idTabla) {
        return tablaRepository.findById(idTabla).orElseThrow(
                () -> new RecursoNoEncontradoException("Tabla no encontrada con ID: " + idTabla)
        );
    }

    private void validarColumnaPerteneceATabla(Long idColumna, Long idTabla) {
        TablaModel tablaModel = findTablaModelById(idTabla);
        if (!tablaModel.getColumnas().stream().anyMatch(columna -> columna.getId().equals(idColumna))) {
            throw new ModificacionInvalidaException("La columna no pertenece a la tabla");
        }

    }

    private void validarFilaPerteneceATabla(Long idFila, Long idTabla) {
        TablaModel tablaModel = findTablaModelById(idTabla);
        if (!tablaModel.getFilas().stream().anyMatch(fila -> fila.getId().equals(idFila))) {
            throw new ModificacionInvalidaException("La fila no pertenece a la tabla");
        }

    }


    public void validarTablaPertenceAVersionProduccion(VersionRecetaModel version, Long idTabla) {

        TablaModel tablaModel = findTablaModelById(idTabla);

        if (!tablaModel.getSeccion().getVersionRecetaPadre().equals(version)) {
            throw new ModificacionInvalidaException("La tabla no pertenece a la version que intenta modificar.");
        }

    }

    public void validarUpdateMetadata(String codigoProduccion, ProduccionMetadataModifyRequestDTO request) {
        ProduccionModel produccion = validarProduccionParaEdicion(codigoProduccion);


    }
}
