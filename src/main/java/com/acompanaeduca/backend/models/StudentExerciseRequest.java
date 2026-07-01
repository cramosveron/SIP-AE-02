package com.acompanaeduca.backend.models;

public record StudentExerciseRequest(
        String courseId,
        String studentId,
        String deliveryName,
        String exerciseNumber
) {
}
