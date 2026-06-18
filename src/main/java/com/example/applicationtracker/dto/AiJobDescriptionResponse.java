package com.example.applicationtracker.dto;

import java.util.List;

public class AiJobDescriptionResponse {

    private String roleType;
    private List<String> requiredSkills;
    private List<String> preferredSkills;
    private List<String> responsibilities;
    private boolean cloudRelated;
    private boolean aiRelated;
    private String summary;
    private List<String> suggestedProjects;

    public AiJobDescriptionResponse(
            String roleType,
            List<String> requiredSkills,
            List<String> preferredSkills,
            List<String> responsibilities,
            boolean cloudRelated,
            boolean aiRelated,
            String summary,
            List<String> suggestedProjects
    ) {
        this.roleType = roleType;
        this.requiredSkills = requiredSkills;
        this.preferredSkills = preferredSkills;
        this.responsibilities = responsibilities;
        this.cloudRelated = cloudRelated;
        this.aiRelated = aiRelated;
        this.summary = summary;
        this.suggestedProjects = suggestedProjects;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }

    public List<String> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<String> responsibilities) {
        this.responsibilities = responsibilities;
    }

    public boolean isCloudRelated() {
        return cloudRelated;
    }

    public void setCloudRelated(boolean cloudRelated) {
        this.cloudRelated = cloudRelated;
    }

    public boolean isAiRelated() {
        return aiRelated;
    }

    public void setAiRelated(boolean aiRelated) {
        this.aiRelated = aiRelated;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getSuggestedProjects() {
        return suggestedProjects;
    }

    public void setSuggestedProjects(List<String> suggestedProjects) {
        this.suggestedProjects = suggestedProjects;
    }
}
