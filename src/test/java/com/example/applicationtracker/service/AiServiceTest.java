package com.example.applicationtracker.service;

import com.example.applicationtracker.dto.AiJobDescriptionRequest;
import com.example.applicationtracker.dto.AiJobDescriptionResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiServiceTest {

    private final AiService aiService = new AiService();

    @Test
    void analyseJobDescriptionExtractsCloudAndAiSignals() {
        AiJobDescriptionRequest request = new AiJobDescriptionRequest();
        request.setJobDescription("""
                We need a Java Spring Boot engineer to build REST API services for cloud-native
                distributed systems on AWS. Docker, Kubernetes, Terraform, SQL, LangChain,
                TensorFlow, and AI agent experience are preferred.
                """);

        AiJobDescriptionResponse response = aiService.analyseJobDescription(request);

        assertThat(response.getRoleType()).isEqualTo("AI / Cloud Software Engineer");
        assertThat(response.getRequiredSkills()).contains("Java", "Spring Boot", "REST API", "SQL", "AI agent");
        assertThat(response.getPreferredSkills()).contains("AWS", "Docker", "Kubernetes", "Terraform", "LangChain", "TensorFlow");
        assertThat(response.isCloudRelated()).isTrue();
        assertThat(response.isAiRelated()).isTrue();
        assertThat(response.getResponsibilities()).contains("Design software for distributed systems");
        assertThat(response.getSuggestedProjects()).anyMatch(project -> project.contains("AI API"));
    }

    @Test
    void analyseJobDescriptionHandlesPlainBackendRole() {
        AiJobDescriptionRequest request = new AiJobDescriptionRequest();
        request.setJobDescription("Backend role building Java REST API services with SQL.");

        AiJobDescriptionResponse response = aiService.analyseJobDescription(request);

        assertThat(response.getRoleType()).isEqualTo("Backend Software Engineer");
        assertThat(response.isCloudRelated()).isFalse();
        assertThat(response.isAiRelated()).isFalse();
        assertThat(response.getRequiredSkills()).contains("Java", "REST API", "SQL");
    }
}
