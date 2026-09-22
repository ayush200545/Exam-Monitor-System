package com.distributedexam.server;

import com.distributedexam.server.failure.FailureDetector;
import com.distributedexam.server.failure.HeartbeatService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * RMIServer — central server JVM entry point.
 * Starts the RMI Registry, binds all remote services, and starts failure detection.
 *
 * Run:
 *   java -cp "bin;lib/*" com.distributedexam.server.RMIServer
 */
public class RMIServer {

    public static void main(String[] args) {
        try {
            System.out.println("=================================");
            System.out.println(" Distributed Exam RMI Server");
            System.out.println("=================================");
            System.out.println("\nStarting RMI Registry...");

            LocateRegistry.createRegistry(1099);
            System.out.println("Registry running on port 1099\n");

            System.out.println("Binding remote services...\n");

            // -- Member 2 business managers --
            com.distributedexam.server.exam.ExamManager examManager             = new com.distributedexam.server.exam.ExamManager();
            com.distributedexam.server.room.RoomManager roomManager             = new com.distributedexam.server.room.RoomManager();
            com.distributedexam.server.attendance.AttendanceManager attManager  = new com.distributedexam.server.attendance.AttendanceManager();
            com.distributedexam.server.submission.SubmissionManager subManager  = new com.distributedexam.server.submission.SubmissionManager();
            com.distributedexam.server.student.StudentManager studentManager    = new com.distributedexam.server.student.StudentManager();
            com.distributedexam.server.auth.AuthManager authManager = new com.distributedexam.server.auth.AuthManager();
            com.distributedexam.server.monitoring.MonitoringManager monitoringManager = new com.distributedexam.server.monitoring.MonitoringManager();
            com.distributedexam.server.dashboard.DashboardManager dashboardManager = new com.distributedexam.server.dashboard.DashboardManager();

            Naming.rebind("ExamService",       examManager);
            System.out.println("ExamService       -> READY");

            Naming.rebind("RoomService",       roomManager);
            System.out.println("RoomService       -> READY");

            Naming.rebind("StudentService",    studentManager);
            System.out.println("StudentService    -> READY");

            Naming.rebind("AttendanceService", attManager);
            System.out.println("AttendanceService -> READY");

            Naming.rebind("SubmissionService", subManager);
            System.out.println("SubmissionService -> READY");
            Naming.rebind("AuthService", authManager);
            Naming.rebind("MonitoringService", monitoringManager);
            Naming.rebind("DashboardService", dashboardManager);
            System.out.println("AuthService, MonitoringService, DashboardService -> READY");

            // -- Member 1 distributed communication services --
            HeartbeatService heartbeatService = new HeartbeatService();
            Naming.rebind("HeartbeatService",  heartbeatService);
            System.out.println("HeartbeatService  -> READY");

            // Start failure detector (uses same registry from HeartbeatService)
            FailureDetector failureDetector = new FailureDetector(heartbeatService.getRoomRegistry());
            failureDetector.start();

            System.out.println("\nRMI Server is READY");
            System.out.println("Waiting for clients...\n");

            // Shutdown hook for clean exit
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[SERVER] Shutting down...");
                failureDetector.stop();
            }));

            // Keep server JVM alive
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("RMI Server failed to start:");
            e.printStackTrace();
        }
    }
}
