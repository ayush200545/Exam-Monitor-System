package com.exam.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an examination incident reported by a room client or coordinator.
 */
public class Incident implements Serializable {
    private static final long serialVersionUID = 1L;

    private String incidentId;
    private String roomId;
    private IncidentType type;
    private String description;
    private IncidentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public Incident(String incidentId, String roomId, IncidentType type, String description) {
        this.incidentId = incidentId;
        this.roomId = roomId;
        this.type = type;
        this.description = description;
        this.status = IncidentStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getRoomId() {
        return roomId;
    }

    public IncidentType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
        if (status == IncidentStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[Incident %s | Room: %s | Type: %s | Status: %s | Created: %s | Desc: %s]",
                incidentId, roomId, type, status, createdAt.format(fmt), description);
    }
}
