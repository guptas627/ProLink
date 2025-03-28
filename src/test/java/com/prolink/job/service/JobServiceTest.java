package com.prolink.job.service;

import com.prolink.job.model.Job;
import com.prolink.job.repository.JobRepository;
import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostJob_Success() {
        // Arrange
        String username = "testUser";
        String title = "Software Engineer";
        String description = "Develop software solutions";
        String location = "Remote";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Job job = jobService.postJob(username, title, description, location);

        // Assert
        assertNotNull(job);
        assertEquals(title, job.getTitle());
        assertEquals(description, job.getDescription());
        assertEquals(location, job.getLocation());
        assertEquals(user, job.getPostedBy());
        verify(userRepository, times(1)).findByUsername(username);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void testPostJob_UserNotFound() {
        // Arrange
        String username = "testUser";
        String title = "Software Engineer";
        String description = "Develop software solutions";
        String location = "Remote";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobService.postJob(username, title, description, location);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetJobsByUser() {
        // Arrange
        String username = "testUser";
        Job job = new Job();
        when(jobRepository.findByPostedByUsername(username)).thenReturn(Collections.singletonList(job));

        // Act
        List<Job> jobs = jobService.getJobsByUser(username);

        // Assert
        assertEquals(1, jobs.size());
        verify(jobRepository, times(1)).findByPostedByUsername(username);
    }

    @Test
    void testGetJobById_Success() {
        // Arrange
        Long jobId = 1L;
        Job job = new Job();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // Act
        Job result = jobService.getJobById(jobId);

        // Assert
        assertNotNull(result);
        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void testGetJobById_NotFound() {
        // Arrange
        Long jobId = 1L;
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobService.getJobById(jobId);
        });
        assertEquals("Job not found", exception.getMessage());
    }

    @Test
    void testGetAllJobs() {
        // Arrange
        Job job = new Job();
        when(jobRepository.findAll()).thenReturn(Collections.singletonList(job));

        // Act
        List<Job> jobs = jobService.getAllJobs();

        // Assert
        assertEquals(1, jobs.size());
        verify(jobRepository, times(1)).findAll();
    }
}