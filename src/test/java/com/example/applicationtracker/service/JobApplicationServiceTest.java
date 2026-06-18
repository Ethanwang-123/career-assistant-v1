package com.example.applicationtracker.service;

import com.example.applicationtracker.entity.AppUser;
import com.example.applicationtracker.entity.JobApplication;
import com.example.applicationtracker.entity.JobApplicationStatus;
import com.example.applicationtracker.exception.ResourceNotFoundException;
import com.example.applicationtracker.repository.AppUserRepository;
import com.example.applicationtracker.repository.JobApplicationRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    @Test
    void createApplicationClearsIncomingIdAndSavesApplication() {
        JobApplication request = sampleApplication(99L);
        JobApplication saved = sampleApplication(1L);
        Mockito.when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(sampleUser()));
        Mockito.when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(saved);

        JobApplication result = jobApplicationService.createApplication("test@example.com", request);

        assertThat(request.getId()).isNull();
        assertThat(request.getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(result.getId()).isEqualTo(1L);
        Mockito.verify(jobApplicationRepository).save(request);
    }

    @Test
    void getApplicationByIdReturnsExistingApplication() {
        JobApplication existingApplication = sampleApplication(1L);
        Mockito.when(jobApplicationRepository.findByIdAndUserEmail(1L, "test@example.com"))
                .thenReturn(Optional.of(existingApplication));

        JobApplication result = jobApplicationService.getApplicationById("test@example.com", 1L);

        assertThat(result.getCompanyName()).isEqualTo("OpenAI");
    }

    @Test
    void getApplicationsFiltersByStatusWhenStatusIsProvided() {
        JobApplication existingApplication = sampleApplication(1L);
        Mockito.when(jobApplicationRepository.findByUserEmailAndStatus("test@example.com", JobApplicationStatus.APPLIED))
                .thenReturn(java.util.List.of(existingApplication));

        java.util.List<JobApplication> result = jobApplicationService.getApplications("test@example.com", JobApplicationStatus.APPLIED);

        assertThat(result).hasSize(1);
        Mockito.verify(jobApplicationRepository).findByUserEmailAndStatus("test@example.com", JobApplicationStatus.APPLIED);
        Mockito.verify(jobApplicationRepository, Mockito.never()).findAll();
    }

    @Test
    void searchApplicationsSearchesRepositoryByKeyword() {
        JobApplication existingApplication = sampleApplication(1L);
        Mockito.when(jobApplicationRepository.searchByKeyword("test@example.com", "cloud"))
                .thenReturn(java.util.List.of(existingApplication));

        java.util.List<JobApplication> result = jobApplicationService.searchApplications("test@example.com", "cloud");

        assertThat(result).hasSize(1);
        Mockito.verify(jobApplicationRepository).searchByKeyword("test@example.com", "cloud");
    }

    @Test
    void getApplicationByIdThrowsWhenApplicationDoesNotExist() {
        Mockito.when(jobApplicationRepository.findByIdAndUserEmail(99L, "test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobApplicationService.getApplicationById("test@example.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Application not found with id: 99");
    }

    @Test
    void updateApplicationCopiesFieldsAndSaves() {
        JobApplication existingApplication = sampleApplication(1L);
        JobApplication update = sampleApplication(null);
        update.setCompanyName("Anthropic");
        update.setStatus(JobApplicationStatus.OFFER);

        Mockito.when(jobApplicationRepository.findByIdAndUserEmail(1L, "test@example.com"))
                .thenReturn(Optional.of(existingApplication));
        Mockito.when(jobApplicationRepository.save(existingApplication)).thenReturn(existingApplication);

        JobApplication result = jobApplicationService.updateApplication("test@example.com", 1L, update);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCompanyName()).isEqualTo("Anthropic");
        assertThat(result.getStatus()).isEqualTo(JobApplicationStatus.OFFER);
    }

    @Test
    void deleteApplicationDeletesExistingApplication() {
        JobApplication existingApplication = sampleApplication(1L);
        Mockito.when(jobApplicationRepository.findByIdAndUserEmail(1L, "test@example.com"))
                .thenReturn(Optional.of(existingApplication));

        jobApplicationService.deleteApplication("test@example.com", 1L);

        Mockito.verify(jobApplicationRepository).delete(existingApplication);
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

    private AppUser sampleUser() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setEmail("test@example.com");
        appUser.setPassword("encoded-password");
        return appUser;
    }
}
