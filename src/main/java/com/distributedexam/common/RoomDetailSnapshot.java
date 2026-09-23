package com.distributedexam.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomDetailSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomId;
    private String roomName;
    private String status;
    private String location;
    private int capacity;
    private Integer currentExamId;
    private String currentExamName;
    private String subjectCode;
    private String examStatus;
    private int assignedStudentCount;
    private int attendancePresent;
    private int attendanceAbsent;
    private double attendancePercent;
    private int submissionSubmitted;
    private int submissionLate;
    private int submissionNotSubmitted;
    private double submissionPercent;
    private int openIncidents;
    private List<Incident> recentIncidents = new ArrayList<>();
    private List<ExamEvent> recentEvents = new ArrayList<>();

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Integer getCurrentExamId() { return currentExamId; }
    public void setCurrentExamId(Integer currentExamId) { this.currentExamId = currentExamId; }

    public String getCurrentExamName() { return currentExamName; }
    public void setCurrentExamName(String currentExamName) { this.currentExamName = currentExamName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getExamStatus() { return examStatus; }
    public void setExamStatus(String examStatus) { this.examStatus = examStatus; }

    public int getAssignedStudentCount() { return assignedStudentCount; }
    public void setAssignedStudentCount(int assignedStudentCount) { this.assignedStudentCount = assignedStudentCount; }

    public int getAttendancePresent() { return attendancePresent; }
    public void setAttendancePresent(int attendancePresent) { this.attendancePresent = attendancePresent; }

    public int getAttendanceAbsent() { return attendanceAbsent; }
    public void setAttendanceAbsent(int attendanceAbsent) { this.attendanceAbsent = attendanceAbsent; }

    public double getAttendancePercent() { return attendancePercent; }
    public void setAttendancePercent(double attendancePercent) { this.attendancePercent = attendancePercent; }

    public int getSubmissionSubmitted() { return submissionSubmitted; }
    public void setSubmissionSubmitted(int submissionSubmitted) { this.submissionSubmitted = submissionSubmitted; }

    public int getSubmissionLate() { return submissionLate; }
    public void setSubmissionLate(int submissionLate) { this.submissionLate = submissionLate; }

    public int getSubmissionNotSubmitted() { return submissionNotSubmitted; }
    public void setSubmissionNotSubmitted(int submissionNotSubmitted) { this.submissionNotSubmitted = submissionNotSubmitted; }

    public double getSubmissionPercent() { return submissionPercent; }
    public void setSubmissionPercent(double submissionPercent) { this.submissionPercent = submissionPercent; }

    public int getOpenIncidents() { return openIncidents; }
    public void setOpenIncidents(int openIncidents) { this.openIncidents = openIncidents; }

    public List<Incident> getRecentIncidents() { return recentIncidents; }
    public void setRecentIncidents(List<Incident> recentIncidents) { this.recentIncidents = recentIncidents; }

    public List<ExamEvent> getRecentEvents() { return recentEvents; }
    public void setRecentEvents(List<ExamEvent> recentEvents) { this.recentEvents = recentEvents; }
}
