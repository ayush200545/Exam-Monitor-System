package com.distributedexam.remote;

import com.distributedexam.common.Attendance;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AttendanceRemote extends Remote {
    boolean markAttendance(int examId, int studentId, int roomId, String status, String markedBy) throws RemoteException, Exception;
    Attendance getAttendance(int examId, int studentId) throws RemoteException, Exception;
    List<Attendance> getAttendanceByExam(int examId) throws RemoteException, Exception;
    List<Attendance> getAttendanceByRoom(int examId, int roomId) throws RemoteException, Exception;
}
