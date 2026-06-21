package com.acompanaeduca.backend.models;

public record CorrectionItem(
        String original,
        String suggestion,
        String type
) {}