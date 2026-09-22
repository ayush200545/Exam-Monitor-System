package com.distributedexam.server.failure;

import com.distributedexam.common.ConnectionState;
import com.distributedexam.common.RoomConnectionInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RecoveryManager — server-side component.
 * Handles re-accepting a room that reconnects after being marked OFFLINE.
 * When a room re-registers via HeartbeatService.registerRoom(), this manager
 * updates its state back to ONLINE and logs the recovery event.
 *
 * Called internally by HeartbeatService on re-registration.
 */
public class RecoveryManager {

    private final ConcurrentHashMap<String, RoomConnectionInfo> roomRegistry;

    public RecoveryManager(ConcurrentHashMap<String, RoomConnectionInfo> roomRegistry) {
        this.roomRegistry = roomRegistry;
    }

    /**
     * Called when a room that was previously OFFLINE re-registers.
     * Resets state to ONLINE and logs the recovery.
     */
    public void handleRecovery(String roomId) {
        RoomConnectionInfo info = roomRegistry.get(roomId);
        if (info != null && info.getConnectionState() == ConnectionState.OFFLINE) {
            info.setConnectionState(ConnectionState.ONLINE);
            System.out.println("\n[RECOVERY] Room " + roomId + " has reconnected.");
            System.out.println("[RECOVERY] " + roomId + " -> ONLINE\n");
        }
    }

    /**
     * Returns true if the given room was previously OFFLINE (recovering).
     */
    public boolean isRecovering(String roomId) {
        RoomConnectionInfo info = roomRegistry.get(roomId);
        return info != null && info.getConnectionState() == ConnectionState.OFFLINE;
    }
}
