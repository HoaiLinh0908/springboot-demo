package com.example.demo.service.implement;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.model.ConfirmationToken;
import com.example.demo.model.Role;
import com.example.demo.model.Student;
import com.example.demo.repository.ConfirmationTokenRepository;
import com.example.demo.service.TokenService;
import com.example.demo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    private final StudentServiceImpl studentService;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    TokenServiceImpl(@Lazy StudentServiceImpl studentService, ConfirmationTokenRepository confirmationTokenRepository) {
        this.studentService = studentService;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Demo ")) {
            try {
                String refreshToken = authorizationHeader.substring("Demo ".length());
                DecodedJWT decodedJWT = Utils.getDeCodedJWT(refreshToken);
                String username = decodedJWT.getSubject();
                Student student = studentService.getStudent(username);

                String accessToken = Utils.generateToken(student.getEmail(),
                        request.getRequestURL().toString(),
                        student.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                tokens.put("refresh_token", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                Utils.responseError(response, e.getMessage());
            }
        } else {
            throw new RuntimeException("Refresh token is missing!");
        }
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Override
    public Optional<ConfirmationToken> getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
