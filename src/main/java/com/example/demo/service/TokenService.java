package com.example.demo.service;

import com.example.demo.model.ConfirmationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public interface TokenService {
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
    void saveConfirmationToken(ConfirmationToken token);
    Optional<ConfirmationToken> getConfirmationToken(String token);
    int setConfirmedAt(String token);
}
