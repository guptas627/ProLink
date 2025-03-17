package com.prolink.job.service;

import com.prolink.job.model.Job;
import com.prolink.job.repository.JobRepository;
import com.prolink.user.model.User;
import com.prolink.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public Job postJob(String username, String title, String description, String location) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = Job.builder()
                .postedBy(user)
                .title(title)
                .description(description)
                .location(location)
                .createdAt(LocalDateTime.now())
                .build();
        return jobRepository.save(job);
    }

    public List<Job> getJobsByUser(String username) {
        return jobRepository.findByPostedByUsername(username);
    }
    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }



    }
