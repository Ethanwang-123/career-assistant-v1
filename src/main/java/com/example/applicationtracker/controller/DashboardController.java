package com.example.applicationtracker.controller;

import com.example.applicationtracker.dto.DashboardStatsResponse;
import com.example.applicationtracker.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardStatsResponse getStats(Authentication authentication) {
        return dashboardService.getStats(authentication.getName());
    }
}
