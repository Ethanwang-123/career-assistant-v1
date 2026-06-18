package com.example.applicationtracker.service;

import com.example.applicationtracker.dto.DashboardStatsResponse;
import com.example.applicationtracker.entity.JobApplicationStatus;
import com.example.applicationtracker.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final JobApplicationRepository jobApplicationRepository;

    public DashboardService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public DashboardStatsResponse getStats(String userEmail) {
        long totalApplications = jobApplicationRepository.countByUserEmail(userEmail);
        long totalInterviews = jobApplicationRepository.countByUserEmailAndStatus(userEmail, JobApplicationStatus.INTERVIEW);
        long totalOffers = jobApplicationRepository.countByUserEmailAndStatus(userEmail, JobApplicationStatus.OFFER);
        long totalRejected = jobApplicationRepository.countByUserEmailAndStatus(userEmail, JobApplicationStatus.REJECTED);
        return new DashboardStatsResponse(totalApplications, totalInterviews, totalOffers, totalRejected);
    }
}
