package com.distributedexam.remote;

import com.distributedexam.common.Exam;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ExamRemote extends Remote {
    void createExam(Exam exam) throws RemoteException, Exception;
    Exam getExam(int examId) throws RemoteException, Exception;
    List<Exam> getAllExams() throws RemoteException, Exception;
    void updateExam(Exam exam) throws RemoteException, Exception;
    void deleteExam(int examId) throws RemoteException, Exception;
    void startExam(int examId) throws RemoteException, Exception;
    void endExam(int examId) throws RemoteException, Exception;
    void cancelExam(int examId) throws RemoteException, Exception;
    String getExamStatus(int examId) throws RemoteException, Exception;
}
