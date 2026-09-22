package com.distributedexam.client;

import com.distributedexam.common.ConnectionState;
import com.distributedexam.common.RoomConnectionInfo;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ReconnectionManager — runs inside each Room Client JVM.
 * When triggered, it retries connecting to the RMI registry using a ScheduledExecutorService.
 * On success it re-registers the room and restarts the heartbeat.
 */
public class ReconnectionManager {

    private static final long RECONNECT_INTERVAL_MS = Long.parseLong(System.getProperty("RECONNECT_INTERVAL", "5000"));

    private final String roomId;
    private final String rmiHost;
    private final int rmiPort;

    private RMIClientConnector connector;
    private HeartbeatManager heartbeatManager; // set after construction to avoid circular dep
    private volatile ConnectionState state = ConnectionState.DISCONNECTED;

    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> reconnectTask;
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    public ReconnectionManager(String roomId, String rmiHost, int rmiPort, RMIClientConnector connector) {
        this.roomId = roomId;
        this.rmiHost = rmiHost;
        this.rmiPort = rmiPort;
        this.connector = connector;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Reconnector-" + roomId);
            t.setDaemon(true);
            return t;
        });
    }

    public void setHeartbeatManager(HeartbeatManager hm) {
        this.heartbeatManager = hm;
    }

    public ConnectionState getState() {
        return state;
    }

    /** Called by HeartbeatManager when a RemoteException is detected. */
    public void triggerReconnect() {
        if (reconnecting.compareAndSet(false, true)) {
            state = ConnectionState.RECONNECTING;
            System.out.println("[" + roomId + "] -> RECONNECTING");
            reconnectTask = scheduler.scheduleAtFixedRate(this::attemptReconnect,
                    0,
                    RECONNECT_INTERVAL_MS,
                    TimeUnit.MILLISECONDS);
        }
    }

    private void attemptReconnect() {
        System.out.println("[" + roomId + "] Attempting reconnection to " + rmiHost + ":" + rmiPort + "...");
        try {
            connector = new RMIClientConnector(rmiHost, rmiPort);
            connector.connect();

            // Re-register with server
            RoomConnectionInfo info = new RoomConnectionInfo(roomId, rmiHost);
            connector.getHeartbeatService().registerRoom(info);

            // Update heartbeat manager with fresh stub
            heartbeatManager.updateHeartbeatService(connector.getHeartbeatService());
            heartbeatManager.restart();

            state = ConnectionState.ONLINE;
            reconnecting.set(false);
            reconnectTask.cancel(false);

            System.out.println("[" + roomId + "] Reconnected to RMI server.");
            System.out.println("[" + roomId + "] " + roomId + " registered again.");
            System.out.println("[" + roomId + "] -> ONLINE");

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            System.err.println("[" + roomId + "] Reconnection attempt failed: " + e.getMessage());
            System.out.println("[" + roomId + "] Retrying in " + RECONNECT_INTERVAL_MS + "ms...");
        }
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
