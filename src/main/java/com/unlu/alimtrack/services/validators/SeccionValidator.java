package com.unlu.alimtrack.services.validators;

import com.unlu.alimtrack.DTOS.create.secciones.SeccionCreateDTO;
import com.unlu.alimtrack.exceptions.ValidationException;
import com.unlu.alimtrack.services.queries.SeccionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Validador para operaciones relacionadas con estructura
 */
@Component
@RequiredArgsConstructor
@Lazy
public class SeccionValidator {


    private final SeccionQueryService seccionQueryService;


    /**
     * Valida los parámetros para la creación de una sección
     *
     * @param codigoVersion Versión de receta a la que pertenecerá la sección
     * @param seccionDTO    Datos de la sección a validar
     * @throws ValidationException si la validación falla
     */
    public void validarCreacionSeccion(String codigoVersion, SeccionCreateDTO seccionDTO) {
        validarConsistenciaSeccion(codigoVersion, seccionDTO);
        validarUnicidadTituloEnVersion(codigoVersion, seccionDTO.titulo());
        validarUnicidadOrdenEnVersion(codigoVersion, seccionDTO.orden());
    }

    private void validarUnicidadOrdenEnVersion(String codigoVersion, Integer orden) {
        // Validar unicidad del orden dentro de la versión de receta
        if (seccionQueryService.existsByVersionRecetaPadreAndOrden(codigoVersion, orden)) {
            throw new ValidationException(
                    String.format("Ya existe una sección con el orden '%d' en esta versión de receta",
                            orden)
            );
        }
    }

    private void validarUnicidadTituloEnVersion(String codigoVersion, String titulo) {
        // Validar unicidad del título dentro de la versión de receta
        if (seccionQueryService.existsByVersionRecetaPadreAndTitulo(codigoVersion, titulo)) {
            throw new ValidationException(
                    String.format("Ya existe una sección con el título '%s' en esta versión de receta",
                            titulo)
            );
        }
    }

    private void validarConsistenciaSeccion(String codigoVersion, SeccionCreateDTO seccionDTO) {
        if (!codigoVersion.equals(seccionDTO.codigoVersionRecetaPadre())) {
            throw new ValidationException("La version de receta no coincide con la de la seccion");
        }
    }

//    /**
//     * Valida los parámetros para la actualización de una sección
//     *
//     * @param id           ID de la sección a actualizar
//     * @param codigoReceta Versión de receta a la que pertenece la sección
//     * @param seccionDTO   Datos actualizados de la sección
//     * @throws ValidationException si la validación falla
//     */
//    public void validarActualizacionSeccion(Long id, String codigoReceta, SeccionResponseDTO seccionDTO) {
//        if (id == null) {
//            throw new ValidationException("El ID de la sección es obligatorio");
//        }
//
//        validarCreacionSeccion(codigoReceta, seccionDTO);
//
//        // Validar que si se cambia el título, no exista otro con el mismo título en la misma versión
//        seccionService.obtenerSeccionesPorVersion(versionReceta).stream()
//                .filter(seccion -> !seccion.getIdSeccion().equals(id)) // Excluir la sección actual
//                .filter(seccion -> seccion.getTitulo().equals(seccionDTO.getNombre()))
//                .findFirst()
//                .ifPresent(seccion -> {
//                    throw new ValidationException(
//                            String.format("Ya existe otra sección con el título '%s' en esta versión de receta",
//                                    seccionDTO.getNombre())
//                    );
//                });
//
//        // Validar que si se cambia el orden, no exista otro con el mismo orden en la misma versión
//        seccionService.obtenerSeccionesPorVersion(versionReceta).stream()
//                .filter(seccion -> !seccion.getIdSeccion().equals(id)) // Excluir la sección actual
//                .filter(seccion -> seccion.getOrden().equals(seccionDTO.getOrden()))
//                .findFirst()
//                .ifPresent(seccion -> {
//                    throw new ValidationException(
//                            String.format("Ya existe otra sección con el orden '%d' en esta versión de receta",
//                                    seccionDTO.getOrden())
//                    );
//                });
//    }
}
