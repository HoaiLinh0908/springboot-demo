package com.example.demo.service;

import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudentServiceImpl implements StudentService, UserDetailsService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(username);
        if (!optionalStudent.isPresent()) {
            log.error("Student {} not found in the database!", username);
            throw new UsernameNotFoundException("Student not found in the database!");
        } else {
            log.info("Student found in the database: {}", username);
        }
        Student student = optionalStudent.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        student.getRoles().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getName())));
        return new User(student.getEmail(), student.getPassword(), authorities);
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudent(String email) {
        return studentRepository.findStudentByEmail(email).orElseThrow(IllegalStateException::new);
    }

    public Student addNewStudent(Student student) {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(student.getEmail());
        if (optionalStudent.isPresent()) {
            throw new IllegalStateException(String.format("The email '%s' is taken!", student.getEmail()));
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalStateException(String.format("The id '%s' is not existed!", studentId));
        }
        studentRepository.deleteById(studentId);
    }

    public void updateStudent(Long studentId, String name, String email) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException(String.format("Student with id '%s' does not exist!")));
        if (name != null && !student.getName().equals(name)) {
            student.setName(name);
        }

        if (email != null && !student.getEmail().equals(email)) {
            Optional<Student> optionalStudent = studentRepository.findStudentByEmail(email);
            if (optionalStudent.isPresent()) {
                throw new IllegalStateException(String.format("The email '%s' is taken!", student.getEmail()));
            }
            student.setEmail(email);
        }
    }

    @Override
    public void addRoleToStudent(String email, String role) {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(email);
        if (!optionalStudent.isPresent()) {
            throw new IllegalStateException(String.format("Student with email '%s' does not exist!", email));
        }
        Optional<Role> optionalRole = roleRepository.findByName(role);
        if (!optionalRole.isPresent()) {
            throw new IllegalStateException(String.format("Role '%s' does not exist!", email));
        }
        optionalStudent.get().getRoles().add(optionalRole.get());
    }
}
