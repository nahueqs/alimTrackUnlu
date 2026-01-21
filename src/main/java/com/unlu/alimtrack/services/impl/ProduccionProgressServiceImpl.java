package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.response.Produccion.publico.ProgresoProduccionResponseDTO;
import com.unlu.alimtrack.enums.TipoDatoCampo;
import com.unlu.alimtrack.models.RespuestaCampoModel;
import com.unlu.alimtrack.models.RespuestaTablaModel;
import com.unlu.alimtrack.services.ProduccionProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProduccionProgressServiceImpl implements ProduccionProgressService {

    private record CeldaKey(Long idTabla, Long idFila, Long idColumna) {
        static CeldaKey fromRespuestaTabla(RespuestaTablaModel respuesta) {
            if (respuesta == null) return null;

            Long idTabla = respuesta.getId() != null ? respuesta.getId() : null;
            Long idFila = respuesta.getFila() != null ? respuesta.getFila().getId() : null;
            Long idColumna = respuesta.getColumna() != null ? respuesta.getColumna().getId() : null;

            return (idTabla != null && idFila != null && idColumna != null)
                    ? new CeldaKey(idTabla, idFila, idColumna)
                    : null;
        }
    }

    @Override
    public ProgresoProduccionResponseDTO calcularProgreso(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas) {

        validarParametros(totalCampos, totalCeldas);

        long camposRespondidos = calcularCamposRespondidos(respuestasCampos);
        long celdasRespondidas = calcularCeldasRespondidas(respuestasTablas);

        int totalGlobal = totalCampos + totalCeldas;
        long respondidoGlobal = camposRespondidos + celdasRespondidas;
        double porcentaje = calcularPorcentaje(totalGlobal, respondidoGlobal);

        log.debug("Progreso calculado: Campos {}/{}, Celdas {}/{}, Global {}/{} ({}%)",
                camposRespondidos, totalCampos,
                celdasRespondidas, totalCeldas,
                respondidoGlobal, totalGlobal,
                porcentaje);

        return new ProgresoProduccionResponseDTO(
                totalCampos, (int) camposRespondidos,
                totalCeldas, (int) celdasRespondidas,
                totalGlobal, (int) respondidoGlobal,
                porcentaje
        );
    }

    @Override
    public long calcularCamposRespondidos(List<RespuestaCampoModel> respuestasCampos) {
        if (respuestasCampos == null || respuestasCampos.isEmpty()) {
            return 0;
        }

        // Usar Set para evitar duplicados
        Set<Long> camposIdsUnicos = respuestasCampos.stream()
                .filter(Objects::nonNull)
                .filter(this::esRespuestaCampoValida)
                .map(respuesta -> respuesta.getIdCampo() != null ? respuesta.getIdCampo().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return camposIdsUnicos.size();
    }

    @Override
    public long calcularCeldasRespondidas(List<RespuestaTablaModel> respuestasTablas) {
        if (respuestasTablas == null || respuestasTablas.isEmpty()) {
            return 0;
        }

        // Usar Set para celdas únicas
        Set<CeldaKey> celdasUnicas = respuestasTablas.stream()
                .filter(Objects::nonNull)
                .filter(this::esRespuestaTablaValida)
                .map(CeldaKey::fromRespuestaTabla)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return celdasUnicas.size();
    }

    private void validarParametros(Integer totalCampos, Integer totalCeldas) {
        if (totalCampos == null || totalCampos < 0) {
            throw new IllegalArgumentException("totalCampos no puede ser nulo o negativo");
        }

        if (totalCeldas == null || totalCeldas < 0) {
            throw new IllegalArgumentException("totalCeldas no puede ser nulo o negativo");
        }

        // Log de advertencia si hay valores cero
        if (totalCampos == 0) {
            log.warn("totalCampos es 0 - verificar configuración de campos");
        }
        if (totalCeldas == 0) {
            log.warn("totalCeldas es 0 - verificar configuración de tablas");
        }
    }

    private boolean esRespuestaCampoValida(RespuestaCampoModel respuesta) {
        if (respuesta == null || respuesta.getIdCampo() == null) {
            return false;
        }

        // Usar método del modelo si existe
        if (respuesta.esRespuestaVacia()) {
            return false;
        }

        // Validar que el valor corresponda al tipo esperado
        boolean valorCoherente = validarValorSegunTipoCampo(respuesta);
        if (!valorCoherente) {
            log.debug("Respuesta de campo {} tiene valor incoherente con tipo {}",
                    respuesta.getIdCampo().getId(),
                    respuesta.getIdCampo().getTipoDato());
            return false;
        }

        return true;
    }

    private boolean esRespuestaTablaValida(RespuestaTablaModel respuesta) {
        if (respuesta == null) {
            return false;
        }

        // Usar método del modelo (lo moviste a RespuestaTablaModel)
        return respuesta.esRespuestaValida();

        // O si necesitas validación más específica:
        // return respuesta.esRespuestaValida() &&
        //        respuesta.getColumna() != null &&
        //        respuesta.getFila() != null &&
        //        respuesta.getIdTabla() != null;
    }

    private boolean validarValorSegunTipoCampo(RespuestaCampoModel respuesta) {
        if (respuesta.getIdCampo() == null || respuesta.getIdCampo().getTipoDato() == null) {
            return false;
        }

        TipoDatoCampo tipo = respuesta.getIdCampo().getTipoDato();

        switch (tipo) {
            case TEXTO:
                return respuesta.getValorTexto() != null && !respuesta.getValorTexto().trim().isEmpty();
            case DECIMAL:
            case ENTERO:
                return respuesta.getValorNumerico() != null;
            case FECHA:
            case HORA:
                return respuesta.getValorFecha() != null;
            case BOOLEANO:
                // Para booleanos, aceptar cualquier valor (true/false representado como BigDecimal)
                return respuesta.getValorNumerico() != null;
            default:
                log.warn("Tipo de campo no reconocido: {}", tipo);
                return false;
        }
    }

    private double calcularPorcentaje(int total, long respondido) {
        if (total <= 0) {
            return 0.0;
        }

        // Evitar división por cero
        if (total == 0) {
            return 0.0;
        }

        // Calcular con precisión
        double porcentaje = (respondido * 100.0) / total;

        // Redondear a 2 decimales
        return Math.round(porcentaje * 100.0) / 100.0;
    }

    // ========== MÉTODOS ADICIONALES UTILES ==========

    /**
     * Calcula el progreso por sección o grupo
     */
    public ProgresoProduccionResponseDTO calcularProgresoPorSeccion(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas,
            Long idSeccion) {

        // Filtrar respuestas por sección
        List<RespuestaCampoModel> camposFiltrados = respuestasCampos.stream()
                .filter(respuesta -> respuesta.getIdCampo() != null &&
                        respuesta.getIdCampo().getSeccion() != null &&
                        Objects.equals(respuesta.getIdCampo().getSeccion().getId(), idSeccion))
                .toList();

        List<RespuestaTablaModel> tablasFiltradas = respuestasTablas.stream()
                .filter(respuesta -> {
                    // Filtrar por sección de la tabla (ajustar según tu modelo)
                    return true; // Implementar lógica específica
                })
                .toList();

        return calcularProgreso(totalCampos, totalCeldas, camposFiltrados, tablasFiltradas);
    }

    /**
     * Verifica si una producción está completa (100%)
     */
    public boolean estaProduccionCompleta(
            Integer totalCampos, Integer totalCeldas,
            List<RespuestaCampoModel> respuestasCampos,
            List<RespuestaTablaModel> respuestasTablas) {

        ProgresoProduccionResponseDTO progreso = calcularProgreso(
                totalCampos, totalCeldas, respuestasCampos, respuestasTablas);

        return Math.abs(progreso.porcentajeCompletado() - 100.0) < 0.01;
    }

    /**
     * Obtiene lista de campos no respondidos
     */
    public List<Long> obtenerCamposNoRespondidos(
            List<Long> todosLosCamposIds,
            List<RespuestaCampoModel> respuestasCampos) {

        Set<Long> camposRespondidosIds = respuestasCampos.stream()
                .filter(this::esRespuestaCampoValida)
                .map(respuesta -> respuesta.getIdCampo() != null ? respuesta.getIdCampo().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return todosLosCamposIds.stream()
                .filter(id -> !camposRespondidosIds.contains(id))
                .toList();
    }

    /**
     * Calcula tendencia de progreso (comparando con progreso anterior)
     */
    public double calcularTendenciaProgreso(
            ProgresoProduccionResponseDTO progresoActual,
            ProgresoProduccionResponseDTO progresoAnterior) {

        if (progresoAnterior == null) {
            return 0.0;
        }

        return progresoActual.porcentajeCompletado() -
                progresoAnterior.porcentajeCompletado();
    }
}