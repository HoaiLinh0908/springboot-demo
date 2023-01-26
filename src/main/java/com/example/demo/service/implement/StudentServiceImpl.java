package com.example.demo.service.implement;

import com.example.demo.model.ConfirmationToken;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import com.example.demo.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudentServiceImpl implements StudentService, UserDetailsService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(username);
        if (!optionalStudent.isPresent()) {
            log.error("Student {} not found in the database!", username);
            throw new UsernameNotFoundException("Student not found in the database!");
        } else {
            log.info("Student found in the database: {}", username);
        }
        return optionalStudent.get();
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

    @Override
    public String registerStudent(Student student) {
        Optional<Student> optionalStudent = studentRepository.findStudentByEmail(student.getEmail());
        if (optionalStudent.isPresent()) {
            // TODO check of attributes are the same and
            // TODO if email not confirmed send confirmation email.
            throw new IllegalStateException(String.format("The email '%s' is taken!", student.getEmail()));
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        if (student.getRoles().isEmpty()) {
            student.setRoles(Collections.singletonList(roleRepository.findByName("Bad Student").get()));
        }
        studentRepository.save(student);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                student
        );

        tokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    public int enableStudent(String email) {
        return studentRepository.enableStudent(email);
    }
}
