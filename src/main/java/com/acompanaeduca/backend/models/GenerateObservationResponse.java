package com.acompanaeduca.backend.models;

import java.util.Map;

public record GenerateObservationResponse(
    Map<String, String> observations
) {}
