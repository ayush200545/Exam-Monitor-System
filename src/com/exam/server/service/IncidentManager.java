package com.exam.server.service;

import com.exam.common.model.Incident;
import com.exam.common.model.IncidentStatus;
import com.exam.common.model.IncidentType;
import com.exam.dao.IncidentDAO;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Manages incident lifecycle and querying on the Central Server.
 */
public class IncidentManager {
    private final IncidentDAO incidentDAO;
    private final EventManager eventManager;
    private final AtomicLong incidentCounter = new AtomicLong(1000);

    public IncidentManager(IncidentDAO incidentDAO, EventManager eventManager) {
        this.incidentDAO = incidentDAO;
        this.eventManager = eventManager;
    }

    public synchronized String createIncident(String roomId, IncidentType type, String description) {
        String id = "INC-" + incidentCounter.incrementAndGet();
        Incident incident = new Incident(id, roomId, type, description);
        incidentDAO.save(incident);

        if (eventManager != null) {
            eventManager.logEvent(roomId, com.exam.common.model.EventType.INCIDENT_REPORTED,
                    "Incident reported [" + id + "]: " + description);
        }
        return id;
    }

    public Incident getIncident(String incidentId) {
        return incidentDAO.findById(incidentId).orElse(null);
    }

    public List<Incident> getOpenIncidents() {
        return incidentDAO.findAll().stream()
                .filter(i -> i.getStatus() == IncidentStatus.OPEN || i.getStatus() == IncidentStatus.ACKNOWLEDGED)
                .collect(Collectors.toList());
    }

    public List<Incident> getIncidentsByRoom(String roomId) {
        return incidentDAO.findByRoomId(roomId);
    }

    public synchronized boolean acknowledgeIncident(String incidentId) {
        Optional<Incident> opt = incidentDAO.findById(incidentId);
        if (opt.isPresent()) {
            Incident inc = opt.get();
            if (inc.getStatus() == IncidentStatus.OPEN) {
                inc.setStatus(IncidentStatus.ACKNOWLEDGED);
                incidentDAO.update(inc);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean markInProgress(String incidentId) {
        Optional<Incident> opt = incidentDAO.findById(incidentId);
        if (opt.isPresent()) {
            Incident inc = opt.get();
            inc.setStatus(IncidentStatus.IN_PROGRESS);
            incidentDAO.update(inc);
            return true;
        }
        return false;
    }

    public synchronized boolean resolveIncident(String incidentId) {
        Optional<Incident> opt = incidentDAO.findById(incidentId);
        if (opt.isPresent()) {
            Incident inc = opt.get();
            inc.setStatus(IncidentStatus.RESOLVED);
            incidentDAO.update(inc);

            if (eventManager != null) {
                eventManager.logEvent(inc.getRoomId(), com.exam.common.model.EventType.INCIDENT_RESOLVED,
                        "Incident resolved [" + incidentId + "]");
            }
            return true;
        }
        return false;
    }
}
