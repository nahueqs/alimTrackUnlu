// ProductionEventPublisher.java
package com.unlu.alimtrack.eventos;

import com.unlu.alimtrack.enums.TipoEstadoProduccion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProduccionEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publicarRespuestaCampoGuardada(Object source, String codigoProduccion, Long idCampo, Object valor) {
        log.debug("Publicando evento: Respuesta campo guardada. Producción: {}, Campo: {}", codigoProduccion, idCampo);
        eventPublisher.publishEvent(new RespuestaCampoGuardadaEvent(
                source, codigoProduccion, idCampo, valor));
    }

    public void publicarRespuestaTablaGuardada(Object source, String codigoProduccion, Long idTabla, Long idFila, Long idColumna, String valor) {
        log.debug("Publicando evento: Respuesta tabla guardada. Producción: {}, Tabla: {}, Fila: {}, Columna: {}",
                codigoProduccion, idTabla, idFila, idColumna);
        eventPublisher.publishEvent(new RespuestaTablaGuardadaEvent(
                source, codigoProduccion, idTabla, idFila, idColumna, valor));
    }

    public void publicarEstadoCambiado(Object source, String codigoProduccion, TipoEstadoProduccion estado, LocalDateTime fechaFin) {
        log.debug("Publicando evento: Estado cambiado. Producción: {}, Estado: {}", codigoProduccion, estado);
        eventPublisher.publishEvent(new EstadoProduccionCambiadoEvent(
                source, codigoProduccion, estado, fechaFin));
    }

    public void publicarMetadataActualizada(Object source, String codigoProduccion, String lote, String encargado, String observaciones) {
        log.debug("Publicando evento: Metadata actualizada. Producción: {}", codigoProduccion);
        eventPublisher.publishEvent(new MetadataProduccionActualizadaEvent(
                source, codigoProduccion, lote, encargado, observaciones));
    }

    public void publicarProduccionCreada(Object source, String codigoProduccion, String codigoVersionReceta, String lote, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Publicando evento: Producción creada. Código: {}", codigoProduccion);
        eventPublisher.publishEvent(new ProduccionCreadaEvent(
                source, codigoProduccion, codigoVersionReceta, lote, fechaInicio, fechaFin));
    }

    public void publicarProduccionEliminada(Object source, String codigoProduccion) {
        log.debug("Publicando evento: Producción eliminada. Código: {}", codigoProduccion);
        eventPublisher.publishEvent(new ProduccionEliminadaEvent(source, codigoProduccion));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(RespuestaCampoGuardadaEvent event) {
        log.debug("Evento procesado después de commit: {}", event.getTipoEvento());
    }
}