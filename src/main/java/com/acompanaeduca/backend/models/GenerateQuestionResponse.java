package com.acompanaeduca.backend.models;

import java.util.List;

public record GenerateQuestionResponse(
        List<String> questions
) {}
