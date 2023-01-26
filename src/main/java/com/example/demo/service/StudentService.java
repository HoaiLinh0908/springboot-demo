package com.example.demo.service;

import com.example.demo.model.Student;

import java.util.List;

public interface StudentService {
    List<Student> getStudents();
    Student getStudent(String email);
    Student addNewStudent(Student student);
    String registerStudent(Student student);
    void deleteStudent(Long studentId);
    void updateStudent(Long studentId, String name, String email);
    void addRoleToStudent(String email, String role);
    int enableStudent(String email);
}
