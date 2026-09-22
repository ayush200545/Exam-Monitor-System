package com.distributedexam.server.student;

import com.distributedexam.common.Student;
import com.distributedexam.dao.ExamStudentDAO;
import com.distributedexam.dao.ExamStudentDAOImpl;
import com.distributedexam.dao.StudentDAO;
import com.distributedexam.dao.StudentDAOImpl;
import com.distributedexam.remote.StudentRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class StudentManager extends UnicastRemoteObject implements StudentRemote {
    private StudentDAO studentDAO;
    private ExamStudentDAO examStudentDAO;

    public StudentManager() throws RemoteException {
        super();
        this.studentDAO = new StudentDAOImpl();
        this.examStudentDAO = new ExamStudentDAOImpl();
    }

    public synchronized void updateStudent(Student student) throws Exception {
        studentDAO.updateStudent(student);
    }

    public synchronized void deleteStudent(int studentId) throws Exception {
        studentDAO.deleteStudent(studentId);
    }

    @Override
    public synchronized void createStudent(Student student) throws Exception {
        studentDAO.createStudent(student);
    }

    @Override
    public Student getStudent(int studentId) throws Exception {
        return studentDAO.getStudent(studentId);
    }

    @Override
    public List<Student> getAllStudents() throws Exception {
        return studentDAO.getAllStudents();
    }

    @Override
    public synchronized void assignStudentToExam(int examId, int studentId, int roomId, String seatNumber) throws Exception {
        examStudentDAO.assignStudentToExam(examId, studentId, roomId, seatNumber);
    }

    @Override
    public synchronized void unassignStudent(int examId, int studentId) throws Exception {
        examStudentDAO.unassignStudent(examId, studentId);
    }

    @Override
    public Integer getStudentRoomForExam(int examId, int studentId) throws Exception {
        return examStudentDAO.getStudentRoomForExam(examId, studentId);
    }

    @Override
    public List<Integer> getStudentsInRoomForExam(int examId, int roomId) throws Exception {
        return examStudentDAO.getStudentsInRoomForExam(examId, roomId);
    }
}
