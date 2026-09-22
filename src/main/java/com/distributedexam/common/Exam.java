package com.distributedexam.common;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Exam implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int examId;
    private String examName;
    private String subjectCode;
    private Date examDate;
    private Time startTime;
    private Time endTime;
    private int durationMinutes;
    private ExamStatus status;
    private Timestamp createdAt;

    public Exam() {}

    public Exam(int examId, String examName, String subjectCode, Date examDate, Time startTime, Time endTime, int durationMinutes, ExamStatus status, Timestamp createdAt) {
        this.examId = examId;
        this.examName = examName;
        this.subjectCode = subjectCode;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public Date getExamDate() { return examDate; }
    public void setExamDate(Date examDate) { this.examDate = examDate; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public ExamStatus getStatus() { return status; }
    public void setStatus(ExamStatus status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
