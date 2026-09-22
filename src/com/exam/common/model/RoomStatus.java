package com.exam.common.model;

import java.io.Serializable;

/**
 * Distributed connectivity state of an exam room.
 */
public enum RoomStatus implements Serializable {
    ONLINE,
    OFFLINE,
    RECONNECTED
}
