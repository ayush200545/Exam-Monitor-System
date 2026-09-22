package com.exam.server.service;

import com.exam.common.model.EventType;
import com.exam.common.model.RoomStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background failure detector that detects room communication timeouts.
 * If a room does not heartbeat within timeoutMs, it is marked OFFLINE
 * and a single ROOM_OFFLINE event is published.
 */
public class FailureDetector {
    private final HeartbeatManager heartbeatManager;
    private final EventManager eventManager;
    private final long timeoutMs;
    private final long checkIntervalMs;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public FailureDetector(HeartbeatManager heartbeatManager, EventManager eventManager,
                           long timeoutMs, long checkIntervalMs) {
        this.heartbeatManager = heartbeatManager;
        this.eventManager = eventManager;
        this.timeoutMs = timeoutMs;
        this.checkIntervalMs = checkIntervalMs;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "FailureDetector-Thread");
            t.setDaemon(true);
            return t;
        });
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        scheduler.scheduleWithFixedDelay(this::checkHeartbeats, checkIntervalMs, checkIntervalMs, TimeUnit.MILLISECONDS);
        System.out.printf("[FailureDetector] Started (timeout=%d ms, interval=%d ms)%n", timeoutMs, checkIntervalMs);
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        scheduler.shutdownNow();
        System.out.println("[FailureDetector] Stopped.");
    }

    public void checkHeartbeats() {
        long now = System.currentTimeMillis();
        for (String roomId : heartbeatManager.getAllRooms()) {
            RoomStatus currentStatus = heartbeatManager.getRoomStatus(roomId);

            // Only transition from ONLINE to OFFLINE to prevent duplicate events
            if (currentStatus == RoomStatus.ONLINE) {
                Long lastPulse = heartbeatManager.getLastHeartbeat(roomId);
                if (lastPulse != null && (now - lastPulse) > timeoutMs) {
                    heartbeatManager.setRoomStatus(roomId, RoomStatus.OFFLINE);
                    System.err.printf("[FailureDetector] Room %s missed heartbeats for %d ms -> MARKED OFFLINE%n",
                            roomId, (now - lastPulse));
                    if (eventManager != null) {
                        eventManager.logEvent(roomId, EventType.ROOM_OFFLINE,
                                "Room missed heartbeats for " + (now - lastPulse) + "ms. Detected as OFFLINE.");
                    }
                }
            }
        }
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }
}
