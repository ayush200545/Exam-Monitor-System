package com.distributedexam.dao;

import com.distributedexam.common.Submission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAOImpl implements SubmissionDAO {
    @Override
    public void createSubmission(Submission submission) throws Exception {
        String query = "INSERT INTO submissions (exam_id, student_id, room_id, status, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, submission.getExamId());
            stmt.setInt(2, submission.getStudentId());
            stmt.setInt(3, submission.getRoomId());
            stmt.setString(4, submission.getStatus());
            stmt.setTimestamp(5, submission.getSubmittedAt());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    submission.setSubmissionId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Submission getSubmission(int examId, int studentId) throws Exception {
        String query = "SELECT * FROM submissions WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractSubmissionFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Submission> getSubmissionsByExam(int examId) throws Exception {
        List<Submission> list = new ArrayList<>();
        String query = "SELECT * FROM submissions WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSubmissionFromResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Submission> getSubmissionsByRoom(int examId, int roomId) throws Exception {
        List<Submission> list = new ArrayList<>();
        String query = "SELECT * FROM submissions WHERE exam_id = ? AND room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examId);
            stmt.setInt(2, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSubmissionFromResultSet(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void updateSubmission(Submission submission) throws Exception {
        String query = "UPDATE submissions SET room_id = ?, status = ?, submitted_at = ? WHERE exam_id = ? AND student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, submission.getRoomId());
            stmt.setString(2, submission.getStatus());
            stmt.setTimestamp(3, submission.getSubmittedAt());
            stmt.setInt(4, submission.getExamId());
            stmt.setInt(5, submission.getStudentId());
            stmt.executeUpdate();
        }
    }

    private Submission extractSubmissionFromResultSet(ResultSet rs) throws SQLException {
        Submission submission = new Submission();
        submission.setSubmissionId(rs.getInt("submission_id"));
        submission.setExamId(rs.getInt("exam_id"));
        submission.setStudentId(rs.getInt("student_id"));
        submission.setRoomId(rs.getInt("room_id"));
        submission.setStatus(rs.getString("status"));
        submission.setSubmittedAt(rs.getTimestamp("submitted_at"));
        return submission;
    }
}
