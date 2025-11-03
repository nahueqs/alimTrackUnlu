package com.unlu.alimtrack.services.queries;

import com.unlu.alimtrack.repositories.SeccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeccionQueryServiceImpl implements SeccionQueryService {

    private SeccionRepository seccionRepository;

    /**
     * Verifica si existe una sección con un orden específico en una versión de receta
     *
     * @param codigoVersion Versión de receta
     * @param orden         Orden a verificar
     * @return true si ya existe una sección con ese orden, false en caso contrario
     */
    @Override
    public boolean existsByVersionRecetaPadreAndOrden(String codigoVersion, Integer orden) {
        return seccionRepository.existsByCodigoVersionRecetaPadreAndOrden(codigoVersion, orden);
    }

    /**
     * Verifica si existe una sección con un título específico en una versión de receta
     *
     * @param codigoVersion Versión de receta
     * @param titulo        Título a verificar
     * @return true si ya existe una sección con ese título, false en caso contrario
     */
    @Override
    public boolean existsByVersionRecetaPadreAndTitulo(String codigoVersion, String titulo) {
        return seccionRepository.existsByCodigoVersionRecetaPadreAndTitulo(codigoVersion, titulo);
    }


}

