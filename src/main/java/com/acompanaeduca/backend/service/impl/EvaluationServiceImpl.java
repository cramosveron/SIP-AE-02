package com.acompanaeduca.backend.service.impl;

import com.acompanaeduca.backend.models.GenerateObservationRequest;
import com.acompanaeduca.backend.models.GenerateObservationResponse;
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
import com.acompanaeduca.backend.models.GenerateCorrectionsRequest;
import com.acompanaeduca.backend.models.GenerateCorrectionsResponse;
import com.acompanaeduca.backend.models.CorrectionItem;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public GenerateObservationResponse generateObservations(GenerateObservationRequest request) {
        validateObservationRequest(request);

        String workName = request.workName() != null ? request.workName() : "No se proporcionó nombre del trabajo.";

        String explanation = request.explanation() != null && !request.explanation().isBlank()
                ? request.explanation()
                : "No se proporcionó explicación adicional.";

        String activeParameters = request.activeParameters() != null && !request.activeParameters().isEmpty()
                ? String.join(", ", request.activeParameters())
                : "No se proporcionaron parámetros activos.";

        String prompt = """
                Sos un asistente pedagógico.

                Vas a recibir el desarrollo entregado por un alumno y la explicación que ofreció sobre ese trabajo.

                Tu tarea es generar observaciones que ayuden a detectar posibles copias, explicaciones genéricas, errores conceptuales o señales de comprensión insuficiente.

                No corrijas el texto.
                No califiques al alumno.
                No expliques las respuestas.
                No devuelvas resumen, análisis, nivel ni comentarios adicionales.
                No acuses directamente de plagio.

                Generá exactamente una observación por cada parámetro activo.

                Las observaciones deben enfocarse en:
                - indicadores de copia o uso de contenido de terceros sin adaptación;
                - frases vagas o explicaciones genéricas que no demuestran comprensión propia;
                - inconsistencias entre el trabajo entregado y la explicación;
                - posibles errores conceptuales, omisiones importantes o afirmaciones poco claras.

                Nombre del trabajo:
                %s

                Desarrollo entregado por el alumno:
                %s

                Explicación del alumno:
                %s

                Parámetros activos:
                %s

                Devolvé únicamente el JSON solicitado en este formato:
                {
                  "observations": {
                    "tipo de observación": "observación 1",
                    "tipo de observación": "observación 2",
                    "tipo de observación": "observación 3"
                  }
                }
                """.formatted(
                workName,
                request.submittedWork(),
                explanation,
                activeParameters
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

            return objectMapper.readValue(json, GenerateObservationResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("No se pudieron generar observaciones");
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

    private void validateObservationRequest(GenerateObservationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request es obligatorio");
        }

        if (request.submittedWork() == null || request.submittedWork().isBlank()) {
            throw new IllegalArgumentException("El desarrollo entregado por el alumno es requerido");
        }

        if (request.submittedWork().length() < evaluationProperties.getText().getMinLength()) {
            throw new IllegalArgumentException("El desarrollo entregado es demasiado corto para generar observaciones.");
        }

        if (request.submittedWork().length() > evaluationProperties.getText().getMaxLength()) {
            throw new IllegalArgumentException("El desarrollo entregado supero la cantidad máxima de caracteres.");
        }

        if (request.explanation() != null && request.explanation().length() > evaluationProperties.getText().getMaxLength()) {
            throw new IllegalArgumentException("La explicación supero la cantidad máxima de caracteres.");
        }
    }
    
    @Override
    public GenerateCorrectionsResponse generateCorrections(GenerateCorrectionsRequest request) {
    if (request == null || request.text() == null || request.text().isBlank()) {
        throw new IllegalArgumentException("El texto a corregir es requerido");
    }

    String prompt = """
            Sos un asistente experto en corrección de textos en español.
            Analizá el siguiente texto buscando errores ortográficos, gramaticales,
            de redacción, de claridad, de puntuación y de uso de mayúsculas.

            Devolvé ÚNICAMENTE un JSON válido con este formato exacto, sin texto adicional,
            sin bloques de código, sin comillas extras:
            {
              "correctedText": "<texto corregido completo>",
              "corrections": [
                {
                  "original": "<fragmento original con error>",
                  "suggestion": "<corrección sugerida>",
                  "type": "<Ortografía | Gramática | Redacción | Claridad | Mayúsculas | Puntuación>"
                }
              ]
            }

            Si no hay errores, devolvé el texto tal cual con corrections como lista vacía.

            Texto a corregir:
            %s
            """.formatted(request.text());

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

        JsonNode root = objectMapper.readTree(json);

        String correctedText = root.get("correctedText").asText();
        List<CorrectionItem> corrections = new ArrayList<>();

        JsonNode items = root.get("corrections");
        if (items != null && items.isArray()) {
            for (JsonNode item : items) {
                corrections.add(new CorrectionItem(
                        item.get("original").asText(),
                        item.get("suggestion").asText(),
                        item.get("type").asText()
                ));
            }
        }

        return new GenerateCorrectionsResponse(correctedText, corrections);

    } catch (Exception e) {
        throw new RuntimeException("No se pudo generar la corrección");
    }
}
}
