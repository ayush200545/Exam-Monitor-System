package com.distributedexam.dao;

import java.util.List;

public interface ExamStudentDAO {
    void assignStudentToExam(int examId, int studentId, int roomId, String seatNumber) throws Exception;
    void unassignStudent(int examId, int studentId) throws Exception;
    Integer getStudentRoomForExam(int examId, int studentId) throws Exception;
    List<Integer> getStudentsInRoomForExam(int examId, int roomId) throws Exception;
}
