package com.distributedexam.client;

import com.distributedexam.remote.HeartbeatRemote;

import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * HeartbeatManager — runs inside each Room Client JVM.
 * Sends a heartbeat to the server every HEARTBEAT_INTERVAL_MS using a ScheduledExecutorService.
 * Notifies the ReconnectionManager on RemoteException (connection lost).
 */
public class HeartbeatManager {

    private static final long HEARTBEAT_INTERVAL_MS = Long.parseLong(System.getProperty("HEARTBEAT_INTERVAL", "5000"));

    private final String roomId;
    private HeartbeatRemote heartbeatService;
    private final ReconnectionManager reconnectionManager;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartbeatTask;

    public HeartbeatManager(String roomId, HeartbeatRemote heartbeatService, ReconnectionManager reconnectionManager) {
        this.roomId = roomId;
        this.heartbeatService = heartbeatService;
        this.reconnectionManager = reconnectionManager;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "HeartbeatSender-" + roomId);
            t.setDaemon(true);
            return t;
        });
    }

    /** Start sending heartbeats. */
    public void start() {
        heartbeatTask = scheduler.scheduleAtFixedRate(this::sendHeartbeat,
                HEARTBEAT_INTERVAL_MS,
                HEARTBEAT_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
        System.out.println("[" + roomId + "] HeartbeatManager started (interval=" + HEARTBEAT_INTERVAL_MS + "ms)");
    }

    private void sendHeartbeat() {
        try {
            heartbeatService.heartbeat(roomId);
        } catch (RemoteException e) {
            System.err.println("[" + roomId + "] Heartbeat failed: " + e.getMessage());
            System.out.println("[" + roomId + "] Communication lost -> RECONNECTING");
            stopTask();
            reconnectionManager.triggerReconnect();
        }
    }

    /** Update the remote stub after a successful reconnection. */
    public void updateHeartbeatService(HeartbeatRemote heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    /** Cancel the scheduled task (called before reconnecting). */
    public void stopTask() {
        if (heartbeatTask != null && !heartbeatTask.isCancelled()) {
            heartbeatTask.cancel(false);
        }
    }

    /** Restart sending heartbeats after reconnection. */
    public void restart() {
        heartbeatTask = scheduler.scheduleAtFixedRate(this::sendHeartbeat,
                HEARTBEAT_INTERVAL_MS,
                HEARTBEAT_INTERVAL_MS,
                TimeUnit.MILLISECONDS);
        System.out.println("[" + roomId + "] HeartbeatManager restarted.");
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
