package com.exam.common.model;

import java.io.Serializable;

/**
 * Categories of examination room incidents.
 */
public enum IncidentType implements Serializable {
    STUDENT_ISSUE,
    TECHNICAL_PROBLEM,
    SUSPICIOUS_ACTIVITY,
    ROOM_ENVIRONMENT,
    NETWORK_DISRUPTION,
    OTHER
}
