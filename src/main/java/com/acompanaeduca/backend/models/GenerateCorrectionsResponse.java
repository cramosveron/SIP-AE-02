package com.acompanaeduca.backend.models;

import java.util.List;

public record GenerateCorrectionsResponse(
        String correctedText,
        List<CorrectionItem> corrections
) {}