package com.distributedexam.remote;

import com.distributedexam.common.Student;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StudentRemote extends Remote {
    void createStudent(Student student) throws RemoteException, Exception;
    Student getStudent(int studentId) throws RemoteException, Exception;
    List<Student> getAllStudents() throws RemoteException, Exception;
    
    // Exam Assignment Methods
    void assignStudentToExam(int examId, int studentId, int roomId, String seatNumber) throws RemoteException, Exception;
    void unassignStudent(int examId, int studentId) throws RemoteException, Exception;
    Integer getStudentRoomForExam(int examId, int studentId) throws RemoteException, Exception;
    List<Integer> getStudentsInRoomForExam(int examId, int roomId) throws RemoteException, Exception;
}
