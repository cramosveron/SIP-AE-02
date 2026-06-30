package com.acompanaeduca.backend.models;

public record StudentWorkRequest(
        String courseId,
        String studentId,
        String deliveryId
) {
}
