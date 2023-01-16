package com.example.demo.service;

import com.example.demo.model.Student;

public interface RegistrationService {
    String register(Student student);
    String confirmToken(String token);
}
