package com.example.applicationtracker.controller;

import com.example.applicationtracker.entity.JobApplication;
import com.example.applicationtracker.entity.JobApplicationStatus;
import com.example.applicationtracker.exception.ResourceNotFoundException;
import com.example.applicationtracker.security.AppUserDetailsService;
import com.example.applicationtracker.security.JwtService;
import com.example.applicationtracker.service.JobApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobApplicationController.class)
@WithMockUser(username = "test@example.com")
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobApplicationService jobApplicationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    void createApplicationReturnsCreatedApplication() throws Exception {
        JobApplication request = sampleApplication(null);
        JobApplication response = sampleApplication(1L);
        Mockito.when(jobApplicationService.createApplication(eq("test@example.com"), any(JobApplication.class))).thenReturn(response);

        mockMvc.perform(post("/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.companyName").value("OpenAI"))
                .andExpect(jsonPath("$.roleTitle").value("Backend Developer"));
    }

    @Test
    void getAllApplicationsReturnsApplications() throws Exception {
        Mockito.when(jobApplicationService.getApplications("test@example.com", null)).thenReturn(List.of(sampleApplication(1L)));

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].companyName").value("OpenAI"));
    }

    @Test
    void getApplicationsCanFilterByStatus() throws Exception {
        Mockito.when(jobApplicationService.getApplications("test@example.com", JobApplicationStatus.APPLIED))
                .thenReturn(List.of(sampleApplication(1L)));

        mockMvc.perform(get("/applications?status=APPLIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("APPLIED"));
    }

    @Test
    void searchApplicationsReturnsMatchingApplications() throws Exception {
        Mockito.when(jobApplicationService.searchApplications("test@example.com", "cloud"))
                .thenReturn(List.of(sampleApplication(1L)));

        mockMvc.perform(get("/applications/search?keyword=cloud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getApplicationByIdReturnsNotFoundWhenMissing() throws Exception {
        Mockito.when(jobApplicationService.getApplicationById("test@example.com", 99L))
                .thenThrow(new ResourceNotFoundException("Application not found with id: 99"));

        mockMvc.perform(get("/applications/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Application not found with id: 99"));
    }

    @Test
    void updateApplicationReturnsUpdatedApplication() throws Exception {
        JobApplication request = sampleApplication(null);
        request.setStatus(JobApplicationStatus.INTERVIEW);
        JobApplication response = sampleApplication(1L);
        response.setStatus(JobApplicationStatus.INTERVIEW);
        Mockito.when(jobApplicationService.updateApplication(eq("test@example.com"), eq(1L), any(JobApplication.class))).thenReturn(response);

        mockMvc.perform(put("/applications/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"));
    }

    @Test
    void deleteApplicationReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/applications/1").with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(jobApplicationService).deleteApplication("test@example.com", 1L);
    }

    @Test
    void validationReturnsBadRequestForMissingRequiredFields() throws Exception {
        JobApplication invalidApplication = sampleApplication(null);
        invalidApplication.setCompanyName("");

        mockMvc.perform(post("/applications")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidApplication)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.companyName").value("Company name is required"));
    }

    private JobApplication sampleApplication(Long id) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(id);
        jobApplication.setCompanyName("OpenAI");
        jobApplication.setRoleTitle("Backend Developer");
        jobApplication.setLocation("Remote");
        jobApplication.setStatus(JobApplicationStatus.APPLIED);
        jobApplication.setApplicationDate(LocalDate.of(2026, 6, 18));
        jobApplication.setDeadline(LocalDate.of(2026, 7, 1));
        jobApplication.setNotes("Submitted through company careers page.");
        return jobApplication;
    }
}
