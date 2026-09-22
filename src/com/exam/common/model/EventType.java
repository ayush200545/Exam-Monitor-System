package com.exam.common.model;

import java.io.Serializable;

/**
 * Event categories tracked by the distributed monitoring system.
 */
public enum EventType implements Serializable {
    ROOM_REGISTERED,
    EXAM_STARTED,
    ATTENDANCE_UPDATED,
    INCIDENT_REPORTED,
    INCIDENT_RESOLVED,
    STUDENT_SUBMITTED,
    ROOM_OFFLINE,
    ROOM_RECONNECTED,
    EXAM_COMPLETED
}
