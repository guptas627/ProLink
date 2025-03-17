package com.prolink.security.controller;

import com.prolink.security.config.JwtUtil;
import com.prolink.user.model.Role;
import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        User newUser = new User();
        newUser.setFullName(request.getFullname());
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(Role.USER); 

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest request) {
        Map<String, String> response = new HashMap<>();

        // Hardcoded admin check
        if ("Abhainn".equals(request.getUsername()) && "admin".equals(request.getPassword())) {
            String token = jwtUtil.generateToken("Abhainn", "ADMIN"); // Generate token with ADMIN role

            response.put("message", "Login successful");
            response.put("role", "ADMIN");
            response.put("token", token);
            response.put("redirect", "/admin");

            return ResponseEntity.ok(response);
        }

        // Regular user login logic
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            String role = userOpt.get().getRole().name();
            String token = jwtUtil.generateToken(userOpt.get().getUsername(), role);

            response.put("message", "Login successful");
            response.put("role", role);
            response.put("token", token);
            response.put("redirect", role.equals("ADMIN") ? "/admin" : "/users");

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }


    // DTO classes for requests
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String fullname;
        private String username;
        private String email;
        private String password;
        public String getFullname() { return fullname; }
        public void setFullname(String fullname) { this.fullname = fullname; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
