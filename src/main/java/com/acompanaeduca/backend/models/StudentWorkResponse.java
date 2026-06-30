package com.acompanaeduca.backend.models;

public record StudentWorkResponse(
        String courseId,
        String studentId,
        String deliveryId,
        String workName,
        String submittedWork,
        String explanation
) {
}
