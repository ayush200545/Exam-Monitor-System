package com.distributedexam.dao;

import com.distributedexam.common.Submission;
import java.util.List;

public interface SubmissionDAO {
    void createSubmission(Submission submission) throws Exception;
    Submission getSubmission(int examId, int studentId) throws Exception;
    List<Submission> getSubmissionsByExam(int examId) throws Exception;
    List<Submission> getSubmissionsByRoom(int examId, int roomId) throws Exception;
    void updateSubmission(Submission submission) throws Exception;
}
