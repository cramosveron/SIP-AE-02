package com.acompanaeduca.backend.controller;

import com.acompanaeduca.backend.models.GenerateQuestionRequest;
import com.acompanaeduca.backend.models.GenerateQuestionResponse;
import com.acompanaeduca.backend.service.EvaluationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping(
            value = "/questions",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateQuestionResponse generateQuestions(
            @RequestBody String studentText,
            @RequestParam(required = false) String originalAssignment,
            @RequestParam(required = false, defaultValue = "5") Integer questionCount
    ) {
        String normalizedText = normalizeText(studentText);

        GenerateQuestionRequest request = new GenerateQuestionRequest(
                normalizedText,
                originalAssignment,
                questionCount
        );

        return evaluationService.generateQuestions(request);
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();
    }
}
