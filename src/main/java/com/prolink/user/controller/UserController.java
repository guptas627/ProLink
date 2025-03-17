package com.prolink.user.controller;

import com.prolink.user.model.User;
import com.prolink.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final String UPLOAD_DIR = "uploads/profile_pictures/";

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<User> getUserProfile(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile/update/{username}")
    public ResponseEntity<User> updateUserProfile(@PathVariable String username, @RequestBody Map<String, Object> updates) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Check and update only the provided fields
        updates.forEach((key, value) -> {
            switch (key) {
                case "fullName":
                    user.setFullName((String) value);
                    break;
                case "bio":
                    user.setBio((String) value);
                    break;
                case "location":
                    user.setLocation((String) value);
                    break;
                case "currentPosition":
                    user.setCurrentPosition((String) value);
                    break;
                case "education":
                    user.setEducation((List<String>)value);
                    break;
                case "workExperience":
                    user.setWorkExperience((List<String>) value);
                    break;
                case "skills":
                    user.setSkills((List<String>) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        User updatedUser = userService.saveUser(user); 
        return ResponseEntity.ok(updatedUser);
    }

    
    @PostMapping("/profile/upload/{username}")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable String username, @RequestParam("file") MultipartFile file) {
        try {
            //  Ensure upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            //  Generate unique file name to prevent overwrites
            String filename = username + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate public file URL
            String fileUrl = "/uploads/profile_pictures/" + filename;

            // Update user model with new profile picture URL
            User updatedUser = userService.updateProfilePicture(username, fileUrl);

            //  Return JSON response with the new image URL
            return ResponseEntity.ok(Map.of("profilePictureUrl", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query, @RequestParam String username) {
        List<User> users = userService.searchUsers(username, query);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/profile/{username}/pending-requests")
    public ResponseEntity<List<User>> getPendingRequests(@PathVariable String username) {
        return ResponseEntity.ok(userService.getPendingRequests(username));
    }
    @GetMapping("/profile/{username}/connections")
    public ResponseEntity<List<User>> getConnections(@PathVariable String username) {
        return ResponseEntity.ok(userService.getConnections(username));
    }
    @PostMapping("/profile/{username}/connect/{connectUsername}")
    public ResponseEntity<String> sendConnectionRequest(@PathVariable String username, @PathVariable String connectUsername) {
        return ResponseEntity.ok(userService.sendConnectionRequest(username, connectUsername));
    }
    @PostMapping("/profile/{username}/accept/{connectUsername}")
    public ResponseEntity<String> acceptConnectionRequest(@PathVariable String username, @PathVariable String connectUsername) {
        return ResponseEntity.ok(userService.acceptConnectionRequest(username, connectUsername));
    }

    @PostMapping("/profile/{username}/reject/{connectUsername}")
    public ResponseEntity<String> rejectConnectionRequest(@PathVariable String username, @PathVariable String connectUsername) {
        return ResponseEntity.ok(userService.rejectConnectionRequest(username, connectUsername));
    }

    



}
