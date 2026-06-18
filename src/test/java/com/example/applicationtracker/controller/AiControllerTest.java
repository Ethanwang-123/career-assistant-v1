package com.example.applicationtracker.controller;

import com.example.applicationtracker.dto.AiJobDescriptionRequest;
import com.example.applicationtracker.dto.AiJobDescriptionResponse;
import com.example.applicationtracker.security.AppUserDetailsService;
import com.example.applicationtracker.security.JwtService;
import com.example.applicationtracker.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
@WithMockUser(username = "test@example.com")
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiService aiService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void analyseJobDescriptionReturnsAnalysis() throws Exception {
        AiJobDescriptionRequest request = new AiJobDescriptionRequest();
        request.setJobDescription("Java Spring Boot AWS cloud-native role.");
        AiJobDescriptionResponse response = new AiJobDescriptionResponse(
                "Cloud Backend Engineer",
                List.of("Java", "Spring Boot"),
                List.of("AWS"),
                List.of("Build and maintain backend services"),
                true,
                false,
                "This looks like a Cloud Backend Engineer role.",
                List.of("Containerize the backend with Docker and deploy it to a cloud platform")
        );
        Mockito.when(aiService.analyseJobDescription(any(AiJobDescriptionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/ai/analyse-job-description")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleType").value("Cloud Backend Engineer"))
                .andExpect(jsonPath("$.cloudRelated").value(true))
                .andExpect(jsonPath("$.aiRelated").value(false))
                .andExpect(jsonPath("$.requiredSkills[0]").value("Java"));
    }

    @Test
    void analyseJobDescriptionValidatesBlankDescription() throws Exception {
        AiJobDescriptionRequest request = new AiJobDescriptionRequest();
        request.setJobDescription("");

        mockMvc.perform(post("/ai/analyse-job-description")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.jobDescription").value("Job description is required"));
    }
}
