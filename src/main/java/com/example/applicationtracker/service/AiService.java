package com.example.applicationtracker.service;

import com.example.applicationtracker.dto.AiJobDescriptionRequest;
import com.example.applicationtracker.dto.AiJobDescriptionResponse;
import com.example.applicationtracker.exception.AiServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private static final String GEMINI_MODEL = "gemini-2.5-flash";
    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";
    private static final Duration GEMINI_TIMEOUT = Duration.ofSeconds(30);

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

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String geminiApiKey;

    public AiService(ObjectMapper objectMapper, @Value("${gemini.api.key:}") String geminiApiKey) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.geminiApiKey = geminiApiKey;
    }

    public AiJobDescriptionResponse analyseJobDescription(AiJobDescriptionRequest request) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return analyseWithMockRules(request.getJobDescription());
        }

        return analyseWithGemini(request.getJobDescription());
    }

    private AiJobDescriptionResponse analyseWithGemini(String jobDescription) {
        try {
            String requestBody = objectMapper.writeValueAsString(buildGeminiRequest(jobDescription));
            HttpRequest httpRequest = HttpRequest.newBuilder(buildGeminiUri())
                    .timeout(GEMINI_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AiServiceException("Gemini API request failed with status "
                        + response.statusCode()
                        + ". Response body: "
                        + response.body());
            }

            return parseGeminiResponse(response.body());
        } catch (JsonProcessingException exception) {
            throw new AiServiceException("Could not build or parse Gemini JSON response", exception);
        } catch (IOException exception) {
            throw new AiServiceException("Could not connect to Gemini API", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AiServiceException("Gemini API request was interrupted", exception);
        }
    }

    private URI buildGeminiUri() {
        return URI.create(GEMINI_ENDPOINT + "?key=" + URLEncoder.encode(geminiApiKey, StandardCharsets.UTF_8));
    }

    private Map<String, Object> buildGeminiRequest(String jobDescription) {
        String prompt = """
                Analyse this job description and return only strict JSON.
                Do not include markdown, code fences, comments, or extra text.

                The JSON must exactly match this shape:
                {
                  "roleType": "string",
                  "requiredSkills": ["string"],
                  "preferredSkills": ["string"],
                  "responsibilities": ["string"],
                  "cloudRelated": true,
                  "aiRelated": true,
                  "summary": "string",
                  "suggestedProjects": ["string"]
                }

                Job description:
                %s
                """.formatted(jobDescription);

        Map<String, Object> schema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "roleType", Map.of("type", "STRING"),
                        "requiredSkills", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
                        "preferredSkills", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
                        "responsibilities", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
                        "cloudRelated", Map.of("type", "BOOLEAN"),
                        "aiRelated", Map.of("type", "BOOLEAN"),
                        "summary", Map.of("type", "STRING"),
                        "suggestedProjects", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"))
                ),
                "required", List.of(
                        "roleType",
                        "requiredSkills",
                        "preferredSkills",
                        "responsibilities",
                        "cloudRelated",
                        "aiRelated",
                        "summary",
                        "suggestedProjects"
                )
        );

        return Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "response_mime_type", "application/json",
                        "response_schema", schema
                )
        );
    }

    private AiJobDescriptionResponse parseGeminiResponse(String responseBody) throws JsonProcessingException {
        JsonNode textNode = objectMapper.readTree(responseBody)
                .path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            throw new AiServiceException("Gemini API returned an empty analysis response");
        }

        return objectMapper.readValue(stripJsonCodeFence(textNode.asText()), AiJobDescriptionResponse.class);
    }

    private String stripJsonCodeFence(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(?:json)?\\s*", "");
            trimmed = trimmed.replaceFirst("\\s*```$", "");
        }
        return trimmed.trim();
    }

    private AiJobDescriptionResponse analyseWithMockRules(String jobDescription) {
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
