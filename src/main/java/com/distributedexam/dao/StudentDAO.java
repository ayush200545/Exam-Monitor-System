package com.distributedexam.dao;

import com.distributedexam.common.Student;
import java.util.List;

public interface StudentDAO {
    void createStudent(Student student) throws Exception;
    Student getStudent(int studentId) throws Exception;
    List<Student> getAllStudents() throws Exception;
    void updateStudent(Student student) throws Exception;
    void deleteStudent(int studentId) throws Exception;
}
