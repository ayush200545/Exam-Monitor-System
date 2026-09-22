package com.distributedexam.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Submission implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int submissionId;
    private int examId;
    private int studentId;
    private int roomId;
    private String status; // NOT_SUBMITTED, SUBMITTED, LATE
    private Timestamp submittedAt;

    public Submission() {}

    public Submission(int submissionId, int examId, int studentId, int roomId, String status, Timestamp submittedAt) {
        this.submissionId = submissionId;
        this.examId = examId;
        this.studentId = studentId;
        this.roomId = roomId;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public int getSubmissionId() { return submissionId; }
    public void setSubmissionId(int submissionId) { this.submissionId = submissionId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
}
