package com.distributedexam.remote;

import com.distributedexam.common.Submission;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SubmissionRemote extends Remote {
    boolean recordSubmission(int examId, int studentId, int roomId) throws RemoteException, Exception;
    Submission getSubmission(int examId, int studentId) throws RemoteException, Exception;
    List<Submission> getSubmissionsByExam(int examId) throws RemoteException, Exception;
    List<Submission> getSubmissionsByRoom(int examId, int roomId) throws RemoteException, Exception;
}
