package com.example.applicationtracker.controller;

import com.example.applicationtracker.dto.AiJobDescriptionRequest;
import com.example.applicationtracker.dto.AiJobDescriptionResponse;
import com.example.applicationtracker.service.AiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/analyse-job-description")
    public AiJobDescriptionResponse analyseJobDescription(@Valid @RequestBody AiJobDescriptionRequest request) {
        return aiService.analyseJobDescription(request);
    }
}
