package com.prolink.job.controller;

import com.prolink.job.model.Application;
import com.prolink.job.model.Job;
import com.prolink.user.model.User;
import com.prolink.job.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyForJob() {
        // Arrange
        String username = "testUser";
        Long jobId = 1L;
        Application application = new Application();
        application.setId(1L); // Set a valid ID
        application.setUser(new User(jobId, username, username, username, username, username, username, username, username, null, null, null, null, null, null, null, null)); // Set a valid User
        application.setJob(new Job(jobId, username, username, username, null, null, null)); // Set a valid Job

        // Mock the service to return a valid Application
        when(applicationService.applyForJob(username, jobId, application)).thenReturn(application);

        // Act
        ResponseEntity<?> response = applicationController.applyForJob(username, jobId, application);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Application submitted successfully!", response.getBody());
        verify(applicationService, times(1)).applyForJob(username, jobId, application);
    }

    @Test
    void testGetApplicationsByJob() {
        // Arrange
        Long jobId = 1L;
        Application application = createApplicationWithJobAndUser(); // Create a valid Application object
        List<Application> applications = Collections.singletonList(application);
        when(applicationService.getApplicationsByJob(jobId)).thenReturn(applications);

        // Act
        ResponseEntity<CollectionModel<EntityModel<Application>>> response = applicationController.getApplicationsByJob(jobId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(applicationService, times(1)).getApplicationsByJob(jobId);
    }

    @Test
    void testGetApplicationsByUser() {
        // Arrange
        String username = "testUser";
        Application application = createApplicationWithJobAndUser(); // Create a valid Application object
        List<Application> applications = Collections.singletonList(application);
        when(applicationService.getApplicationsByUser(username)).thenReturn(applications);

        // Act
        ResponseEntity<CollectionModel<EntityModel<Application>>> response = applicationController.getApplicationsByUser(username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(applicationService, times(1)).getApplicationsByUser(username);
    }

    @Test
    void testGetApplicationById() {
        // Arrange
        Long applicationId = 1L;
        Application application = createApplicationWithJobAndUser(); // Create a valid Application object
        when(applicationService.getApplicationById(applicationId)).thenReturn(application);

        // Act
        ResponseEntity<EntityModel<Application>> response = applicationController.getApplicationById(applicationId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(applicationService, times(1)).getApplicationById(applicationId);
    }

    @Test
    void testGetApplicationCountByJob() {
        // Arrange
        Long jobId = 1L;
        when(applicationService.getApplicationCountByJob(jobId)).thenReturn(5);

        // Act
        ResponseEntity<Integer> response = applicationController.getApplicationCountByJob(jobId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(5, response.getBody());
        verify(applicationService, times(1)).getApplicationCountByJob(jobId);
    }

    // Helper method to create a valid Application object with Job and User
    private Application createApplicationWithJobAndUser() {
        Application application = new Application();
        Job job = new Job();
        job.setId(1L); // Set a valid job ID
        User user = new User();
        user.setUsername("testUser"); // Set a valid username
        application.setJob(job); // Associate the job with the application
        application.setUser(user); // Associate the user with the application
        return application;
    }
}