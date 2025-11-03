package com.unlu.alimtrack.services.queries;

public interface SeccionQueryService {

    boolean existsByVersionRecetaPadreAndOrden(String codigoVersion, Integer orden);

    boolean existsByVersionRecetaPadreAndTitulo(String codigoVersion, String titulo);

}
