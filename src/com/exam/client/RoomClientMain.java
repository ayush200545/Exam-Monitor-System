package com.exam.client;

import com.exam.client.service.HeartbeatService;
import com.exam.client.service.LocalEventQueue;
import com.exam.common.model.EventType;
import com.exam.common.model.IncidentType;
import com.exam.common.rmi.RemoteMonitoringService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Room Client node that connects to the Central Server via Java RMI,
 * pulses heartbeats, reports incidents, and buffers events during disconnects.
 */
public class RoomClientMain {
    private final String roomId;
    private final String host;
    private final int port;
    private RemoteMonitoringService monitoringService;
    private HeartbeatService heartbeatService;
    private final LocalEventQueue offlineQueue;

    public RoomClientMain(String roomId, String host, int port) {
        this.roomId = roomId;
        this.host = host;
        this.port = port;
        this.offlineQueue = new LocalEventQueue(roomId);
    }

    public boolean connectAndRegister() {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            this.monitoringService = (RemoteMonitoringService) registry.lookup("ExamMonitoringService");
            monitoringService.registerRoom(roomId);
            System.out.printf("[%s] Successfully connected to Central Server and registered.%n", roomId);

            // Start heartbeat pulsing (every 5 seconds)
            this.heartbeatService = new HeartbeatService(roomId, monitoringService, 5);
            heartbeatService.start();

            // Flush any previously buffered offline events
            offlineQueue.flushPendingEvents(monitoringService);
            return true;
        } catch (Exception e) {
            System.err.printf("[%s] Connection failed: %s%n", roomId, e.getMessage());
            return false;
        }
    }

    public void reportIncident(IncidentType type, String description) {
        if (monitoringService != null) {
            try {
                String incId = monitoringService.reportIncident(roomId, type, description);
                System.out.printf("[%s] Reported Incident %s: %s%n", roomId, incId, description);
            } catch (Exception e) {
                System.err.printf("[%s] Remote error reporting incident, buffering locally: %s%n", roomId, e.getMessage());
                offlineQueue.queueEvent(EventType.INCIDENT_REPORTED, description);
            }
        } else {
            offlineQueue.queueEvent(EventType.INCIDENT_REPORTED, description);
        }
    }

    public void logEvent(EventType type, String description) {
        if (monitoringService != null) {
            try {
                monitoringService.logEvent(roomId, type, description);
                System.out.printf("[%s] Logged event %s: %s%n", roomId, type, description);
            } catch (Exception e) {
                System.err.printf("[%s] Remote error logging event, buffering locally: %s%n", roomId, e.getMessage());
                offlineQueue.queueEvent(type, description);
            }
        } else {
            offlineQueue.queueEvent(type, description);
        }
    }

    public void disconnect() {
        if (heartbeatService != null) {
            heartbeatService.stop();
        }
        System.out.printf("[%s] Disconnected from server.%n", roomId);
    }

    public static void main(String[] args) {
        String roomId = args.length > 0 ? args[0] : "ROOM-101";
        String host = args.length > 1 ? args[1] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 1099;

        System.out.println("=================================================");
        System.out.println("          EXAM ROOM CLIENT - " + roomId);
        System.out.println("=================================================");

        RoomClientMain client = new RoomClientMain(roomId, host, port);
        boolean connected = client.connectAndRegister();

        if (!connected) {
            System.err.println("Could not connect to Central Server. Exiting.");
            return;
        }

        client.logEvent(EventType.ROOM_REGISTERED, "Room " + roomId + " joined the examination session.");

        // Interactive command loop for testing
        System.out.println("\nCommands available:");
        System.out.println("  1 -> Report Student Issue Incident");
        System.out.println("  2 -> Report Technical Problem Incident");
        System.out.println("  3 -> Log Student Submitted Event");
        System.out.println("  4 -> Stop Heartbeat (Simulate Failure)");
        System.out.println("  5 -> Resume Heartbeat (Simulate Reconnection/Recovery)");
        System.out.println("  q -> Quit");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String cmd = scanner.nextLine().trim();
            if ("q".equalsIgnoreCase(cmd)) {
                client.disconnect();
                break;
            } else if ("1".equals(cmd)) {
                client.reportIncident(IncidentType.STUDENT_ISSUE, "Student attempting to communicate without authorization.");
            } else if ("2".equals(cmd)) {
                client.reportIncident(IncidentType.TECHNICAL_PROBLEM, "Workstation terminal 04 power flicker.");
            } else if ("3".equals(cmd)) {
                client.logEvent(EventType.STUDENT_SUBMITTED, "Student STU-882 submitted exam paper.");
            } else if ("4".equals(cmd)) {
                System.out.println("Simulating room failure: STOPPING heartbeats...");
                client.heartbeatService.stop();
            } else if ("5".equals(cmd)) {
                System.out.println("Simulating room recovery: RESUMING heartbeats...");
                client.heartbeatService.start();
            } else {
                System.out.println("Unknown command: " + cmd);
            }
        }
        scanner.close();
    }
}
