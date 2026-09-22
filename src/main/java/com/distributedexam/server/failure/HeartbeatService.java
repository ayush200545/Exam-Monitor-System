package com.distributedexam.server.failure;

import com.distributedexam.common.ConnectionState;
import com.distributedexam.common.RoomConnectionInfo;
import com.distributedexam.remote.HeartbeatRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side implementation of HeartbeatRemote.
 * Tracks room registrations and heartbeat timestamps using thread-safe collections.
 * Delegates recovery events to RecoveryManager.
 */
public class HeartbeatService extends UnicastRemoteObject implements HeartbeatRemote {

    private static final long serialVersionUID = 1L;

    /** Thread-safe map: roomId -> RoomConnectionInfo */
    private final ConcurrentHashMap<String, RoomConnectionInfo> roomRegistry = new ConcurrentHashMap<>();
    private final RecoveryManager recoveryManager;

    public HeartbeatService() throws RemoteException {
        super();
        this.recoveryManager = new RecoveryManager(roomRegistry);
    }

    @Override
    public void registerRoom(RoomConnectionInfo info) throws RemoteException {
        // Check if this room was previously OFFLINE (recovery scenario)
        RoomConnectionInfo existing = roomRegistry.get(info.getRoomId());
        boolean recovering = existing != null && existing.getConnectionState() == ConnectionState.OFFLINE;

        info.setConnectionState(ConnectionState.ONLINE);
        info.setLastHeartbeat(Instant.now());
        roomRegistry.put(info.getRoomId(), info);

        if (recovering) {
            System.out.println("\n[RECOVERY] Room " + info.getRoomId() + " has reconnected.");
            System.out.println("[RECOVERY] " + info.getRoomId() + " -> ONLINE\n");
        } else {
            System.out.println("[SERVER] Room " + info.getRoomId() + " registered -> ONLINE");
        }
    }

    @Override
    public void heartbeat(String roomId) throws RemoteException {
        RoomConnectionInfo info = roomRegistry.get(roomId);
        if (info != null) {
            info.setLastHeartbeat(Instant.now());
            info.setConnectionState(ConnectionState.ONLINE);
            System.out.println("[HEARTBEAT] " + roomId + " heartbeat received");
        } else {
            // Room not registered yet — register on-the-fly
            RoomConnectionInfo newInfo = new RoomConnectionInfo(roomId, "unknown");
            newInfo.setConnectionState(ConnectionState.ONLINE);
            roomRegistry.put(roomId, newInfo);
            System.out.println("[HEARTBEAT] " + roomId + " first heartbeat — auto-registered");
        }
    }

    @Override
    public List<RoomConnectionInfo> getAllRoomConnections() throws RemoteException {
        return new ArrayList<>(roomRegistry.values());
    }

    /** Used by FailureDetector and RMIServer to inspect the registry */
    public ConcurrentHashMap<String, RoomConnectionInfo> getRoomRegistry() {
        return roomRegistry;
    }
}
