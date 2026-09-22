package com.distributedexam.common;

import java.io.Serializable;
import java.time.Instant;

/**
 * Communication-level information about a connected room client.
 * Distinct from the business-level Room entity — this tracks network/connection state only.
 */
public class RoomConnectionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomId;
    private String host;
    private ConnectionState connectionState;
    private Instant lastHeartbeat;

    public RoomConnectionInfo() {}

    public RoomConnectionInfo(String roomId, String host) {
        this.roomId = roomId;
        this.host = host;
        this.connectionState = ConnectionState.CONNECTING;
        this.lastHeartbeat = Instant.now();
    }

    public String getRoomId()               { return roomId; }
    public void setRoomId(String roomId)    { this.roomId = roomId; }

    public String getHost()                 { return host; }
    public void setHost(String host)        { this.host = host; }

    public ConnectionState getConnectionState()                    { return connectionState; }
    public void setConnectionState(ConnectionState connectionState) { this.connectionState = connectionState; }

    public Instant getLastHeartbeat()                 { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    @Override
    public String toString() {
        return "RoomConnectionInfo{roomId='" + roomId + "', host='" + host +
               "', state=" + connectionState + ", lastHeartbeat=" + lastHeartbeat + "}";
    }
}
