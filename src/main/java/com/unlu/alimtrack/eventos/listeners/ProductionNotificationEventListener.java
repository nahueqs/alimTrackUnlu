// ProductionNotificationEventListener.java
package com.unlu.alimtrack.eventos.listeners;

import com.unlu.alimtrack.DTOS.websocket.*;
import com.unlu.alimtrack.eventos.*;
import com.unlu.alimtrack.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductionNotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRespuestaCampoGuardada(RespuestaCampoGuardadaEvent event) {
        log.info("Procesando notificación WebSocket para CAMPO guardado. Producción: {}, Campo: {}", 
                event.getCodigoProduccion(), event.getIdCampo());

        try {
            String valorString = convertirValorAString(event.getValor());

            ProductionUpdateMessage message = ProductionUpdateMessage.fieldUpdated(
                    event.getCodigoProduccion(),
                    new FieldUpdatePayload(
                            event.getIdCampo(),
                            valorString
                    )
            );

            notificationService.notifyProductionUpdate(message);
            log.debug("Notificación enviada: {}", message);

        } catch (Exception e) {
            log.error("Error al procesar notificación de respuesta campo", e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRespuestaTablaGuardada(RespuestaTablaGuardadaEvent event) {
        log.info("Procesando notificación WebSocket para TABLA guardada. Producción: {}, Celda: {}-{}-{}", 
                event.getCodigoProduccion(), event.getIdTabla(), event.getIdFila(), event.getIdColumna());

        try {
            // Para tablas, el valor ya viene como String en el evento según ProduccionEventPublisher
            String valorString = event.getValor();

            ProductionUpdateMessage message = ProductionUpdateMessage.tableCellUpdated(
                    event.getCodigoProduccion(),
                    new TableCellUpdatePayload(
                            event.getIdTabla(),
                            event.getIdFila(),
                            event.getIdColumna(),
                            valorString
                    )
            );

            notificationService.notifyProductionUpdate(message);
            log.debug("Notificación enviada: {}", message);

        } catch (Exception e) {
            log.error("Error al procesar notificación de respuesta tabla", e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEstadoCambiado(EstadoProduccionCambiadoEvent event) {
        log.info("Procesando notificación WebSocket para ESTADO cambiado. Producción: {}", event.getCodigoProduccion());

        try {
            ProductionUpdateMessage message = ProductionUpdateMessage.stateChanged(
                    event.getCodigoProduccion(),
                    new ProductionStateUpdatePayload(
                            event.getEstado(),
                            event.getFechaFin()
                    )
            );

            notificationService.notifyProductionUpdate(message);
            notificationService.notifyProductionStateChangedGlobal(message);

        } catch (Exception e) {
            log.error("Error al procesar notificación de estado cambiado", e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMetadataActualizada(MetadataProduccionActualizadaEvent event) {
        log.info("Procesando notificación WebSocket para METADATA actualizada. Producción: {}", event.getCodigoProduccion());

        try {
            ProductionUpdateMessage message = ProductionUpdateMessage.metadataUpdated(
                    event.getCodigoProduccion(),
                    new ProductionMetadataUpdatePayload(
                            event.getCodigoProduccion(),
                            event.getLote(),
                            event.getEncargado(),
                            event.getObservaciones()
                    )
            );

            notificationService.notifyProductionUpdate(message);

        } catch (Exception e) {
            log.error("Error al procesar notificación de metadata", e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProduccionCreada(ProduccionCreadaEvent event) {
        log.info("Procesando notificación WebSocket para PRODUCCIÓN CREADA. Código: {}", event.getCodigoProduccion());

        try {
            ProductionUpdateMessage metadataMessage = ProductionUpdateMessage.metadataCreated(
                    event.getCodigoProduccion(),
                    new ProductionCreationPayload(
                            event.getCodigoVersionReceta(),
                            event.getLote(),
                            event.getFechaInicio(),
                            event.getFechaFin()
                    )
            );

            notificationService.notifyProductionUpdate(metadataMessage);

            ProductionUpdateMessage stateMessage = ProductionUpdateMessage.stateChanged(
                    event.getCodigoProduccion(),
                    new ProductionStateUpdatePayload(
                            com.unlu.alimtrack.enums.TipoEstadoProduccion.EN_PROCESO,
                            event.getFechaFin()
                    )
            );

            notificationService.notifyProductionUpdate(stateMessage);
            notificationService.notifyProductionCreated(metadataMessage);

        } catch (Exception e) {
            log.error("Error al procesar notificación de producción creada", e);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProduccionEliminada(ProduccionEliminadaEvent event) {
        log.info("Procesando notificación WebSocket para PRODUCCIÓN ELIMINADA. Código: {}", event.getCodigoProduccion());

        try {
            ProductionUpdateMessage message = ProductionUpdateMessage.productionDeleted(event.getCodigoProduccion());
            notificationService.notifyProductionDeleted(message);
            log.debug("Notificación de eliminación enviada: {}", message);

        } catch (Exception e) {
            log.error("Error al procesar notificación de producción eliminada", e);
        }
    }

    // Método auxiliar para convertir valores de forma segura y consistente
    private String convertirValorAString(Object valor) {
        if (valor == null) {
            return ""; // Enviar cadena vacía si es null para limpiar el campo en el front
        }
        if (valor instanceof LocalDateTime) {
            return ((LocalDateTime) valor).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (valor instanceof BigDecimal) {
            return ((BigDecimal) valor).toPlainString(); // Evitar notación científica
        }
        return valor.toString();
    }
}
