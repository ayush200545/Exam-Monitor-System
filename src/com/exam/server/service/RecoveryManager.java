package com.exam.server.service;

import com.exam.common.model.EventType;
import com.exam.common.model.RoomStatus;

/**
 * Handles room recovery and re-synchronization when a disconnected room reconnects.
 */
public class RecoveryManager {
    private final HeartbeatManager heartbeatManager;
    private final EventManager eventManager;

    public RecoveryManager(HeartbeatManager heartbeatManager, EventManager eventManager) {
        this.heartbeatManager = heartbeatManager;
        this.eventManager = eventManager;
    }

    public synchronized boolean handleRecovery(String roomId) {
        RoomStatus previousStatus = heartbeatManager.getRoomStatus(roomId);
        heartbeatManager.recordHeartbeat(roomId);
        heartbeatManager.setRoomStatus(roomId, RoomStatus.ONLINE);

        System.out.printf("[RecoveryManager] Room %s reconnected (previous state: %s) -> NOW ONLINE%n",
                roomId, previousStatus);

        if (eventManager != null) {
            eventManager.logEvent(roomId, EventType.ROOM_RECONNECTED,
                    "Room successfully reconnected and recovered to ONLINE state.");
        }
        return true;
    }
}
