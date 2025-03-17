package com.prolink.user.service;

import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserProfile(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(updatedUser.getFullName());
        user.setBio(updatedUser.getBio());
        user.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        user.setLocation(updatedUser.getLocation());
        user.setCurrentPosition(updatedUser.getCurrentPosition());
        user.setEducation(updatedUser.getEducation());
        user.setWorkExperience(updatedUser.getWorkExperience());
        user.setSkills(updatedUser.getSkills());

        return userRepository.save(user);
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateProfilePicture(String username, String profilePictureUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Store new image URL in database
        user.setProfilePictureUrl(profilePictureUrl);
        return userRepository.save(user);
    }
    public List<User> searchUsers(String username, String query) {
        return userRepository.searchUsersExcludingConnections(query, username);
    }

    public List<User> getPendingRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getPendingRequests());
    }


    public List<User> getConnections(String username) {
        return userRepository.findConnections(username);
    }

    public String sendConnectionRequest(String username, String connectUsername) {
        User user = userRepository.findByUsername(username).orElseThrow();
        User connectUser = userRepository.findByUsername(connectUsername).orElseThrow();

        //  Ensure request is pending before saving
        if (!connectUser.getPendingRequests().contains(user)) {
            connectUser.getPendingRequests().add(user);
            userRepository.save(connectUser);
        }
        return "Connection request sent!";
    }

    public String acceptConnectionRequest(String username, String connectUsername) {
        User user = userRepository.findByUsername(username).orElseThrow();
        User connectUser = userRepository.findByUsername(connectUsername).orElseThrow();

        if (user.getPendingRequests().contains(connectUser)) {
            user.getPendingRequests().remove(connectUser); 
            user.getConnections().add(connectUser);
            connectUser.getConnections().add(user);
            userRepository.save(user);
            userRepository.save(connectUser);
            return "Connection accepted!";
        }
        return "No pending request from this user.";
    }


    public String rejectConnectionRequest(String username, String connectUsername) {
        return "Request rejected!";
    }

}
