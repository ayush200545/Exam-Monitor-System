package com.distributedexam.remote;

import com.distributedexam.common.RoomConnectionInfo;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for room registration and heartbeat on the central server.
 * Implemented by the server-side HeartbeatService.
 */
public interface HeartbeatRemote extends Remote {

    /**
     * Called by a Room Client when it first starts up to register itself.
     * @param info connection information (roomId, host, state)
     */
    void registerRoom(RoomConnectionInfo info) throws RemoteException;

    /**
     * Called periodically by each Room Client to signal it is still alive.
     * @param roomId the room sending the heartbeat
     */
    void heartbeat(String roomId) throws RemoteException;

    /**
     * Returns a snapshot of all currently registered rooms and their states.
     */
    List<RoomConnectionInfo> getAllRoomConnections() throws RemoteException;
}
