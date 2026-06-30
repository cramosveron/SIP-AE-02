package com.acompanaeduca.backend.service;

import com.acompanaeduca.backend.models.GenerateCorrectionsRequest;   
import com.acompanaeduca.backend.models.GenerateCorrectionsResponse;
import com.acompanaeduca.backend.models.GenerateObservationRequest;
import com.acompanaeduca.backend.models.GenerateObservationResponse;
import com.acompanaeduca.backend.models.GenerateQuestionRequest;
import com.acompanaeduca.backend.models.GenerateQuestionResponse;
import com.acompanaeduca.backend.models.StudentWorkResponse;

public interface EvaluationService {

    GenerateQuestionResponse generateQuestions(GenerateQuestionRequest request);

    GenerateObservationResponse generateObservations(GenerateObservationRequest request);

    GenerateCorrectionsResponse generateCorrections(GenerateCorrectionsRequest request);

    StudentWorkResponse getStudentWork(String courseId, String studentId, String deliveryId);
}