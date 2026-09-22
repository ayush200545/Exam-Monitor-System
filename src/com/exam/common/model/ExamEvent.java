package com.exam.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a distributed event occurring within an exam room or server.
 */
public class ExamEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String roomId;
    private EventType eventType;
    private LocalDateTime timestamp;
    private String description;

    public ExamEvent(String eventId, String roomId, EventType eventType, String description) {
        this.eventId = eventId;
        this.roomId = roomId;
        this.eventType = eventType;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public ExamEvent(String eventId, String roomId, EventType eventType, String description, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.roomId = roomId;
        this.eventType = eventType;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public String getRoomId() {
        return roomId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[Event %s | Room: %s | Type: %s | Time: %s | Details: %s]",
                eventId, roomId, eventType, timestamp.format(fmt), description);
    }
}
