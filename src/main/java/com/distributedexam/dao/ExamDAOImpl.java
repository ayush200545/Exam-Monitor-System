package com.distributedexam.dao;

import com.distributedexam.common.Exam;
import com.distributedexam.common.ExamStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExamDAOImpl implements ExamDAO {
    @Override
    public void createExam(Exam exam) throws Exception {
        String query = "INSERT INTO exams (exam_name, subject_code, exam_date, start_time, end_time, duration_minutes, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exam.getExamName());
            stmt.setString(2, exam.getSubjectCode());
            stmt.setDate(3, exam.getExamDate());
            stmt.setTime(4, exam.getStartTime());
            stmt.setTime(5, exam.getEndTime());
            stmt.setInt(6, exam.getDurationMinutes());
            stmt.setString(7, exam.getStatus().name());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    exam.setExamId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Exam getExam(int examId) throws Exception {
        String query = "SELECT * FROM exams WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractExamFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Exam> getAllExams() throws Exception {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT * FROM exams";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) exams.add(extractExamFromResultSet(rs));
        }
        return exams;
    }

    @Override
    public void updateExam(Exam exam) throws Exception {
        String query = "UPDATE exams SET exam_name = ?, subject_code = ?, exam_date = ?, start_time = ?, end_time = ?, duration_minutes = ?, status = ? WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, exam.getExamName());
            stmt.setString(2, exam.getSubjectCode());
            stmt.setDate(3, exam.getExamDate());
            stmt.setTime(4, exam.getStartTime());
            stmt.setTime(5, exam.getEndTime());
            stmt.setInt(6, exam.getDurationMinutes());
            stmt.setString(7, exam.getStatus().name());
            stmt.setInt(8, exam.getExamId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteExam(int examId) throws Exception {
        String query = "DELETE FROM exams WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.executeUpdate();
        }
    }
    
    private Exam extractExamFromResultSet(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setExamName(rs.getString("exam_name"));
        exam.setSubjectCode(rs.getString("subject_code"));
        exam.setExamDate(rs.getDate("exam_date"));
        exam.setStartTime(rs.getTime("start_time"));
        exam.setEndTime(rs.getTime("end_time"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setStatus(ExamStatus.valueOf(rs.getString("status")));
        exam.setCreatedAt(rs.getTimestamp("created_at"));
        return exam;
    }
}
