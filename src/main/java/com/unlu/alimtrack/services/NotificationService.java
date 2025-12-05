package com.unlu.alimtrack.services;

import com.unlu.alimtrack.DTOS.websocket.ProductionUpdateMessage;

public interface NotificationService {
    // Single method for all granular production updates for a specific production
    void notifyProductionUpdate(ProductionUpdateMessage message);

    // Method for notifying about newly created productions (for list updates)
    void notifyProductionCreated(ProductionUpdateMessage message);

    // New method for notifying about production state changes globally (for list updates)
    void notifyProductionStateChangedGlobal(ProductionUpdateMessage message);
}
