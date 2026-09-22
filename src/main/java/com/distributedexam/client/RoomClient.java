package com.distributedexam.client;

import com.distributedexam.common.ConnectionState;
import com.distributedexam.common.RoomConnectionInfo;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * RoomClient — fully independent JVM process.
 *
 * Launch:
 *   java -cp "bin;lib/*" com.distributedexam.client.RoomClient ROOM-A
 *
 * Optional system properties:
 *   -DRMI_HOST=192.168.1.10  (default: localhost)
 *   -DRMI_PORT=1099           (default: 1099)
 *   -DHEARTBEAT_INTERVAL=5000 (default: 5000ms)
 *   -DRECONNECT_INTERVAL=5000 (default: 5000ms)
 */
public class RoomClient {

    private static final String RMI_HOST = System.getProperty("RMI_HOST", "localhost");
    private static final int    RMI_PORT = Integer.parseInt(System.getProperty("RMI_PORT", "1099"));

    private final String roomId;
    private RMIClientConnector   connector;
    private HeartbeatManager     heartbeatManager;
    private ReconnectionManager  reconnectionManager;
    private volatile ConnectionState state = ConnectionState.DISCONNECTED;

    public RoomClient(String roomId) {
        this.roomId = roomId;
    }

    public void start() {
        printBanner();
        state = ConnectionState.CONNECTING;

        // Attempt initial connection (may retry if server not yet up)
        connector = new RMIClientConnector(RMI_HOST, RMI_PORT);
        boolean connected = false;

        while (!connected) {
            try {
                connector.connect();
                connected = true;
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                System.err.println("[" + roomId + "] Server unavailable: " + e.getMessage());
                System.out.println("[" + roomId + "] Retrying in 5 seconds...");
                try { Thread.sleep(5000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
            }
        }

        System.out.println("[" + roomId + "] Connected successfully.\n");

        // Register room with server
        try {
            RoomConnectionInfo info = new RoomConnectionInfo(roomId, RMI_HOST);
            connector.getHeartbeatService().registerRoom(info);
            state = ConnectionState.ONLINE;
            System.out.println("[" + roomId + "] " + roomId + " -> REGISTERED");
            System.out.println("[" + roomId + "] " + roomId + " is ONLINE");
        } catch (RemoteException e) {
            System.err.println("[" + roomId + "] Registration failed: " + e.getMessage());
        }

        // Wire up reconnection and heartbeat managers
        reconnectionManager = new ReconnectionManager(roomId, RMI_HOST, RMI_PORT, connector);
        heartbeatManager = new HeartbeatManager(roomId, connector.getHeartbeatService(), reconnectionManager);
        reconnectionManager.setHeartbeatManager(heartbeatManager);

        // Start heartbeat
        heartbeatManager.start();

        // Shutdown hook for clean exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[" + roomId + "] Shutting down...");
            heartbeatManager.shutdown();
            reconnectionManager.shutdown();
        }));

        // Keep JVM alive
        System.out.println("\n[" + roomId + "] Running. Press Ctrl+C to stop.");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void printBanner() {
        System.out.println("=================================");
        System.out.println(" Distributed Exam — Room Client");
        System.out.println("=================================");
        System.out.println("\nRoom ID : " + roomId);
        System.out.println("Server  : " + RMI_HOST + ":" + RMI_PORT);
        System.out.println("\nConnecting to RMI Server...");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java com.distributedexam.client.RoomClient <ROOM-ID>");
            System.err.println("Example: java com.distributedexam.client.RoomClient ROOM-A");
            System.exit(1);
        }
        new RoomClient(args[0].toUpperCase()).start();
    }
}
