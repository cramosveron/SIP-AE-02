package com.acompanaeduca.backend.service.impl;

import com.acompanaeduca.backend.models.GenerateQuestionRequest;
import com.acompanaeduca.backend.models.GenerateQuestionResponse;
import com.acompanaeduca.backend.properties.EvaluationProperties;
import com.acompanaeduca.backend.service.EvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.stereotype.Service;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;
    private final EvaluationProperties evaluationProperties;

    public EvaluationServiceImpl(
            OpenAIClient openAIClient,
            ObjectMapper objectMapper,
            EvaluationProperties evaluationProperties
    ) {
        this.openAIClient = openAIClient;
        this.objectMapper = objectMapper;
        this.evaluationProperties = evaluationProperties;
    }


    @Override
    public GenerateQuestionResponse generateQuestions(GenerateQuestionRequest request) {
        validateRequest(request);

        int questionCount = request.questionCount() != null
                ? request.questionCount()
                : evaluationProperties.getQuestions().getDefaultCount();

        questionCount = Math.max(
                evaluationProperties.getQuestions().getMinCount(),
                Math.min(questionCount, evaluationProperties.getQuestions().getMaxCount())
        );

        String originalAssignment = request.originalAssignment() != null
                && !request.originalAssignment().isBlank()
                ? request.originalAssignment()
                : "No se proporcionó consigna original.";

        String prompt = """
                Sos un asistente pedagógico.

                Vas a recibir un texto entregado por un alumno.

                Tu tarea es generar preguntas que una profesora pueda hacerle oralmente o por escrito
                para comprobar si el alumno realmente entendió lo que escribió.

                No corrijas el texto.
                No califiques al alumno.
                No expliques las respuestas.
                No devuelvas resumen, análisis, nivel ni comentarios.
                No acuses copia ni plagio.

                Generá exactamente %d preguntas.

                Las preguntas deben obligar al alumno a:
                - explicar con sus propias palabras;
                - justificar afirmaciones del texto;
                - dar ejemplos nuevos;
                - relacionar conceptos;
                - aplicar lo escrito a una situación distinta;
                - aclarar términos técnicos usados en el texto.

                Evitá preguntas que puedan responderse copiando literalmente una frase del texto.

                Consigna original:
                %s

                Texto del alumno:
                %s

                Devolvé únicamente el JSON solicitado en este formato:.
                {
                  "questions": [
                    "question 1",
                    "question 2",
                    "question 3"
                  ]
                }
                """.formatted(
                questionCount,
                originalAssignment,
                request.studentText()
        );

        try {
            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_5_2)
                    .input(prompt)
                    .build();

            Response response = openAIClient.responses().create(params);

            String json = response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .map(content -> content.asOutputText().text())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Empty OpenAI response"));

            return objectMapper.readValue(json, GenerateQuestionResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("No se pudieron generar preguntas");
        }
    }

    private void validateRequest(GenerateQuestionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request es obligatorio");
        }

        if (request.studentText() == null || request.studentText().isBlank()) {
            throw new IllegalArgumentException("El texto del estudiante es requerido");
        }

        if (request.studentText().length() < evaluationProperties.getText().getMinLength()) {
            throw new IllegalArgumentException("El texto del estudiante es demasiado corto para generar preguntas.");
        }

        if (request.studentText().length() > evaluationProperties.getText().getMaxLength()) {
            throw new IllegalArgumentException("El texto del estudiante supero la cantidad máxima de caracteres.");
        }
    }
}
