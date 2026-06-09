package com.acompanaeduca.backend.service;

import com.acompanaeduca.backend.models.GenerateQuestionRequest;
import com.acompanaeduca.backend.models.GenerateQuestionResponse;

public interface EvaluationService {

    GenerateQuestionResponse generateQuestions(GenerateQuestionRequest request);
}