package com.distributedexam.common;

import java.io.Serializable;

public enum RoomStatus implements Serializable {
    AVAILABLE, ASSIGNED, EXAM_RUNNING, OFFLINE, MAINTENANCE
}
