package com.distributedexam.client;

import com.distributedexam.remote.AttendanceRemote;
import com.distributedexam.remote.ExamRemote;
import com.distributedexam.remote.HeartbeatRemote;
import com.distributedexam.remote.RoomRemote;
import com.distributedexam.remote.StudentRemote;
import com.distributedexam.remote.SubmissionRemote;
import com.distributedexam.remote.AuthRemote;
import com.distributedexam.remote.MonitoringRemote;
import com.distributedexam.remote.DashboardRemote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * RMIClientConnector — looks up all remote stubs from the RMI Registry.
 * Each Room Client JVM holds one instance of this and uses it to communicate with the server.
 */
public class RMIClientConnector {

    private final String host;
    private final int port;
    private final String baseUrl;

    private ExamRemote       examService;
    private RoomRemote       roomService;
    private StudentRemote    studentService;
    private AttendanceRemote attendanceService;
    private SubmissionRemote submissionService;
    private HeartbeatRemote  heartbeatService;
    private AuthRemote authService;
    private MonitoringRemote monitoringService;
    private DashboardRemote dashboardService;

    public RMIClientConnector(String host, int port) {
        this.host    = host;
        this.port    = port;
        this.baseUrl = "rmi://" + host + ":" + port + "/";
    }

    /**
     * Looks up all remote services from the RMI Registry.
     * Throws RemoteException / NotBoundException on failure — handled by caller.
     */
    public void connect() throws RemoteException, NotBoundException, MalformedURLException {
        examService       = (ExamRemote)       Naming.lookup(baseUrl + "ExamService");
        roomService       = (RoomRemote)       Naming.lookup(baseUrl + "RoomService");
        studentService    = (StudentRemote)    Naming.lookup(baseUrl + "StudentService");
        attendanceService = (AttendanceRemote) Naming.lookup(baseUrl + "AttendanceService");
        submissionService = (SubmissionRemote) Naming.lookup(baseUrl + "SubmissionService");
        heartbeatService  = (HeartbeatRemote)  Naming.lookup(baseUrl + "HeartbeatService");
        // Member 4 services are optional so legacy clients can still connect to an
        // original server that exposes only the existing service bindings.
        try { authService = (AuthRemote) Naming.lookup(baseUrl + "AuthService"); } catch (NotBoundException ignored) { }
        try { monitoringService = (MonitoringRemote) Naming.lookup(baseUrl + "MonitoringService"); } catch (NotBoundException ignored) { }
        try { dashboardService = (DashboardRemote) Naming.lookup(baseUrl + "DashboardService"); } catch (NotBoundException ignored) { }
    }

    public ExamRemote       getExamService()       { return examService; }
    public RoomRemote       getRoomService()        { return roomService; }
    public StudentRemote    getStudentService()     { return studentService; }
    public AttendanceRemote getAttendanceService()  { return attendanceService; }
    public SubmissionRemote getSubmissionService()  { return submissionService; }
    public HeartbeatRemote  getHeartbeatService()   { return heartbeatService; }
    public AuthRemote getAuthService() { return authService; }
    public MonitoringRemote getMonitoringService() { return monitoringService; }
    public DashboardRemote getDashboardService() { return dashboardService; }
}
