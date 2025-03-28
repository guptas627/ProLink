package com.prolink.job.service;

import com.prolink.job.model.Application;
import com.prolink.job.model.ApplicationStatus;
import com.prolink.job.model.Job;
import com.prolink.job.repository.ApplicationRepository;
import com.prolink.job.repository.JobRepository;
import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyForJob_Success() {
        // Arrange
        String username = "testUser";
        Long jobId = 1L;
        Application application = new Application();
        User user = new User();
        user.setUsername(username);
        Job job = new Job();
        job.setId(jobId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationRepository.save(application)).thenReturn(application);

        // Act
        Application savedApplication = applicationService.applyForJob(username, jobId, application);

        // Assert
        assertNotNull(savedApplication);
        assertEquals(ApplicationStatus.PENDING, savedApplication.getStatus());
        verify(userRepository, times(1)).findByUsername(username);
        verify(jobRepository, times(1)).findById(jobId);
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    void testApplyForJob_UserNotFound() {
        // Arrange
        String username = "testUser";
        Long jobId = 1L;
        Application application = new Application();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.applyForJob(username, jobId, application);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testApplyForJob_JobNotFound() {
        // Arrange
        String username = "testUser";
        Long jobId = 1L;
        Application application = new Application();
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.applyForJob(username, jobId, application);
        });
        assertEquals("Job not found", exception.getMessage());
    }

    @Test
    void testGetApplicationsByUser() {
        // Arrange
        String username = "testUser";
        Application application = new Application();
        when(applicationRepository.findByUserUsername(username)).thenReturn(Collections.singletonList(application));

        // Act
        List<Application> applications = applicationService.getApplicationsByUser(username);

        // Assert
        assertEquals(1, applications.size());
        verify(applicationRepository, times(1)).findByUserUsername(username);
    }

    @Test
    void testGetApplicationsByJob() {
        // Arrange
        Long jobId = 1L;
        Application application = new Application();
        when(applicationRepository.findByJobId(jobId)).thenReturn(Collections.singletonList(application));

        // Act
        List<Application> applications = applicationService.getApplicationsByJob(jobId);

        // Assert
        assertEquals(1, applications.size());
        verify(applicationRepository, times(1)).findByJobId(jobId);
    }

    @Test
    void testGetApplicationById_Success() {
        // Arrange
        Long applicationId = 1L;
        Application application = new Application();
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        // Act
        Application result = applicationService.getApplicationById(applicationId);

        // Assert
        assertNotNull(result);
        verify(applicationRepository, times(1)).findById(applicationId);
    }

    @Test
    void testGetApplicationById_NotFound() {
        // Arrange
        Long applicationId = 1L;
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.getApplicationById(applicationId);
        });
        assertEquals("Application not found", exception.getMessage());
    }

    @Test
    void testGetApplicationCountByJob() {
        // Arrange
        Long jobId = 1L;
        when(applicationRepository.countByJobId(jobId)).thenReturn(5);

        // Act
        int count = applicationService.getApplicationCountByJob(jobId);

        // Assert
        assertEquals(5, count);
        verify(applicationRepository, times(1)).countByJobId(jobId);
    }
}