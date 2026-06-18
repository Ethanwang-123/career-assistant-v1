package com.example.applicationtracker.controller;

import com.example.applicationtracker.entity.JobApplication;
import com.example.applicationtracker.entity.JobApplicationStatus;
import com.example.applicationtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(
            Authentication authentication,
            @Valid @RequestBody JobApplication jobApplication
    ) {
        JobApplication createdApplication = jobApplicationService.createApplication(authentication.getName(), jobApplication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
    }

    @GetMapping
    public List<JobApplication> getApplications(
            Authentication authentication,
            @RequestParam(required = false) JobApplicationStatus status
    ) {
        return jobApplicationService.getApplications(authentication.getName(), status);
    }

    @GetMapping("/search")
    public List<JobApplication> searchApplications(Authentication authentication, @RequestParam String keyword) {
        return jobApplicationService.searchApplications(authentication.getName(), keyword);
    }

    @GetMapping("/{id}")
    public JobApplication getApplicationById(Authentication authentication, @PathVariable Long id) {
        return jobApplicationService.getApplicationById(authentication.getName(), id);
    }

    @PutMapping("/{id}")
    public JobApplication updateApplication(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody JobApplication jobApplication
    ) {
        return jobApplicationService.updateApplication(authentication.getName(), id, jobApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(Authentication authentication, @PathVariable Long id) {
        jobApplicationService.deleteApplication(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
