package com.prolink.job.controller;

import com.prolink.job.model.Application;
import com.prolink.job.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping("/{username}/apply/{jobId}")
    public ResponseEntity<?> applyForJob(@PathVariable String username, @PathVariable Long jobId, @RequestBody Application application) {
        Application savedApplication = applicationService.applyForJob(username, jobId, application);

        if (savedApplication.getId() == null) {
            System.out.println(" Application NOT saved!");
            return ResponseEntity.badRequest().body("Application not saved");
        } else {
            System.out.println(" Application successfully saved with ID: " + savedApplication.getId());
        }

        return ResponseEntity.ok("Application submitted successfully!");
    }


    @GetMapping("/job/{jobId}")
    public ResponseEntity<CollectionModel<EntityModel<Application>>> getApplicationsByJob(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJob(jobId);

        List<EntityModel<Application>> applicationResources = applications.stream().map(application -> EntityModel.of(application,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationById(application.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationsByUser(application.getUser().getUsername())).withRel("user-applications")
        )).collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(applicationResources));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<CollectionModel<EntityModel<Application>>> getApplicationsByUser(@PathVariable String username) {
        List<Application> applications = applicationService.getApplicationsByUser(username);

        List<EntityModel<Application>> applicationResources = applications.stream().map(application -> EntityModel.of(application,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationById(application.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationsByJob(application.getJob().getId())).withRel("job-applications")
        )).collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(applicationResources));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<EntityModel<Application>> getApplicationById(@PathVariable Long applicationId) {
        Application application = applicationService.getApplicationById(applicationId);

        if (application.getUser() == null || application.getJob() == null) {
            throw new RuntimeException("User or Job is missing in Application");
        }

        EntityModel<Application> applicationResource = EntityModel.of(application,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationById(applicationId)).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationsByUser(application.getUser().getUsername())).withRel("user-applications"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ApplicationController.class).getApplicationsByJob(application.getJob().getId())).withRel("job-applications")
        );

        return ResponseEntity.ok(applicationResource);
    }
    @GetMapping("/count/{jobId}")
    public ResponseEntity<Integer> getApplicationCountByJob(@PathVariable Long jobId) {
        int count = applicationService.getApplicationCountByJob(jobId);
        return ResponseEntity.ok(count);
    }


}
