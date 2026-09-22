package com.exam.server;

import com.exam.common.rmi.RemoteMonitoringService;
import com.exam.dao.EventDAO;
import com.exam.dao.IncidentDAO;
import com.exam.dao.InMemoryEventDAO;
import com.exam.dao.InMemoryIncidentDAO;
import com.exam.server.service.EventManager;
import com.exam.server.service.FailureDetector;
import com.exam.server.service.HeartbeatManager;
import com.exam.server.service.IncidentManager;
import com.exam.server.service.MonitoringServiceImpl;
import com.exam.server.service.RecoveryManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Bootstrap entry point for the Central Examination Monitoring Server.
 */
public class ServerMain {
    public static final int RMI_PORT = 1099;
    public static final String SERVICE_NAME = "ExamMonitoringService";
    public static final long FAILURE_TIMEOUT_MS = 15000; // 15 seconds
    public static final long FAILURE_CHECK_INTERVAL_MS = 2000; // 2 seconds

    public static void main(String[] args) {
        try {
            System.out.println("=================================================");
            System.out.println("   CENTRAL EXAM MONITORING SERVER - MEMBER 3     ");
            System.out.println("=================================================");

            // 1. Initialize DAO Layer
            IncidentDAO incidentDAO = new InMemoryIncidentDAO();
            EventDAO eventDAO = new InMemoryEventDAO();

            // 2. Initialize Core Services
            EventManager eventManager = new EventManager(eventDAO);
            IncidentManager incidentManager = new IncidentManager(incidentDAO, eventManager);
            HeartbeatManager heartbeatManager = new HeartbeatManager(eventManager);
            RecoveryManager recoveryManager = new RecoveryManager(heartbeatManager, eventManager);

            // 3. Start Failure Detector Daemon
            FailureDetector failureDetector = new FailureDetector(
                    heartbeatManager, eventManager, FAILURE_TIMEOUT_MS, FAILURE_CHECK_INTERVAL_MS);
            failureDetector.start();

            // 4. Create RMI Implementation
            RemoteMonitoringService monitoringService = new MonitoringServiceImpl(
                    incidentManager, eventManager, heartbeatManager, recoveryManager);

            // 5. Create or get RMI Registry & bind service
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
                System.out.println("[RMI Registry] Created on port " + RMI_PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry(RMI_PORT);
                System.out.println("[RMI Registry] Located on existing port " + RMI_PORT);
            }

            registry.rebind(SERVICE_NAME, monitoringService);
            System.out.println("[RMI Service] Bound as: " + SERVICE_NAME);
            System.out.println("[Server Ready] Listening for Room Clients and Heartbeats...");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("[Server Error] Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
