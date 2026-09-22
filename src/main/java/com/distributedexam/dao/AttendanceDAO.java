package com.distributedexam.dao;

import com.distributedexam.common.Attendance;
import java.util.List;

public interface AttendanceDAO {
    void createAttendance(Attendance attendance) throws Exception;
    Attendance getAttendance(int examId, int studentId) throws Exception;
    List<Attendance> getAttendanceByExam(int examId) throws Exception;
    List<Attendance> getAttendanceByRoom(int examId, int roomId) throws Exception;
    void updateAttendance(Attendance attendance) throws Exception;
}
