package com.prolink.user.service;

import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindUserById_NotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findUserById(userId);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindByUsername_Success() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_NotFound() {
        // Arrange
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername(username);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUserProfile_Success() {
        // Arrange
        String username = "testUser";
        User existingUser = new User();
        existingUser.setUsername(username);

        User updatedUser = new User();
        updatedUser.setFullName("John Doe");
        updatedUser.setBio("Software Engineer");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        User result = userService.updateUserProfile(username, updatedUser);

        // Assert
        assertEquals("John Doe", result.getFullName());
        assertEquals("Software Engineer", result.getBio());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        // Arrange
        String username = "testUser";
        User updatedUser = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserProfile(username, updatedUser);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testSaveUser() {
        // Arrange
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateProfilePicture_Success() {
        // Arrange
        String username = "testUser";
        String profilePictureUrl = "/uploads/profile_pictures/test.jpg";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.updateProfilePicture(username, profilePictureUrl);

        // Assert
        assertEquals(profilePictureUrl, result.getProfilePictureUrl());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateProfilePicture_UserNotFound() {
        // Arrange
        String username = "testUser";
        String profilePictureUrl = "/uploads/profile_pictures/test.jpg";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateProfilePicture(username, profilePictureUrl);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testSearchUsers() {
        // Arrange
        String query = "test";
        String username = "testUser";
        User user = new User();
        when(userRepository.searchUsersExcludingConnections(query, username)).thenReturn(List.of(user));

        // Act
        List<User> result = userService.searchUsers(username, query);

        // Assert
        assertEquals(1, result.size());
        verify(userRepository, times(1)).searchUsersExcludingConnections(query, username);
    }

    @Test
    void testGetPendingRequests_Success() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        User pendingUser = new User();
        user.getPendingRequests().add(pendingUser);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        List<User> result = userService.getPendingRequests(username);

        // Assert
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetPendingRequests_UserNotFound() {
        // Arrange
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getPendingRequests(username);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testGetConnections() {
        // Arrange
        String username = "testUser";
        User user = new User();
        when(userRepository.findConnections(username)).thenReturn(List.of(user));

        // Act
        List<User> result = userService.getConnections(username);

        // Assert
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findConnections(username);
    }

    @Test
    void testSendConnectionRequest_Success() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";
        User user = new User();
        user.setUsername(username);
        User connectUser = new User();
        connectUser.setUsername(connectUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(connectUsername)).thenReturn(Optional.of(connectUser));

        // Act
        String result = userService.sendConnectionRequest(username, connectUsername);

        // Assert
        assertEquals("Connection request sent!", result);
        assertTrue(connectUser.getPendingRequests().contains(user));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(connectUsername);
        verify(userRepository, times(1)).save(connectUser);
    }

    @Test
    void testAcceptConnectionRequest_Success() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";
        User user = new User();
        user.setUsername(username);
        User connectUser = new User();
        connectUser.setUsername(connectUsername);
        user.getPendingRequests().add(connectUser);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(connectUsername)).thenReturn(Optional.of(connectUser));

        // Act
        String result = userService.acceptConnectionRequest(username, connectUsername);

        // Assert
        assertEquals("Connection accepted!", result);
        assertFalse(user.getPendingRequests().contains(connectUser));
        assertTrue(user.getConnections().contains(connectUser));
        assertTrue(connectUser.getConnections().contains(user));
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(connectUsername);
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).save(connectUser);
    }

    @Test
    void testAcceptConnectionRequest_NoPendingRequest() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";
        User user = new User();
        user.setUsername(username);
        User connectUser = new User();
        connectUser.setUsername(connectUsername);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(connectUsername)).thenReturn(Optional.of(connectUser));

        // Act
        String result = userService.acceptConnectionRequest(username, connectUsername);

        // Assert
        assertEquals("No pending request from this user.", result);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByUsername(connectUsername);
    }

    @Test
    void testRejectConnectionRequest() {
        // Arrange
        String username = "testUser";
        String connectUsername = "connectUser";

        // Act
        String result = userService.rejectConnectionRequest(username, connectUsername);

        // Assert
        assertEquals("Request rejected!", result);
    }
}