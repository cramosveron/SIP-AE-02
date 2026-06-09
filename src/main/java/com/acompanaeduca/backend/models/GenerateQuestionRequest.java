package com.acompanaeduca.backend.models;

public record GenerateQuestionRequest(
        String studentText,
        String originalAssignment,
        Integer questionCount
) {}
