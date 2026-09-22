package com.exam.dao;

import com.exam.common.model.Incident;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of IncidentDAO.
 */
public class InMemoryIncidentDAO implements IncidentDAO {
    private final Map<String, Incident> store = new ConcurrentHashMap<>();

    @Override
    public void save(Incident incident) {
        store.put(incident.getIncidentId(), incident);
    }

    @Override
    public Optional<Incident> findById(String incidentId) {
        return Optional.ofNullable(store.get(incidentId));
    }

    @Override
    public List<Incident> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Incident> findByRoomId(String roomId) {
        return store.values().stream()
                .filter(i -> i.getRoomId().equalsIgnoreCase(roomId))
                .collect(Collectors.toList());
    }

    @Override
    public void update(Incident incident) {
        store.put(incident.getIncidentId(), incident);
    }
}
