package com.distributedexam.server.submission;

import com.distributedexam.common.Exam;
import com.distributedexam.common.ExamStatus;
import com.distributedexam.common.Submission;
import com.distributedexam.dao.ExamDAO;
import com.distributedexam.dao.ExamDAOImpl;
import com.distributedexam.dao.SubmissionDAO;
import com.distributedexam.dao.SubmissionDAOImpl;
import com.distributedexam.remote.SubmissionRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.List;

public class SubmissionManager extends UnicastRemoteObject implements SubmissionRemote {
    private SubmissionDAO submissionDAO;
    private ExamDAO examDAO;

    public SubmissionManager() throws RemoteException {
        super();
        this.submissionDAO = new SubmissionDAOImpl();
        this.examDAO = new ExamDAOImpl();
    }

    @Override
    public synchronized boolean recordSubmission(int examId, int studentId, int roomId) throws Exception {
        Exam exam = examDAO.getExam(examId);
        if (exam == null) {
            throw new Exception("Exam not found.");
        }

        String subStatus = "SUBMITTED";
        if (exam.getStatus() == ExamStatus.COMPLETED) {
            subStatus = "LATE";
        } else if (exam.getStatus() != ExamStatus.RUNNING) {
            throw new Exception("Cannot record submission. Exam is not active.");
        }

        Submission existing = submissionDAO.getSubmission(examId, studentId);
        if (existing != null) {
            existing.setStatus(subStatus);
            existing.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
            existing.setRoomId(roomId);
            submissionDAO.updateSubmission(existing);
            return true;
        } else {
            Submission sub = new Submission();
            sub.setExamId(examId);
            sub.setStudentId(studentId);
            sub.setRoomId(roomId);
            sub.setStatus(subStatus);
            sub.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
            submissionDAO.createSubmission(sub);
            return true;
        }
    }

    @Override
    public Submission getSubmission(int examId, int studentId) throws Exception {
        return submissionDAO.getSubmission(examId, studentId);
    }

    @Override
    public List<Submission> getSubmissionsByExam(int examId) throws Exception {
        return submissionDAO.getSubmissionsByExam(examId);
    }

    @Override
    public List<Submission> getSubmissionsByRoom(int examId, int roomId) throws Exception {
        return submissionDAO.getSubmissionsByRoom(examId, roomId);
    }
}
