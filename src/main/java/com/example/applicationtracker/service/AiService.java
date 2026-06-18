package com.example.applicationtracker.service;

import com.example.applicationtracker.dto.AiJobDescriptionRequest;
import com.example.applicationtracker.dto.AiJobDescriptionResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final List<String> KEYWORDS = List.of(
            "Java",
            "Python",
            "Spring Boot",
            "REST API",
            "AWS",
            "Azure",
            "Google Cloud",
            "Docker",
            "Kubernetes",
            "SQL",
            "TensorFlow",
            "LangChain",
            "GitLab CI",
            "Terraform",
            "AI agent",
            "cloud-native",
            "distributed systems"
    );

    public AiJobDescriptionResponse analyseJobDescription(AiJobDescriptionRequest request) {
        String jobDescription = request.getJobDescription();
        String normalized = jobDescription.toLowerCase(Locale.ROOT);
        List<String> matchedSkills = findMatchedKeywords(normalized);
        boolean cloudRelated = containsAny(normalized, List.of(
                "aws", "azure", "google cloud", "cloud-native", "cloud native", "kubernetes", "docker", "terraform"
        ));
        boolean aiRelated = containsAny(normalized, List.of(
                "ai", "artificial intelligence", "machine learning", "tensorflow", "langchain", "ai agent"
        ));

        List<String> requiredSkills = inferRequiredSkills(matchedSkills, normalized);
        List<String> preferredSkills = inferPreferredSkills(matchedSkills, requiredSkills, normalized);
        List<String> responsibilities = inferResponsibilities(normalized, cloudRelated, aiRelated);
        String roleType = inferRoleType(normalized, cloudRelated, aiRelated);
        String summary = buildSummary(roleType, requiredSkills, cloudRelated, aiRelated);
        List<String> suggestedProjects = suggestProjects(requiredSkills, cloudRelated, aiRelated);

        return new AiJobDescriptionResponse(
                roleType,
                requiredSkills,
                preferredSkills,
                responsibilities,
                cloudRelated,
                aiRelated,
                summary,
                suggestedProjects
        );
    }

    private List<String> findMatchedKeywords(String normalized) {
        List<String> matches = new ArrayList<>();
        for (String keyword : KEYWORDS) {
            if (normalized.contains(keyword.toLowerCase(Locale.ROOT))) {
                matches.add(keyword);
            }
        }
        return matches;
    }

    private boolean containsAny(String normalized, List<String> keywords) {
        return keywords.stream().anyMatch(normalized::contains);
    }

    private List<String> inferRequiredSkills(List<String> matchedSkills, String normalized) {
        List<String> required = new ArrayList<>();
        for (String skill : matchedSkills) {
            String lowerSkill = skill.toLowerCase(Locale.ROOT);
            if (normalized.contains("required") || normalized.contains("must have") || normalized.contains("essential")) {
                required.add(skill);
            } else if (List.of("Java", "Python", "Spring Boot", "REST API", "SQL").contains(skill)) {
                required.add(skill);
            } else if (normalized.contains(lowerSkill + " experience")) {
                required.add(skill);
            }
        }
        return dedupe(required);
    }

    private List<String> inferPreferredSkills(List<String> matchedSkills, List<String> requiredSkills, String normalized) {
        List<String> preferred = new ArrayList<>();
        for (String skill : matchedSkills) {
            if (!requiredSkills.contains(skill)) {
                preferred.add(skill);
            }
        }
        if (normalized.contains("nice to have") || normalized.contains("preferred")) {
            matchedSkills.stream()
                    .filter(skill -> !requiredSkills.contains(skill))
                    .forEach(preferred::add);
        }
        return dedupe(preferred);
    }

    private List<String> inferResponsibilities(String normalized, boolean cloudRelated, boolean aiRelated) {
        List<String> responsibilities = new ArrayList<>();
        responsibilities.add("Build and maintain backend services");
        if (normalized.contains("rest") || normalized.contains("api")) {
            responsibilities.add("Design and expose REST APIs");
        }
        if (cloudRelated) {
            responsibilities.add("Support scalable cloud deployment and infrastructure practices");
        }
        if (normalized.contains("secure") || normalized.contains("security")) {
            responsibilities.add("Apply secure software engineering practices");
        }
        if (normalized.contains("observable") || normalized.contains("monitoring")) {
            responsibilities.add("Improve observability, monitoring, and operational reliability");
        }
        if (aiRelated) {
            responsibilities.add("Work with AI or machine learning enabled software features");
        }
        if (normalized.contains("distributed systems")) {
            responsibilities.add("Design software for distributed systems");
        }
        return dedupe(responsibilities);
    }

    private String inferRoleType(String normalized, boolean cloudRelated, boolean aiRelated) {
        if (aiRelated && cloudRelated) {
            return "AI / Cloud Software Engineer";
        }
        if (aiRelated) {
            return "AI Software Engineer";
        }
        if (cloudRelated) {
            return "Cloud Backend Engineer";
        }
        if (normalized.contains("backend") || normalized.contains("spring boot") || normalized.contains("rest api")) {
            return "Backend Software Engineer";
        }
        if (normalized.contains("data")) {
            return "Data / Software Engineer";
        }
        return "Software Engineer";
    }

    private String buildSummary(String roleType, List<String> requiredSkills, boolean cloudRelated, boolean aiRelated) {
        StringBuilder summary = new StringBuilder("This looks like a ").append(roleType).append(" role");
        if (!requiredSkills.isEmpty()) {
            summary.append(" requiring ").append(String.join(", ", requiredSkills));
        }
        if (cloudRelated) {
            summary.append(". The job description has a clear cloud or deployment focus");
        }
        if (aiRelated) {
            summary.append(". It also includes AI-related work");
        }
        summary.append(".");
        return summary.toString();
    }

    private List<String> suggestProjects(List<String> requiredSkills, boolean cloudRelated, boolean aiRelated) {
        Map<String, String> projects = new LinkedHashMap<>();
        projects.put("backend", "Build a secure Spring Boot REST API with JWT authentication and validation");
        if (requiredSkills.contains("SQL")) {
            projects.put("sql", "Add SQL-backed search, filtering, and dashboard statistics to an application tracker");
        }
        if (cloudRelated) {
            projects.put("cloud", "Containerize the backend with Docker and deploy it to a cloud platform");
            projects.put("infra", "Create Terraform infrastructure for a cloud-native application deployment");
        }
        if (aiRelated) {
            projects.put("ai", "Add job description analysis using an AI API with structured JSON output");
        }
        if (requiredSkills.contains("GitLab CI")) {
            projects.put("ci", "Create a GitLab CI pipeline that runs tests and builds the backend");
        }
        return new ArrayList<>(projects.values());
    }

    private List<String> dedupe(List<String> values) {
        return values.stream().distinct().toList();
    }
}
