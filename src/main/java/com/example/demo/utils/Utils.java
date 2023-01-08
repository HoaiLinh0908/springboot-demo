package com.example.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class Utils {
    public static final Algorithm myAlgorithm = Algorithm.HMAC256("secret".getBytes());

    public static Algorithm getAlgorithm() {
        return myAlgorithm;
    }

    public static DecodedJWT getDeCodedJWT(String token) {
        JWTVerifier verifier = JWT.require(myAlgorithm).build();
        return verifier.verify(token);
    }

    public static String generateToken(String subject, String requestURL, List<?> claim) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(requestURL)
                .withClaim("roles", claim)
                .sign(Utils.getAlgorithm());
    }

    public static void responseError(HttpServletResponse response, String message) throws IOException {
        response.setHeader("error", message);
        response.setStatus(FORBIDDEN.value());
        Map<String, String> errors = new HashMap<>();
        errors.put("error_message", message);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), errors);
    }
}
