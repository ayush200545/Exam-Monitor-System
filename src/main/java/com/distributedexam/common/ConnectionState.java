package com.distributedexam.common;

import java.io.Serializable;

/**
 * Communication-level connection state for a Room Client JVM.
 * Do NOT confuse with business-level RoomStatus (AVAILABLE, ASSIGNED, etc.).
 */
public enum ConnectionState implements Serializable {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ONLINE,
    RECONNECTING,
    OFFLINE
}
