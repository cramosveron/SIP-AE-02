package com.acompanaeduca.backend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "evaluation")
@Data
public class EvaluationProperties {

    private Questions questions = new Questions();
    private Text text = new Text();

    @Data
    public static class Questions {
        private int defaultCount;
        private int minCount;
        private int maxCount;
    }

    @Data
    public static class Text {
        private int minLength;
        private int maxLength;
    }
}
