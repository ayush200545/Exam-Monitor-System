package com.distributedexam.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            // Start RMI registry locally on standard port 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry started on port 1099.");

            // Instantiate managers
            com.distributedexam.server.exam.ExamManager examManager = new com.distributedexam.server.exam.ExamManager();
            com.distributedexam.server.room.RoomManager roomManager = new com.distributedexam.server.room.RoomManager();
            com.distributedexam.server.attendance.AttendanceManager attendanceManager = new com.distributedexam.server.attendance.AttendanceManager();
            com.distributedexam.server.submission.SubmissionManager submissionManager = new com.distributedexam.server.submission.SubmissionManager();
            com.distributedexam.server.student.StudentManager studentManager = new com.distributedexam.server.student.StudentManager();

            // Bind them to the registry
            Naming.rebind("ExamService", examManager);
            Naming.rebind("RoomService", roomManager);
            Naming.rebind("AttendanceService", attendanceManager);
            Naming.rebind("SubmissionService", submissionManager);
            Naming.rebind("StudentService", studentManager);

            System.out.println("All services bound in registry.");
            System.out.println("Distributed Examination Server is ready and listening.");
        } catch (Exception e) {
            System.err.println("RMI Server failed to start:");
            e.printStackTrace();
        }
    }
}
