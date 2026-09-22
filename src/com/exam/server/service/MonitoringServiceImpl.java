package com.exam.server.service;

import com.exam.common.model.ExamEvent;
import com.exam.common.model.EventType;
import com.exam.common.model.Incident;
import com.exam.common.model.IncidentType;
import com.exam.common.model.RoomStatus;
import com.exam.common.rmi.RemoteMonitoringService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * Concrete RMI implementation of RemoteMonitoringService.
 * Coordinates Incident, Event, Heartbeat, Failure, and Recovery managers.
 */
public class MonitoringServiceImpl extends UnicastRemoteObject implements RemoteMonitoringService {
    private static final long serialVersionUID = 1L;

    private final IncidentManager incidentManager;
    private final EventManager eventManager;
    private final HeartbeatManager heartbeatManager;
    private final RecoveryManager recoveryManager;

    public MonitoringServiceImpl(IncidentManager incidentManager,
                                 EventManager eventManager,
                                 HeartbeatManager heartbeatManager,
                                 RecoveryManager recoveryManager) throws RemoteException {
        super();
        this.incidentManager = incidentManager;
        this.eventManager = eventManager;
        this.heartbeatManager = heartbeatManager;
        this.recoveryManager = recoveryManager;
    }

    // --- Room Registration & Heartbeat ---
    @Override
    public boolean registerRoom(String roomId) throws RemoteException {
        return heartbeatManager.registerRoom(roomId);
    }

    @Override
    public void sendHeartbeat(String roomId) throws RemoteException {
        // If room was detected OFFLINE earlier and now pulses, trigger recovery
        if (heartbeatManager.getRoomStatus(roomId) == RoomStatus.OFFLINE) {
            recoveryManager.handleRecovery(roomId);
        } else {
            heartbeatManager.recordHeartbeat(roomId);
        }
    }

    @Override
    public RoomStatus getRoomStatus(String roomId) throws RemoteException {
        return heartbeatManager.getRoomStatus(roomId);
    }

    @Override
    public Map<String, RoomStatus> getAllRoomStatuses() throws RemoteException {
        return heartbeatManager.getAllRoomStatuses();
    }

    // --- Incident Management ---
    @Override
    public String reportIncident(String roomId, IncidentType type, String description) throws RemoteException {
        return incidentManager.createIncident(roomId, type, description);
    }

    @Override
    public Incident getIncident(String incidentId) throws RemoteException {
        return incidentManager.getIncident(incidentId);
    }

    @Override
    public List<Incident> getOpenIncidents() throws RemoteException {
        return incidentManager.getOpenIncidents();
    }

    @Override
    public List<Incident> getIncidentsByRoom(String roomId) throws RemoteException {
        return incidentManager.getIncidentsByRoom(roomId);
    }

    @Override
    public boolean acknowledgeIncident(String incidentId) throws RemoteException {
        return incidentManager.acknowledgeIncident(incidentId);
    }

    @Override
    public boolean resolveIncident(String incidentId) throws RemoteException {
        return incidentManager.resolveIncident(incidentId);
    }

    // --- Event Logging ---
    @Override
    public void logEvent(String roomId, EventType type, String description) throws RemoteException {
        eventManager.logEvent(roomId, type, description);
    }

    @Override
    public List<ExamEvent> getEvents() throws RemoteException {
        return eventManager.getEvents();
    }

    @Override
    public List<ExamEvent> getEventsByRoom(String roomId) throws RemoteException {
        return eventManager.getEventsByRoom(roomId);
    }

    @Override
    public List<ExamEvent> getRecentEvents(int limit) throws RemoteException {
        return eventManager.getRecentEvents(limit);
    }

    // --- Recovery ---
    @Override
    public boolean recoverRoom(String roomId) throws RemoteException {
        return recoveryManager.handleRecovery(roomId);
    }
}
