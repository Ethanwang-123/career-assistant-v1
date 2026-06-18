package com.example.applicationtracker.repository;

import com.example.applicationtracker.entity.JobApplication;
import com.example.applicationtracker.entity.JobApplicationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByUserEmail(String email);

    List<JobApplication> findByUserEmailAndStatus(String email, JobApplicationStatus status);

    java.util.Optional<JobApplication> findByIdAndUserEmail(Long id, String email);

    @Query("""
            select application
            from JobApplication application
            where application.user.email = :email
              and (
                  lower(application.companyName) like lower(concat('%', :keyword, '%'))
                  or lower(application.roleTitle) like lower(concat('%', :keyword, '%'))
                  or lower(application.location) like lower(concat('%', :keyword, '%'))
                  or lower(application.notes) like lower(concat('%', :keyword, '%'))
              )
            """)
    List<JobApplication> searchByKeyword(@Param("email") String email, @Param("keyword") String keyword);

    long countByUserEmail(String email);

    long countByUserEmailAndStatus(String email, JobApplicationStatus status);
}
