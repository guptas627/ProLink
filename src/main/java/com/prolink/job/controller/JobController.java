package com.prolink.job.controller;

import com.prolink.job.model.Job;
import com.prolink.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping("/{username}/post")
    public ResponseEntity<EntityModel<Job>> postJob(@PathVariable String username, @RequestBody Job jobDetails) {
        Job job = jobService.postJob(username, jobDetails.getTitle(), jobDetails.getDescription(), jobDetails.getLocation());

        EntityModel<Job> jobResource = EntityModel.of(job,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobById(job.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobsByUser(username)).withRel("user-jobs")
        );

        return ResponseEntity.ok(jobResource);
    }

    @GetMapping("/{username}")
    public ResponseEntity<CollectionModel<EntityModel<Job>>> getJobsByUser(@PathVariable String username) {
        List<Job> jobs = jobService.getJobsByUser(username);

        List<EntityModel<Job>> jobResources = jobs.stream().map(job -> EntityModel.of(job,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobById(job.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobsByUser(username)).withRel("user-jobs")
        )).collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(jobResources));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<EntityModel<Job>> getJobById(@PathVariable Long jobId) {
        Job job = jobService.getJobById(jobId);

        if (job.getPostedBy() == null) {
            throw new RuntimeException("Posted By field is missing in Job");
        }

        EntityModel<Job> jobResource = EntityModel.of(job,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobById(jobId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobsByUser(job.getPostedBy().getUsername())).withRel("user-jobs")
        );

        return ResponseEntity.ok(jobResource);
    }
    @GetMapping("")
    public ResponseEntity<CollectionModel<EntityModel<Job>>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs(); 

        List<EntityModel<Job>> jobResources = jobs.stream().map(job -> EntityModel.of(job,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobById(job.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(JobController.class).getJobsByUser(job.getPostedBy().getUsername())).withRel("user-jobs")
        )).collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(jobResources));
    }


}
