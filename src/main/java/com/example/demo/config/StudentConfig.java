package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.service.RoleService;
import com.example.demo.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(RoleService roleService, StudentService studentService) {
        return args -> {
            Student linhDo = new Student(1L,
                    "LinhDo",
                    "linh.do@gmail.com",
                    "hello123",
                    LocalDate.of(1999, Month.JULY, 30),
                    new ArrayList<>());

            Student huyenPham = new Student(2L,
                    "HuyenPham",
                    "huyen.pham@gmail.com",
                    "hello123",
                    LocalDate.of(1998, Month.AUGUST, 12),
                    new ArrayList<>());

            roleService.saveRole(new Role(1L, "Good Student"));
            roleService.saveRole(new Role(2L, "Bad Student"));

            studentService.addNewStudent(linhDo);
            studentService.addNewStudent(huyenPham);

            studentService.addRoleToStudent(linhDo.getEmail(), "Good Student");
            studentService.addRoleToStudent(huyenPham.getEmail(), "Bad Student");
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
