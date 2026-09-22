package com.exam.dao;

import com.exam.common.model.Incident;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Incident persistence.
 * Allows seamless swap between In-Memory store and Member 4's PostgreSQL database.
 */
public interface IncidentDAO {
    void save(Incident incident);
    Optional<Incident> findById(String incidentId);
    List<Incident> findAll();
    List<Incident> findByRoomId(String roomId);
    void update(Incident incident);
}
