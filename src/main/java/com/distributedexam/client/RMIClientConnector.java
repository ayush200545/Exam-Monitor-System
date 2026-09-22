package com.distributedexam.client;

import com.distributedexam.remote.AttendanceRemote;
import com.distributedexam.remote.ExamRemote;
import com.distributedexam.remote.HeartbeatRemote;
import com.distributedexam.remote.RoomRemote;
import com.distributedexam.remote.StudentRemote;
import com.distributedexam.remote.SubmissionRemote;

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
    }

    public ExamRemote       getExamService()       { return examService; }
    public RoomRemote       getRoomService()        { return roomService; }
    public StudentRemote    getStudentService()     { return studentService; }
    public AttendanceRemote getAttendanceService()  { return attendanceService; }
    public SubmissionRemote getSubmissionService()  { return submissionService; }
    public HeartbeatRemote  getHeartbeatService()   { return heartbeatService; }
}
