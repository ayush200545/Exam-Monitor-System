package com.distributedexam.server.attendance;

import com.distributedexam.common.Attendance;
import com.distributedexam.common.Exam;
import com.distributedexam.common.ExamStatus;
import com.distributedexam.dao.AttendanceDAO;
import com.distributedexam.dao.AttendanceDAOImpl;
import com.distributedexam.dao.ExamDAO;
import com.distributedexam.dao.ExamDAOImpl;
import com.distributedexam.remote.AttendanceRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class AttendanceManager extends UnicastRemoteObject implements AttendanceRemote {
    private AttendanceDAO attendanceDAO;
    private ExamDAO examDAO;

    public AttendanceManager() throws RemoteException {
        super();
        this.attendanceDAO = new AttendanceDAOImpl();
        this.examDAO = new ExamDAOImpl();
    }

    @Override
    public synchronized boolean markAttendance(int examId, int studentId, int roomId, String status, String markedBy) throws Exception {
        Exam exam = examDAO.getExam(examId);
        if (exam == null || exam.getStatus() != ExamStatus.RUNNING) {
            throw new Exception("Cannot mark attendance. Exam is not RUNNING.");
        }

        Attendance existing = attendanceDAO.getAttendance(examId, studentId);
        if (existing != null) {
            existing.setRoomId(roomId);
            existing.setStatus(status);
            existing.setMarkedBy(markedBy);
            attendanceDAO.updateAttendance(existing);
            return true;
        } else {
            Attendance att = new Attendance();
            att.setExamId(examId);
            att.setStudentId(studentId);
            att.setRoomId(roomId);
            att.setStatus(status);
            att.setMarkedBy(markedBy);
            attendanceDAO.createAttendance(att);
            return true;
        }
    }

    @Override
    public Attendance getAttendance(int examId, int studentId) throws Exception {
        return attendanceDAO.getAttendance(examId, studentId);
    }

    @Override
    public List<Attendance> getAttendanceByExam(int examId) throws Exception {
        return attendanceDAO.getAttendanceByExam(examId);
    }

    @Override
    public List<Attendance> getAttendanceByRoom(int examId, int roomId) throws Exception {
        return attendanceDAO.getAttendanceByRoom(examId, roomId);
    }
}
