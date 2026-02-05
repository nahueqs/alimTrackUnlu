package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.websocket.ProductionUpdateMessage;
import com.unlu.alimtrack.services.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void notifyProductionUpdate_ShouldSendToSpecificTopic() {
        // Arrange
        String codigoProduccion = "PROD-1";
        // Corregido: ProductionUpdateMessage tiene 4 argumentos: type, codigoProduccion, timestamp, payload
        ProductionUpdateMessage message = new ProductionUpdateMessage(
                "FIELD_UPDATED", codigoProduccion, LocalDateTime.now(), null
        );
        String expectedDestination = "/topic/produccion/" + codigoProduccion;

        // Act
        notificationService.notifyProductionUpdate(message);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(message));
    }

    @Test
    void notifyProductionCreated_ShouldSendToCreatedTopic() {
        // Arrange
        ProductionUpdateMessage message = new ProductionUpdateMessage(
                "PRODUCTION_METADATA_CREATED", "PROD-NEW", LocalDateTime.now(), null
        );
        String expectedDestination = "/topic/produccion/created";

        // Act
        notificationService.notifyProductionCreated(message);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(message));
    }

    @Test
    void notifyProductionStateChangedGlobal_ShouldSendToStateChangedTopic() {
        // Arrange
        ProductionUpdateMessage message = new ProductionUpdateMessage(
                "STATE_CHANGED", "PROD-1", LocalDateTime.now(), null
        );
        String expectedDestination = "/topic/producciones/state-changed";

        // Act
        notificationService.notifyProductionStateChangedGlobal(message);

        // Assert
        verify(messagingTemplate).convertAndSend(eq(expectedDestination), eq(message));
    }
}
