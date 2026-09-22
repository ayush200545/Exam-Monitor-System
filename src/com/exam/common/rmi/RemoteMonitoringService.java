package com.exam.common.rmi;

import com.exam.common.model.ExamEvent;
import com.exam.common.model.EventType;
import com.exam.common.model.Incident;
import com.exam.common.model.IncidentType;
import com.exam.common.model.RoomStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Remote interface exposed by the Central Server for room monitoring,
 * heartbeat pulsing, incident handling, event logging, and failure recovery.
 */
public interface RemoteMonitoringService extends Remote {

    // --- Room Registration & Heartbeat ---
    boolean registerRoom(String roomId) throws RemoteException;
    void sendHeartbeat(String roomId) throws RemoteException;
    RoomStatus getRoomStatus(String roomId) throws RemoteException;
    Map<String, RoomStatus> getAllRoomStatuses() throws RemoteException;

    // --- Incident Management ---
    String reportIncident(String roomId, IncidentType type, String description) throws RemoteException;
    Incident getIncident(String incidentId) throws RemoteException;
    List<Incident> getOpenIncidents() throws RemoteException;
    List<Incident> getIncidentsByRoom(String roomId) throws RemoteException;
    boolean acknowledgeIncident(String incidentId) throws RemoteException;
    boolean resolveIncident(String incidentId) throws RemoteException;

    // --- Event Logging ---
    void logEvent(String roomId, EventType type, String description) throws RemoteException;
    List<ExamEvent> getEvents() throws RemoteException;
    List<ExamEvent> getEventsByRoom(String roomId) throws RemoteException;
    List<ExamEvent> getRecentEvents(int limit) throws RemoteException;

    // --- Recovery / Reconnection ---
    boolean recoverRoom(String roomId) throws RemoteException;
}
