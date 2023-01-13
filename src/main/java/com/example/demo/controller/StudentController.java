package com.example.demo.controller;

import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.service.RoleService;
import com.example.demo.service.StudentServiceImpl;
import com.example.demo.service.TokenService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api")
public class StudentController {
    private final StudentServiceImpl studentService;
    private final RoleService roleService;
    private final TokenService tokenService;

    @GetMapping("/student/students")
    public ResponseEntity<List<Student>> getStudents() {
        return ResponseEntity.ok().body(studentService.getStudents());
    }

    @PostMapping("/student/register")
    public ResponseEntity<Student> registerNewStudent(@RequestBody Student student) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/student/register").toUriString());
        return ResponseEntity.created(uri).body(studentService.addNewStudent(student));
    }

    @PostMapping("/student/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/student/role/save").toUriString());
        return ResponseEntity.created(uri).body(roleService.saveRole(role));
    }

    @PostMapping("/student/role/addtostudent")
    public ResponseEntity<?> addRoleToStudent(@RequestBody RoleToStudentForm form) {
        studentService.addRoleToStudent(form.getStudentEmail(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        tokenService.refreshToken(request, response);
    }

    @DeleteMapping(path = "/student/{studentId}")
    public void deleteStudent(@PathVariable("studentId") Long studentId) {
        studentService.deleteStudent(studentId);
    }

    @PutMapping(path = "/student/{studentId}")
    public void updateStudent(@PathVariable("studentId") Long studentId,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) String email) {
        studentService.updateStudent(studentId, name, email);
    }
}

@Data
class RoleToStudentForm {
    private String studentEmail;
    private String roleName;
}
