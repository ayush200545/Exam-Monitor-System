package com.distributedexam.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Attendance implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int attendanceId;
    private int examId;
    private int studentId;
    private int roomId;
    private String status;
    private Timestamp markedAt;
    private String markedBy;

    public Attendance() {}

    public Attendance(int attendanceId, int examId, int studentId, int roomId, String status, Timestamp markedAt, String markedBy) {
        this.attendanceId = attendanceId;
        this.examId = examId;
        this.studentId = studentId;
        this.roomId = roomId;
        this.status = status;
        this.markedAt = markedAt;
        this.markedBy = markedBy;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getMarkedAt() { return markedAt; }
    public void setMarkedAt(Timestamp markedAt) { this.markedAt = markedAt; }

    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
}
