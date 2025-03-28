package com.prolink.security.controller;

import com.prolink.security.config.JwtUtil;
import com.prolink.user.model.Role;
import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;
import com.prolink.security.controller.AuthController.LoginRequest;
import com.prolink.security.controller.AuthController.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFullName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        registerRequest = new RegisterRequest();
        registerRequest.setFullname("Test User");
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegisterUserSuccess() {
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");

        ResponseEntity<String> response = authController.registerUser(registerRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User registered successfully", response.getBody());

        // Verify save method is called once
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUserUsernameAlreadyExists() {
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(user));

        ResponseEntity<String> response = authController.registerUser(registerRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    void testLoginUserSuccessForAdmin() {
        loginRequest.setUsername("Abhainn");
        loginRequest.setPassword("admin");

        when(jwtUtil.generateToken("Abhainn", "ADMIN")).thenReturn("mockToken");

        ResponseEntity<Map<String, String>> response = authController.loginUser(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("token"));
        assertEquals("Login successful", response.getBody().get("message"));
        assertEquals("ADMIN", response.getBody().get("role"));
        assertEquals("/admin", response.getBody().get("redirect"));
    }

    @Test
    void testLoginUserSuccessForRegularUser() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "USER")).thenReturn("mockToken");

        ResponseEntity<Map<String, String>> response = authController.loginUser(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("token"));
        assertEquals("Login successful", response.getBody().get("message"));
        assertEquals("USER", response.getBody().get("role"));
        assertEquals("/users", response.getBody().get("redirect"));
    }

    @Test
    void testLoginUserFailure() {
        loginRequest.setPassword("wrongPassword");

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        ResponseEntity<Map<String, String>> response = authController.loginUser(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Invalid username or password", response.getBody().get("error"));
    }
}
