package com.acompanaeduca.backend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
@Data
public class OpenAIProperties {

    private String apiKey;
    private String model;
}
