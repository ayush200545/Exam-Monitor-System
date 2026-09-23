package com.distributedexam.server.dashboard;

import com.distributedexam.common.*;
import com.distributedexam.dao.*;
import com.distributedexam.remote.DashboardRemote;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardManager extends UnicastRemoteObject implements DashboardRemote {
    private final RoomDAO rooms = new RoomDAOImpl();
    private final ExamDAO exams = new ExamDAOImpl();
    private final AttendanceDAO attendance = new AttendanceDAOImpl();
    private final SubmissionDAO submissions = new SubmissionDAOImpl();
    private final IncidentDAO incidents = new IncidentDAOImpl();
    private final EventDAO events = new EventDAOImpl();

    public DashboardManager() throws RemoteException {}

    public DashboardSnapshot getCoordinatorSnapshot() throws Exception { return snapshot(null); }
    public DashboardSnapshot getRoomSnapshot(int id) throws Exception { return snapshot(id); }

    @Override
    public RoomDetailSnapshot getRoomDetails(int roomId) throws Exception {
        Room room = rooms.getRoom(roomId);
        if (room == null) {
            return null;
        }

        RoomDetailSnapshot detail = new RoomDetailSnapshot();
        detail.setRoomId(room.getRoomId());
        detail.setRoomName(room.getRoomName());
        detail.setStatus(room.getStatus() == null ? "UNKNOWN" : room.getStatus().name());
        detail.setLocation(room.getLocation());
        detail.setCapacity(room.getCapacity());
        detail.setCurrentExamId(room.getCurrentExamId());

        Integer examId = room.getCurrentExamId();
        if (examId != null) {
            Exam exam = exams.getExam(examId);
            if (exam != null) {
                detail.setCurrentExamName(exam.getExamName());
                detail.setSubjectCode(exam.getSubjectCode());
                detail.setExamStatus(exam.getStatus() == null ? "UNKNOWN" : exam.getStatus().name());
            }

            List<Integer> assignedStudents = new ExamStudentDAOImpl().getStudentsInRoomForExam(examId, roomId);
            detail.setAssignedStudentCount(assignedStudents.size());

            List<Attendance> attendanceForRoom = attendance.getAttendanceByRoom(examId, roomId);
            long present = attendanceForRoom.stream().filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus())).count();
            long absent = Math.max(0L, detail.getAssignedStudentCount() - present);
            detail.setAttendancePresent((int) present);
            detail.setAttendanceAbsent((int) absent);
            detail.setAttendancePercent(detail.getAssignedStudentCount() == 0 ? 0 : roundPct(present, detail.getAssignedStudentCount()));

            List<Submission> submissionsForRoom = submissions.getSubmissionsByRoom(examId, roomId);
            long submitted = submissionsForRoom.stream().filter(s -> "SUBMITTED".equalsIgnoreCase(s.getStatus())).count();
            long late = submissionsForRoom.stream().filter(s -> "LATE".equalsIgnoreCase(s.getStatus())).count();
            long notSubmitted = Math.max(0L, detail.getAssignedStudentCount() - submitted - late);
            detail.setSubmissionSubmitted((int) submitted);
            detail.setSubmissionLate((int) late);
            detail.setSubmissionNotSubmitted((int) notSubmitted);
            detail.setSubmissionPercent(detail.getAssignedStudentCount() == 0 ? 0 : roundPct(submitted + late, detail.getAssignedStudentCount()));
        }

        List<Incident> roomIncidents = incidents.findByRoom(roomId);
        List<Incident> openIncidents = roomIncidents.stream().filter(i -> i != null && !"RESOLVED".equalsIgnoreCase(i.getStatus())).collect(Collectors.toList());
        detail.setOpenIncidents(openIncidents.size());
        detail.setRecentIncidents(roomIncidents.stream().limit(10).collect(Collectors.toList()));
        detail.setRecentEvents(events.findByRoom(roomId).stream().limit(20).collect(Collectors.toList()));

        return detail;
    }

    private DashboardSnapshot snapshot(Integer target) throws Exception {
        DashboardSnapshot d = new DashboardSnapshot();
        List<Room> rs = rooms.getAllRooms();
        if (target != null) {
            rs.removeIf(r -> r.getRoomId() != target);
        }
        d.rooms.addAll(rs);
        d.totalRooms = rs.size();

        for (Room r : rs) {
            d.roomStatusSummary.merge(r.getStatus().name(), 1, Integer::sum);
            if (r.getStatus() == RoomStatus.OFFLINE) {
                d.offlineRooms++;
            } else {
                d.onlineRooms++;
            }

            if (r.getCurrentExamId() != null) {
                Exam e = exams.getExam(r.getCurrentExamId());
                if (e != null && e.getStatus() == ExamStatus.RUNNING) {
                    d.runningExams++;
                }
                List<Attendance> a = attendance.getAttendanceByRoom(r.getCurrentExamId(), r.getRoomId());
                List<Submission> s = submissions.getSubmissionsByRoom(r.getCurrentExamId(), r.getRoomId());
                d.attendanceCount += a.size();
                d.submissionCount += s.size();
                int n = new ExamStudentDAOImpl().getStudentsInRoomForExam(r.getCurrentExamId(), r.getRoomId()).size();
                d.attendancePercentByRoom.put(r.getRoomId(), pct(a.stream().filter(x -> "PRESENT".equalsIgnoreCase(x.getStatus())).count(), n));
                d.submissionProgressByRoom.put(r.getRoomId(), pct(s.stream().filter(x -> "SUBMITTED".equalsIgnoreCase(x.getStatus()) || "LATE".equalsIgnoreCase(x.getStatus())).count(), n));
            }
        }

        for (Exam e : exams.getAllExams()) {
            d.examCompletionStatus.put(e.getExamId(), e.getStatus().name());
        }

        List<Incident> open = incidents.findOpen();
        if (target != null) {
            open.removeIf(i -> !target.equals(i.getRoomId()));
        }
        d.openIncidents = open.size();
        for (Incident i : open) {
            d.incidentSummary.merge(i.getType(), 1, Integer::sum);
        }
        d.recentEvents = target == null ? events.findRecent(20) : events.findByRoom(target);
        return d;
    }

    private double pct(long a, long b) { return b == 0 ? 0 : roundPct(a, b); }
    private double roundPct(long numerator, long denominator) {
        return Math.round(numerator * 10000d / denominator) / 100d;
    }
}
