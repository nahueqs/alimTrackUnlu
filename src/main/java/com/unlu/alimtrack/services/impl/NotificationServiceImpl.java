package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.websocket.ProductionUpdateMessage;
import com.unlu.alimtrack.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de notificaciones vía WebSocket.
 * Se encarga de enviar mensajes a los clientes suscritos a diferentes tópicos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notifica una actualización en una producción específica.
     * Envía el mensaje al tópico /topic/produccion/{codigoProduccion}.
     *
     * @param message El mensaje con los detalles de la actualización.
     */
    @Override
    public void notifyProductionUpdate(ProductionUpdateMessage message) {
        String destination = "/topic/produccion/" + message.codigoProduccion();
        log.debug("Enviando notificación de actualización de producción al tópico: {}", destination);
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.trace("Mensaje enviado a {}: {}", destination, message);
        } catch (Exception e) {
            log.error("Error al enviar notificación de actualización de producción a {}: {}", destination, e.getMessage());
        }
    }

    /**
     * Notifica la creación de una nueva producción.
     * Envía el mensaje al tópico global /topic/produccion/created.
     *
     * @param message El mensaje con los detalles de la nueva producción.
     */
    @Override
    public void notifyProductionCreated(ProductionUpdateMessage message) {
        String destination = "/topic/produccion/created";
        log.debug("Enviando notificación de creación de producción al tópico: {}", destination);
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.trace("Mensaje enviado a {}: {}", destination, message);
        } catch (Exception e) {
            log.error("Error al enviar notificación de creación de producción a {}: {}", destination, e.getMessage());
        }
    }

    /**
     * Notifica un cambio de estado global en una producción.
     * Envía el mensaje al tópico /topic/producciones/state-changed.
     *
     * @param message El mensaje con los detalles del cambio de estado.
     */
    @Override
    public void notifyProductionStateChangedGlobal(ProductionUpdateMessage message) {
        String destination = "/topic/producciones/state-changed";
        log.debug("Enviando notificación de cambio de estado global al tópico: {}", destination);
        try {
            messagingTemplate.convertAndSend(destination, message);
            log.trace("Mensaje enviado a {}: {}", destination, message);
        } catch (Exception e) {
            log.error("Error al enviar notificación de cambio de estado global a {}: {}", destination, e.getMessage());
        }
    }
}
