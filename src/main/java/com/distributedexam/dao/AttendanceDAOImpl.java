package com.distributedexam.dao;

import com.distributedexam.common.Attendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAOImpl implements AttendanceDAO {
    @Override
    public void createAttendance(Attendance attendance) throws Exception {
        String query = "INSERT INTO attendance (exam_id, student_id, room_id, status, marked_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, attendance.getExamId());
            stmt.setInt(2, attendance.getStudentId());
            stmt.setInt(3, attendance.getRoomId());
            stmt.setString(4, attendance.getStatus());
            stmt.setString(5, attendance.getMarkedBy());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    attendance.setAttendanceId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Attendance getAttendance(int examId, int studentId) throws Exception {
        String query = "SELECT * FROM attendance WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractAttendanceFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Attendance> getAttendanceByExam(int examId) throws Exception {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAttendanceFromResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Attendance> getAttendanceByRoom(int examId, int roomId) throws Exception {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE exam_id = ? AND room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAttendanceFromResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void updateAttendance(Attendance attendance) throws Exception {
        String query = "UPDATE attendance SET room_id = ?, status = ?, marked_at = CURRENT_TIMESTAMP, marked_by = ? WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, attendance.getRoomId());
            stmt.setString(2, attendance.getStatus());
            stmt.setString(3, attendance.getMarkedBy());
            stmt.setInt(4, attendance.getExamId());
            stmt.setInt(5, attendance.getStudentId());
            stmt.executeUpdate();
        }
    }

    private Attendance extractAttendanceFromResultSet(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("attendance_id"));
        attendance.setExamId(rs.getInt("exam_id"));
        attendance.setStudentId(rs.getInt("student_id"));
        attendance.setRoomId(rs.getInt("room_id"));
        attendance.setStatus(rs.getString("status"));
        attendance.setMarkedAt(rs.getTimestamp("marked_at"));
        attendance.setMarkedBy(rs.getString("marked_by"));
        return attendance;
    }
}
