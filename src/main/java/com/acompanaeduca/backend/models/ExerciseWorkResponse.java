package com.acompanaeduca.backend.models;

public record ExerciseWorkResponse(
        String courseId,
        String studentId,
        String deliveryId,
        String exerciseNumber,
        String submittedWork,
        String assignment
) {
}
