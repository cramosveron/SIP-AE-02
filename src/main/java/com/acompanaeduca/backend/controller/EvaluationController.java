package com.acompanaeduca.backend.controller;

import com.acompanaeduca.backend.models.GenerateObservationRequest;
import com.acompanaeduca.backend.models.GenerateObservationResponse;
import com.acompanaeduca.backend.models.GenerateQuestionRequest;
import com.acompanaeduca.backend.models.GenerateQuestionResponse;
import com.acompanaeduca.backend.models.StudentWorkRequest;
import com.acompanaeduca.backend.models.StudentWorkResponse;
import com.acompanaeduca.backend.service.EvaluationService;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping(
            value = "/student-work",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public StudentWorkResponse getStudentWork(@RequestBody StudentWorkRequest request) {
        return evaluationService.getStudentWork(
                request == null ? null : request.courseId(),
                request == null ? null : request.studentId(),
                request == null ? null : request.deliveryId()
        );
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

    @PostMapping(
            value = "/observations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public GenerateObservationResponse generateObservations(@RequestBody GenerateObservationRequest request) {
        String workName = request == null ? null : normalizeText(request.workName());
        String submittedWork = request == null ? null : normalizeText(request.submittedWork());
        String explanation = request == null ? null : normalizeText(request.explanation());
        List<String> activeParameters = request == null ? null : request.activeParameters();

        GenerateObservationRequest normalizedRequest = new GenerateObservationRequest(
                workName,
                submittedWork,
                explanation,
                activeParameters
        );

        return evaluationService.generateObservations(normalizedRequest);
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
