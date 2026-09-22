package com.exam.server.service;

import com.exam.common.model.ExamEvent;
import com.exam.common.model.EventType;
import com.exam.dao.EventDAO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Manages distributed event logging and event history querying on the Central Server.
 */
public class EventManager {
    private final EventDAO eventDAO;
    private final AtomicLong eventCounter = new AtomicLong(5000);

    public EventManager(EventDAO eventDAO) {
        this.eventDAO = eventDAO;
    }

    public synchronized ExamEvent logEvent(String roomId, EventType type, String description) {
        String id = "EVT-" + eventCounter.incrementAndGet();
        ExamEvent event = new ExamEvent(id, roomId, type, description);
        eventDAO.save(event);
        System.out.println("[AUDIT LOG] " + event);
        return event;
    }

    public List<ExamEvent> getEvents() {
        return eventDAO.findAll();
    }

    public List<ExamEvent> getEventsByRoom(String roomId) {
        return eventDAO.findByRoomId(roomId);
    }

    public List<ExamEvent> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return eventDAO.findByTimeRange(start, end);
    }

    public List<ExamEvent> getRecentEvents(int limit) {
        List<ExamEvent> all = eventDAO.findAll();
        return all.stream()
                .sorted(Comparator.comparing(ExamEvent::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
