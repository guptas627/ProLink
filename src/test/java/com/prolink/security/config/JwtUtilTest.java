package com.prolink.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Key key;

    @BeforeEach
    void setUp() {
        String secretKey = "YourVerySecretKeyForJWTGenerationMustBeLongEnough";
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateToken() {
        // Arrange
        String username = "testUser";
        String role = "ROLE_USER";

        // Act
        String token = jwtUtil.generateToken(username, role);

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);

        // Verify claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject());
        assertEquals(role, claims.get("role", String.class));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUserRole() {
        // Arrange
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedRole = jwtUtil.extractUserRole(token);

        // Assert
        assertEquals(role, extractedRole);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        boolean isValid = jwtUtil.validateToken(token, username);

        // Assert
        assertTrue(isValid);
    }


    @Test
    void testValidateToken_InvalidUsername() {
        // Arrange
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        boolean isValid = jwtUtil.validateToken(token, "invalidUser");

        // Assert
        assertFalse(isValid);
    }


    @Test
    void testIsTokenExpired_ValidToken() {
        // Arrange
        String username = "testUser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }
}