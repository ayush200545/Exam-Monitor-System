package com.exam.server.service;

import com.exam.common.model.EventType;
import com.exam.common.model.RoomStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks periodic heartbeats and room connection statuses on the Central Server.
 */
public class HeartbeatManager {
    private final Map<String, Long> lastHeartbeatMap = new ConcurrentHashMap<>();
    private final Map<String, RoomStatus> roomStatusMap = new ConcurrentHashMap<>();
    private final EventManager eventManager;

    public HeartbeatManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public synchronized boolean registerRoom(String roomId) {
        long now = System.currentTimeMillis();
        boolean isNew = !roomStatusMap.containsKey(roomId);
        lastHeartbeatMap.put(roomId, now);
        roomStatusMap.put(roomId, RoomStatus.ONLINE);

        if (isNew && eventManager != null) {
            eventManager.logEvent(roomId, EventType.ROOM_REGISTERED, "Room registered with Central Server");
        }
        return true;
    }

    public void recordHeartbeat(String roomId) {
        long now = System.currentTimeMillis();
        lastHeartbeatMap.put(roomId, now);

        // If it was OFFLINE, recordHeartbeat keeps it alive, but recovery is handled via RecoveryManager
        roomStatusMap.putIfAbsent(roomId, RoomStatus.ONLINE);
    }

    public Long getLastHeartbeat(String roomId) {
        return lastHeartbeatMap.get(roomId);
    }

    public RoomStatus getRoomStatus(String roomId) {
        return roomStatusMap.getOrDefault(roomId, RoomStatus.OFFLINE);
    }

    public void setRoomStatus(String roomId, RoomStatus status) {
        roomStatusMap.put(roomId, status);
    }

    public Set<String> getAllRooms() {
        return Collections.unmodifiableSet(roomStatusMap.keySet());
    }

    public Map<String, RoomStatus> getAllRoomStatuses() {
        return Collections.unmodifiableMap(roomStatusMap);
    }
}
