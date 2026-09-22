package com.exam.common.model;

import java.io.Serializable;

/**
 * Incident lifecycle status:
 * OPEN -> ACKNOWLEDGED -> IN_PROGRESS -> RESOLVED
 */
public enum IncidentStatus implements Serializable {
    OPEN,
    ACKNOWLEDGED,
    IN_PROGRESS,
    RESOLVED
}
