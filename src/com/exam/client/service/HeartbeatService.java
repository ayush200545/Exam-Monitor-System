package com.exam.client.service;

import com.exam.common.rmi.RemoteMonitoringService;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Client-side background service that periodically sends heartbeat signals
 * to the Central Server over RMI.
 */
public class HeartbeatService {
    private final String roomId;
    private final RemoteMonitoringService monitoringService;
    private final long intervalSeconds;
    private final ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public HeartbeatService(String roomId, RemoteMonitoringService monitoringService, long intervalSeconds) {
        this.roomId = roomId;
        this.monitoringService = monitoringService;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HeartbeatClient-" + roomId);
            t.setDaemon(true);
            return t;
        });
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        scheduler.scheduleAtFixedRate(this::sendPulse, 0, intervalSeconds, TimeUnit.SECONDS);
        System.out.printf("[%s Heartbeat] Started pulsing every %d seconds.%n", roomId, intervalSeconds);
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        scheduler.shutdownNow();
        System.out.printf("[%s Heartbeat] Stopped pulsing.%n", roomId);
    }

    private void sendPulse() {
        try {
            monitoringService.sendHeartbeat(roomId);
            System.out.printf("[%s Heartbeat] Pulse sent successfully at %s.%n",
                    roomId, java.time.LocalTime.now().toString());
        } catch (RemoteException e) {
            System.err.printf("[%s Heartbeat Warning] Failed to reach server: %s%n", roomId, e.getMessage());
        }
    }

    public boolean isRunning() {
        return running;
    }
}
