package com.prolink.user.controller;

import com.prolink.user.model.User;
import com.prolink.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userService.findUserById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<Optional<User>> response = userController.getUserById(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isPresent());
        assertEquals(userId, response.getBody().get().getId());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        Long userId = 1L;
        when(userService.findUserById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Optional<User>> response = userController.getUserById(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user = new User();
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserProfile_Success() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = userController.getUserProfile(username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(username, response.getBody().getUsername());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void testGetUserProfile_NotFound() {
        // Arrange
        String username = "testUser";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserProfile(username);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void testUpdateUserProfile_Success() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        Map<String, Object> updates = Map.of(
                "fullName", "John Doe",
                "bio", "Software Engineer"
        );

        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(userService.saveUser(user)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.updateUserProfile(username, updates);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John Doe", response.getBody().getFullName());
        assertEquals("Software Engineer", response.getBody().getBio());
        verify(userService, times(1)).findByUsername(username);
        verify(userService, times(1)).saveUser(user);
    }

    @Test
    void testUpdateUserProfile_InvalidField() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        Map<String, Object> updates = Map.of("invalidField", "value");

        when(userService.findByUsername(username)).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userController.updateUserProfile(username, updates);
        });
        assertEquals("Invalid field: invalidField", exception.getMessage());
    }


    

    @Test
    void testSearchUsers() {
        // Arrange
        String query = "test";
        String username = "testUser";
        User user = new User();
        when(userService.searchUsers(username, query)).thenReturn(Collections.singletonList(user));

        // Act
        ResponseEntity<List<User>> response = userController.searchUsers(query, username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).searchUsers(username, query);
    }

    

    @Test
    void testGetConnections() {
        // Arrange
        String username = "testUser";
        User user = new User();
        when(userService.getConnections(username)).thenReturn(Collections.singletonList(user));

        // Act
        ResponseEntity<List<User>> response = userController.getConnections(username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getConnections(username);
    }

    @Test
    void testSendConnectionRequest() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";
        when(userService.sendConnectionRequest(username, connectUsername)).thenReturn("Connection request sent");

        // Act
        ResponseEntity<String> response = userController.sendConnectionRequest(username, connectUsername);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Connection request sent", response.getBody());
        verify(userService, times(1)).sendConnectionRequest(username, connectUsername);
    }

    @Test
    void testAcceptConnectionRequest() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";
        when(userService.acceptConnectionRequest(username, connectUsername)).thenReturn("Connection request accepted");

        // Act
        ResponseEntity<String> response = userController.acceptConnectionRequest(username, connectUsername);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Connection request accepted", response.getBody());
        verify(userService, times(1)).acceptConnectionRequest(username, connectUsername);
    }

    @Test
    void testRejectConnectionRequest() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";
        when(userService.rejectConnectionRequest(username, connectUsername)).thenReturn("Connection request rejected");

        // Act
        ResponseEntity<String> response = userController.rejectConnectionRequest(username, connectUsername);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Connection request rejected", response.getBody());
        verify(userService, times(1)).rejectConnectionRequest(username, connectUsername);
    }
}