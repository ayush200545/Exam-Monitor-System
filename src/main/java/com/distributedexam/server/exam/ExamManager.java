package com.distributedexam.server.exam;

import com.distributedexam.common.Exam;
import com.distributedexam.common.ExamStatus;
import com.distributedexam.dao.ExamDAO;
import com.distributedexam.dao.ExamDAOImpl;
import com.distributedexam.remote.ExamRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ExamManager extends UnicastRemoteObject implements ExamRemote {
    private ExamDAO examDAO;

    public ExamManager() throws RemoteException {
        super();
        this.examDAO = new ExamDAOImpl();
    }

    @Override
    public synchronized void createExam(Exam exam) throws Exception {
        exam.setStatus(ExamStatus.SCHEDULED);
        examDAO.createExam(exam);
    }

    @Override
    public Exam getExam(int examId) throws Exception {
        return examDAO.getExam(examId);
    }

    @Override
    public List<Exam> getAllExams() throws Exception {
        return examDAO.getAllExams();
    }

    @Override
    public synchronized void updateExam(Exam exam) throws Exception {
        examDAO.updateExam(exam);
    }

    @Override
    public synchronized void deleteExam(int examId) throws Exception {
        examDAO.deleteExam(examId);
    }

    @Override
    public synchronized void startExam(int examId) throws Exception {
        Exam exam = examDAO.getExam(examId);
        if (exam != null && exam.getStatus() == ExamStatus.SCHEDULED) {
            exam.setStatus(ExamStatus.RUNNING);
            examDAO.updateExam(exam);
        } else {
            throw new Exception("Exam cannot be started. Current status is not SCHEDULED.");
        }
    }

    @Override
    public synchronized void endExam(int examId) throws Exception {
        Exam exam = examDAO.getExam(examId);
        if (exam != null && exam.getStatus() == ExamStatus.RUNNING) {
            exam.setStatus(ExamStatus.COMPLETED);
            examDAO.updateExam(exam);
        } else {
            throw new Exception("Exam cannot be ended. Current status is not RUNNING.");
        }
    }

    @Override
    public synchronized void cancelExam(int examId) throws Exception {
        Exam exam = examDAO.getExam(examId);
        if (exam != null && (exam.getStatus() == ExamStatus.SCHEDULED || exam.getStatus() == ExamStatus.RUNNING)) {
            exam.setStatus(ExamStatus.CANCELLED);
            examDAO.updateExam(exam);
        } else {
            throw new Exception("Exam cannot be cancelled. Already completed or cancelled.");
        }
    }

    @Override
    public String getExamStatus(int examId) throws Exception {
        Exam exam = examDAO.getExam(examId);
        if (exam != null) {
            return exam.getStatus().name();
        }
        return "NOT_FOUND";
    }
}
