package com.prolink.job.service;

import com.prolink.job.model.Application;
import com.prolink.job.model.ApplicationStatus;
import com.prolink.job.repository.ApplicationRepository;
import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.prolink.job.model.Job;
import com.prolink.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Transactional
    public Application applyForJob(String username, Long jobId, Application application) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        application.setUser(user);
        application.setJob(job);
        application.setStatus(ApplicationStatus.PENDING);

        Application savedApplication = applicationRepository.save(application);
        System.out.println("Application saved: " + savedApplication.getId()); 

        return savedApplication;
    }


    public List<Application> getApplicationsByUser(String username) {
        return applicationRepository.findByUserUsername(username);
    }


    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }
    public Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }
    public int getApplicationCountByJob(Long jobId) {
        return applicationRepository.countByJobId(jobId);
    }



}
