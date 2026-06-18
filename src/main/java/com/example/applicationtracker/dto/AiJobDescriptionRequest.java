package com.example.applicationtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AiJobDescriptionRequest {

    @NotBlank(message = "Job description is required")
    @Size(max = 20000, message = "Job description must be 20000 characters or fewer")
    private String jobDescription;

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
