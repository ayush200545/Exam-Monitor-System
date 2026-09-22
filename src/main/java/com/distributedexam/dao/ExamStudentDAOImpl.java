package com.distributedexam.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ExamStudentDAOImpl implements ExamStudentDAO {

    @Override
    public void assignStudentToExam(int examId, int studentId, int roomId, String seatNumber) throws Exception {
        String query = "INSERT INTO exam_students (exam_id, student_id, room_id, seat_number) VALUES (?, ?, ?, ?) " +
                       "ON CONFLICT (exam_id, student_id) DO UPDATE SET room_id = EXCLUDED.room_id, seat_number = EXCLUDED.seat_number";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, studentId);
            stmt.setInt(3, roomId);
            stmt.setString(4, seatNumber);
            stmt.executeUpdate();
        }
    }

    @Override
    public void unassignStudent(int examId, int studentId) throws Exception {
        String query = "DELETE FROM exam_students WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();
        }
    }

    @Override
    public Integer getStudentRoomForExam(int examId, int studentId) throws Exception {
        String query = "SELECT room_id FROM exam_students WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("room_id");
                }
            }
        }
        return null;
    }

    @Override
    public List<Integer> getStudentsInRoomForExam(int examId, int roomId) throws Exception {
        List<Integer> students = new ArrayList<>();
        String query = "SELECT student_id FROM exam_students WHERE exam_id = ? AND room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(rs.getInt("student_id"));
                }
            }
        }
        return students;
    }
}
