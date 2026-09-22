package com.exam.test;

import com.exam.common.model.ExamEvent;
import com.exam.common.model.Incident;
import com.exam.common.model.IncidentStatus;
import com.exam.common.model.IncidentType;
import com.exam.common.model.RoomStatus;
import com.exam.dao.InMemoryEventDAO;
import com.exam.dao.InMemoryIncidentDAO;
import com.exam.server.service.EventManager;
import com.exam.server.service.FailureDetector;
import com.exam.server.service.HeartbeatManager;
import com.exam.server.service.IncidentManager;
import com.exam.server.service.MonitoringServiceImpl;
import com.exam.server.service.RecoveryManager;

import java.util.List;

/**
 * End-to-End Automated Demonstration Test for Member 3:
 * 1. Multi-room registration (ROOM-101, ROOM-102)
 * 2. Heartbeat tracking
 * 3. Incident reporting & lifecycle (OPEN -> ACKNOWLEDGED -> RESOLVED)
 * 4. Event logging & query
 * 5. Room 101 failure simulation (stop heartbeat -> timeout -> OFFLINE)
 * 6. Room 102 normal operation verification
 * 7. Room 101 recovery (reconnect -> resume heartbeat -> RECONNECTED/ONLINE)
 */
public class SimulationDemo {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   DCS EXAM MONITOR SYSTEM — MEMBER 3 AUTOMATED VERIFICATION DEMO     ");
        System.out.println("======================================================================\n");

        try {
            // 1. Setup subsystem with fast test timeouts (4s timeout, 1s check interval)
            long testTimeoutMs = 4000;
            long testCheckIntervalMs = 1000;

            InMemoryIncidentDAO incidentDAO = new InMemoryIncidentDAO();
            InMemoryEventDAO eventDAO = new InMemoryEventDAO();
            EventManager eventManager = new EventManager(eventDAO);
            IncidentManager incidentManager = new IncidentManager(incidentDAO, eventManager);
            HeartbeatManager heartbeatManager = new HeartbeatManager(eventManager);
            RecoveryManager recoveryManager = new RecoveryManager(heartbeatManager, eventManager);

            FailureDetector failureDetector = new FailureDetector(
                    heartbeatManager, eventManager, testTimeoutMs, testCheckIntervalMs);
            failureDetector.start();

            MonitoringServiceImpl service = new MonitoringServiceImpl(
                    incidentManager, eventManager, heartbeatManager, recoveryManager);

            System.out.println("[STEP 1] Initialized Central Server Subsystem (Timeout: " + testTimeoutMs + "ms)");

            // 2. Register Rooms
            System.out.println("\n[STEP 2] Registering ROOM-101 and ROOM-102...");
            service.registerRoom("ROOM-101");
            service.registerRoom("ROOM-102");
            assertStatus(service.getRoomStatus("ROOM-101"), RoomStatus.ONLINE, "ROOM-101 registration");
            assertStatus(service.getRoomStatus("ROOM-102"), RoomStatus.ONLINE, "ROOM-102 registration");

            // 3. Pulse Heartbeats
            System.out.println("\n[STEP 3] Pulsing heartbeats for both rooms...");
            service.sendHeartbeat("ROOM-101");
            service.sendHeartbeat("ROOM-102");
            System.out.println("✓ Heartbeats successfully recorded.");

            // 4. Incident Management Lifecycle
            System.out.println("\n[STEP 4] Testing Incident Management Lifecycle...");
            String incId = service.reportIncident("ROOM-101", IncidentType.TECHNICAL_PROBLEM, "Screen flickering on Station 3");
            System.out.println("✓ Incident created: " + incId);

            Incident inc = service.getIncident(incId);
            assertLifecycle(inc.getStatus(), IncidentStatus.OPEN, "Initial status");

            service.acknowledgeIncident(incId);
            inc = service.getIncident(incId);
            assertLifecycle(inc.getStatus(), IncidentStatus.ACKNOWLEDGED, "Acknowledged status");

            service.resolveIncident(incId);
            inc = service.getIncident(incId);
            assertLifecycle(inc.getStatus(), IncidentStatus.RESOLVED, "Resolved status");

            // 5. Event Logging Verification
            System.out.println("\n[STEP 5] Checking Event Logging...");
            List<ExamEvent> events = service.getEvents();
            System.out.printf("✓ Total events logged so far: %d%n", events.size());
            for (ExamEvent ev : events) {
                System.out.println("   -> " + ev);
            }

            // 6. Failure Detection Demonstration
            System.out.println("\n[STEP 6] Simulating Failure: ROOM-101 stops heartbeats. ROOM-102 continues heartbeats.");
            System.out.println("Waiting 5.5 seconds for FailureDetector timeout (>4000ms)...");

            for (int i = 0; i < 5; i++) {
                Thread.sleep(1100);
                service.sendHeartbeat("ROOM-102"); // Room 102 keeps pulsing
                System.out.print(".");
            }
            System.out.println();

            // Check room statuses
            RoomStatus r101Status = service.getRoomStatus("ROOM-101");
            RoomStatus r102Status = service.getRoomStatus("ROOM-102");
            System.out.println("STATUS AFTER TIMEOUT:");
            System.out.println("   ROOM-101: " + r101Status + " (Expected: OFFLINE)");
            System.out.println("   ROOM-102: " + r102Status + " (Expected: ONLINE)");

            if (r101Status != RoomStatus.OFFLINE) {
                throw new RuntimeException("Failure detection failed! ROOM-101 is not OFFLINE.");
            }
            if (r102Status != RoomStatus.ONLINE) {
                throw new RuntimeException("ROOM-102 was erroneously affected!");
            }
            System.out.println("✓ Failure correctly detected by server!");

            // 7. Recovery Demonstration
            System.out.println("\n[STEP 7] Simulating Reconnection & Recovery of ROOM-101...");
            service.sendHeartbeat("ROOM-101"); // Resumes heartbeat -> triggers automatic recovery
            RoomStatus recoveredStatus = service.getRoomStatus("ROOM-101");
            System.out.println("STATUS AFTER RECONNECTION:");
            System.out.println("   ROOM-101: " + recoveredStatus + " (Expected: ONLINE)");

            if (recoveredStatus != RoomStatus.ONLINE) {
                throw new RuntimeException("Recovery failed! ROOM-101 is not ONLINE.");
            }
            System.out.println("✓ Recovery successfully completed!");

            // 8. Print Final Event Audit Trail
            System.out.println("\n[STEP 8] Final Event Audit Log:");
            for (ExamEvent ev : service.getEvents()) {
                System.out.println("   [AUDIT] " + ev);
            }

            failureDetector.stop();

            System.out.println("\n======================================================================");
            System.out.println("   ALL MEMBER 3 DCS CORE MODULE TESTS PASSED SUCCESSFULLY!          ");
            System.out.println("======================================================================");

        } catch (Exception e) {
            System.err.println("\n[DEMO FAILED]: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void assertStatus(RoomStatus actual, RoomStatus expected, String label) {
        if (actual == expected) {
            System.out.println("✓ " + label + " -> " + actual);
        } else {
            throw new RuntimeException("Assertion failed for " + label + ". Expected: " + expected + ", Got: " + actual);
        }
    }

    private static void assertLifecycle(IncidentStatus actual, IncidentStatus expected, String label) {
        if (actual == expected) {
            System.out.println("✓ Incident " + label + " -> " + actual);
        } else {
            throw new RuntimeException("Lifecycle assertion failed: " + label + ". Expected: " + expected + ", Got: " + actual);
        }
    }
}
