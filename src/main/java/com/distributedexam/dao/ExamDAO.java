package com.distributedexam.dao;

import com.distributedexam.common.Exam;
import java.util.List;

public interface ExamDAO {
    void createExam(Exam exam) throws Exception;
    Exam getExam(int examId) throws Exception;
    List<Exam> getAllExams() throws Exception;
    void updateExam(Exam exam) throws Exception;
    void deleteExam(int examId) throws Exception;
}
