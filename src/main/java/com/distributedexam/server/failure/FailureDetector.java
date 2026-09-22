package com.distributedexam.server.failure;

import com.distributedexam.common.ConnectionState;
import com.distributedexam.common.RoomConnectionInfo;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * FailureDetector — runs on the server JVM.
 * Periodically checks every registered room's lastHeartbeat.
 * If no heartbeat has been received within FAILURE_TIMEOUT_MS, marks the room OFFLINE.
 */
public class FailureDetector {

    private static final long HEARTBEAT_INTERVAL_MS  = Long.parseLong(System.getProperty("HEARTBEAT_INTERVAL", "5000"));
    private static final long FAILURE_TIMEOUT_MS      = Long.parseLong(System.getProperty("FAILURE_TIMEOUT",   "15000"));

    private final ConcurrentHashMap<String, RoomConnectionInfo> roomRegistry;
    private final ScheduledExecutorService scheduler;

    public FailureDetector(ConcurrentHashMap<String, RoomConnectionInfo> roomRegistry) {
        this.roomRegistry = roomRegistry;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "FailureDetector");
            t.setDaemon(true);
            return t;
        });
    }

    /** Start the periodic failure-detection check. */
    public void start() {
        scheduler.scheduleAtFixedRate(this::checkRooms,
                HEARTBEAT_INTERVAL_MS,
                HEARTBEAT_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
        System.out.println("[SERVER] FailureDetector started (timeout=" + FAILURE_TIMEOUT_MS + "ms)");
    }

    private void checkRooms() {
        Instant now = Instant.now();
        for (RoomConnectionInfo info : roomRegistry.values()) {
            if (info.getConnectionState() == ConnectionState.OFFLINE) {
                continue; // already marked offline
            }
            long elapsed = now.toEpochMilli() - info.getLastHeartbeat().toEpochMilli();
            if (elapsed > FAILURE_TIMEOUT_MS) {
                info.setConnectionState(ConnectionState.OFFLINE);
                System.out.println("\n[FAILURE DETECTOR] Room " + info.getRoomId() + " heartbeat timeout.");
                System.out.println("[FAILURE DETECTOR] " + info.getRoomId() + " -> OFFLINE\n");
            }
        }
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
