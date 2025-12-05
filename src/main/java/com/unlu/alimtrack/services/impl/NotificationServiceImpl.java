package com.unlu.alimtrack.services.impl;

import com.unlu.alimtrack.DTOS.websocket.ProductionUpdateMessage;
import com.unlu.alimtrack.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyProductionUpdate(ProductionUpdateMessage message) {
        String destination = "/topic/produccion/" + message.codigoProduccion();
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void notifyProductionCreated(ProductionUpdateMessage message) {
        String destination = "/topic/produccion/created"; // Specific topic for new productions
        messagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void notifyProductionStateChangedGlobal(ProductionUpdateMessage message) {
        String destination = "/topic/producciones/state-changed"; // General topic for state changes
        messagingTemplate.convertAndSend(destination, message);
    }
}
