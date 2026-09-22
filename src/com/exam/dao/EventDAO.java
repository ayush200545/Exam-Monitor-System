package com.exam.dao;

import com.exam.common.model.ExamEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object interface for Event persistence.
 * Allows seamless swap between In-Memory store and Member 4's PostgreSQL database.
 */
public interface EventDAO {
    void save(ExamEvent event);
    List<ExamEvent> findAll();
    List<ExamEvent> findByRoomId(String roomId);
    List<ExamEvent> findByTimeRange(LocalDateTime start, LocalDateTime end);
}
