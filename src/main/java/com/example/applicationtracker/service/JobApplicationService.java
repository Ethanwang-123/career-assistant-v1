package com.example.applicationtracker.service;

import com.example.applicationtracker.entity.JobApplication;
import com.example.applicationtracker.entity.JobApplicationStatus;
import com.example.applicationtracker.exception.ResourceNotFoundException;
import com.example.applicationtracker.repository.AppUserRepository;
import com.example.applicationtracker.repository.JobApplicationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final AppUserRepository appUserRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, AppUserRepository appUserRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.appUserRepository = appUserRepository;
    }

    public JobApplication createApplication(String userEmail, JobApplication jobApplication) {
        jobApplication.setId(null);
        jobApplication.setUser(appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail)));
        return jobApplicationRepository.save(jobApplication);
    }

    public List<JobApplication> getApplications(String userEmail, JobApplicationStatus status) {
        if (status != null) {
            return jobApplicationRepository.findByUserEmailAndStatus(userEmail, status);
        }
        return jobApplicationRepository.findByUserEmail(userEmail);
    }

    public List<JobApplication> searchApplications(String userEmail, String keyword) {
        return jobApplicationRepository.searchByKeyword(userEmail, keyword);
    }

    public JobApplication getApplicationById(String userEmail, Long id) {
        return jobApplicationRepository.findByIdAndUserEmail(id, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    public JobApplication updateApplication(String userEmail, Long id, JobApplication updatedApplication) {
        JobApplication existingApplication = getApplicationById(userEmail, id);
        existingApplication.setCompanyName(updatedApplication.getCompanyName());
        existingApplication.setRoleTitle(updatedApplication.getRoleTitle());
        existingApplication.setLocation(updatedApplication.getLocation());
        existingApplication.setStatus(updatedApplication.getStatus());
        existingApplication.setApplicationDate(updatedApplication.getApplicationDate());
        existingApplication.setDeadline(updatedApplication.getDeadline());
        existingApplication.setNotes(updatedApplication.getNotes());
        return jobApplicationRepository.save(existingApplication);
    }

    public void deleteApplication(String userEmail, Long id) {
        JobApplication existingApplication = getApplicationById(userEmail, id);
        jobApplicationRepository.delete(existingApplication);
    }
}
