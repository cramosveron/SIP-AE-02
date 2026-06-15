package com.acompanaeduca.backend.models;

import java.util.List;

public record GenerateObservationRequest(
    String workName,
    String submittedWork,
    String explanation,
    List<String> activeParameters
) {}
