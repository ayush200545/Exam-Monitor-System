package com.exam.dao;

import com.exam.common.model.ExamEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of EventDAO.
 */
public class InMemoryEventDAO implements EventDAO {
    private final List<ExamEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void save(ExamEvent event) {
        events.add(event);
    }

    @Override
    public List<ExamEvent> findAll() {
        return new ArrayList<>(events);
    }

    @Override
    public List<ExamEvent> findByRoomId(String roomId) {
        return events.stream()
                .filter(e -> e.getRoomId().equalsIgnoreCase(roomId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamEvent> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        return events.stream()
                .filter(e -> !e.getTimestamp().isBefore(start) && !e.getTimestamp().isAfter(end))
                .collect(Collectors.toList());
    }
}
