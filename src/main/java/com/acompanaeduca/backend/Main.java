package com.acompanaeduca.backend;

import com.acompanaeduca.backend.properties.EvaluationProperties;
import com.acompanaeduca.backend.properties.OpenAIProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		OpenAIProperties.class,
		EvaluationProperties.class
})
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
