package com.prolink.job.controller;

import com.prolink.job.model.Job;
import com.prolink.user.model.User;
import com.prolink.job.service.JobService;
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

class JobControllerTest {

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostJob() {
        // Arrange
        String username = "testUser";
        Job jobDetails = new Job();
        jobDetails.setTitle("Software Engineer");
        jobDetails.setDescription("Develop software solutions");
        jobDetails.setLocation("Remote");

        Job job = createJobWithPostedBy(username); // Create a valid Job object with postedBy
        when(jobService.postJob(username, jobDetails.getTitle(), jobDetails.getDescription(), jobDetails.getLocation()))
                .thenReturn(job);

        // Act
        ResponseEntity<EntityModel<Job>> response = jobController.postJob(username, jobDetails);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(jobService, times(1)).postJob(username, jobDetails.getTitle(), jobDetails.getDescription(), jobDetails.getLocation());
    }

    @Test
    void testGetJobById() {
        // Arrange
        Long jobId = 1L;
        String username = "testUser";
        Job job = createJobWithPostedBy(username); // Create a valid Job object with postedBy
        when(jobService.getJobById(jobId)).thenReturn(job);

        // Act
        ResponseEntity<EntityModel<Job>> response = jobController.getJobById(jobId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(jobService, times(1)).getJobById(jobId);
    }

    @Test
    void testGetJobsByUser() {
        // Arrange
        String username = "testUser";
        Job job = createJobWithPostedBy(username); // Create a valid Job object with postedBy
        List<Job> jobs = Collections.singletonList(job); // Use Collections.singletonList
        when(jobService.getJobsByUser(username)).thenReturn(jobs);

        // Act
        ResponseEntity<CollectionModel<EntityModel<Job>>> response = jobController.getJobsByUser(username);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(jobService, times(1)).getJobsByUser(username);
    }

    @Test
    void testGetAllJobs() {
        // Arrange
        String username = "testUser";
        Job job = createJobWithPostedBy(username); // Create a valid Job object with postedBy
        List<Job> jobs = Collections.singletonList(job); // Use Collections.singletonList
        when(jobService.getAllJobs()).thenReturn(jobs);

        // Act
        ResponseEntity<CollectionModel<EntityModel<Job>>> response = jobController.getAllJobs();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(jobService, times(1)).getAllJobs();
    }

    // Helper method to create a valid Job object with postedBy
    private Job createJobWithPostedBy(String username) {
        Job job = new Job();
        job.setId(1L); // Set a valid job ID
        job.setTitle("Software Engineer");
        job.setDescription("Develop software solutions");
        job.setLocation("Remote");

        User postedBy = new User();
        postedBy.setUsername(username); // Set a valid username
        job.setPostedBy(postedBy); // Associate the user with the job

        return job;
    }
}