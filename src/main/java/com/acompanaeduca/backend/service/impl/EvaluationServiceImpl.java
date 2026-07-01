package com.acompanaeduca.backend.service.impl;

import com.acompanaeduca.backend.models.GenerateObservationRequest;
import com.acompanaeduca.backend.models.GenerateObservationResponse;
import com.acompanaeduca.backend.models.GenerateQuestionRequest;
import com.acompanaeduca.backend.models.GenerateQuestionResponse;
import com.acompanaeduca.backend.models.StudentWorkResponse;
import com.acompanaeduca.backend.models.ExerciseWorkResponse;
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
import java.util.Map;

@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;
    private final EvaluationProperties evaluationProperties;
        private final Map<String, StudentWorkResponse> mockedStudentWorks;
        private final Map<String, ExerciseWorkResponse> mockedExerciseWorks;

    public EvaluationServiceImpl(
            OpenAIClient openAIClient,
            ObjectMapper objectMapper,
            EvaluationProperties evaluationProperties
    ) {
        this.openAIClient = openAIClient;
        this.objectMapper = objectMapper;
        this.evaluationProperties = evaluationProperties;
        this.mockedStudentWorks = Map.of(
                "TP — Algoritmos de ordenamiento", new StudentWorkResponse(
                        "course-1",
                        "student-1",
                        "delivery-1",
                        "TP — Algoritmos de ordenamiento",
                        """
                                def bubble_sort(lista):
                                    n = len(lista)

                                    for i in range(n):
                                        for j in range(0, n - 1):
                                            if lista[j] > lista[j + 1]:
                                                lista[j], lista[j + 1] = lista[j + 1], lista[j]

                                    return lista
                                """,
                        """
                                El algoritmo de ordenamiento burbuja compara pares consecutivos e intercambia los elementos si están fuera de orden.
                                En el peor caso, su complejidad temporal es O(n²), aunque funciona bien para listas pequeñas.
                                """
                ),
                "Trabajo práctico N° 1", new StudentWorkResponse(
                        "course-1",
                        "student-2",
                        "delivery-2",
                        "Trabajo práctico N° 1",
                        """
                                def calcular_promedio(notas):
                                    return sum(notas) / len(notas)
                                """,
                        """
                                Se utilizó una función simple para calcular el promedio de una lista de notas.
                                El método consiste en sumar todos los elementos y dividir por la cantidad total.
                                """
                ),
                "Examen parcial", new StudentWorkResponse(
                        "course-1",
                        "student-3",
                        "delivery-3",
                        "Examen parcial",
                        """
                                for i in range(10):
                                    print(i)
                                """,
                        """
                                Se utilizó un algoritmo simple de estructuras repetitivas para recorrer una secuencia.
                                """
                ),
                "TP — Ecuaciones lineales", new StudentWorkResponse(
                        "course-2",
                        "student-4",
                        "delivery-4",
                        "TP — Ecuaciones lineales",
                        """
                                2x + 5 = 13
                                2x = 8
                                x = 4
                                """,
                        """
                                La ecuación fue resuelta aplicando operaciones inversas, con este procedimiento puedo despejar la variable y encontrar su valor.
                                """
                ),
                "Parcial de álgebra", new StudentWorkResponse(
                        "course-2",
                        "student-5",
                        "delivery-5",
                        "Parcial de álgebra",
                        """
                                (a + b)^2 = a^2 + 2ab + b^2
                                """,
                        """
                                La expresión se conoce como el binomio de suma al cuadrado. Significa que cuando sumas dos cantidades y elevas el resultado al cuadrado, el total es igual al cuadrado del primer número, más el doble de la multiplicación de ambos números, más el cuadrado del segundo número.
                                """
                ),
                "Práctica — Movimiento rectilíneo", new StudentWorkResponse(
                        "course-3",
                        "student-7",
                        "delivery-6",
                        "Práctica — Movimiento rectilíneo",
                        """
                                v = d / t
                                v = 100 / 20
                                v = 5 m/s
                                """,
                        """
                                Se utilizó la relación entre velocidad, distancia y tiempo.
                                Con esta fórmula podemos calcular la velocidad promedio de un objeto en movimiento.
                                """
                ),
                "Trabajo de laboratorio", new StudentWorkResponse(
                        "course-3",
                        "student-8",
                        "delivery-7",
                        "Trabajo de laboratorio",
                        """
                                F = m * a
                                F = 2 * 3
                                F = 6 N
                                """,
                        """
                                Se aplicó la segunda ley de Newton, la cual establece que la fuerza neta aplicada a un objeto es directamente proporcional al producto de su masa por la aceleración que adquiere.
                                """
                )
        );
        this.mockedExerciseWorks = Map.of(
                "TP — Algoritmos de ordenamiento", new ExerciseWorkResponse(
                        "course-1",
                        "student-1",
                        "delivery-1",
                        "1",
                        "def bubble_sort(lista):\n    n = len(lista)\n    for i in range(n):\n        for j in range(0, n - 1):\n            if lista[j] > lista[j + 1]:\n                lista[j], lista[j + 1] = lista[j + 1], lista[j]\n    return lista",
                        "Realizar algoritmo de ordenamiento burbuja y explicar su funcionamiento paso a paso."
                ),
                "Trabajo práctico N° 1", new ExerciseWorkResponse(
                        "course-1",
                        "student-2",
                        "delivery-2",
                        "1",
                        "def calcular_promedio(notas):\n    return sum(notas) / len(notas)",
                        "Realizar el cálculo del promedio de las notas y justificar el procedimiento utilizado."
                ),
                "Parcial de álgebra", new ExerciseWorkResponse(
                        "course-2",
                        "student-5",
                        "delivery-5",
                        "1",
                        "(a + b)^2 = a^2 + 2ab + b^2",
                        "Resolver la identidad notable del cuadrado de un binomio y explicar su desarrollo paso a paso."
                ),
                "TP — Ecuaciones lineales", new ExerciseWorkResponse(
                        "course-2",
                        "student-4",
                        "delivery-4",
                        "1",
                        "2x + 5 = 13\n2x = 8\nx = 4",
                        "Resolver la ecuación lineal y justificar cada paso del procedimiento."
                )
        );
    }


    @Override
    public StudentWorkResponse getStudentWork(String courseId, String studentId, String deliveryName) {
        return mockedStudentWorks.getOrDefault(
                deliveryName,
                new StudentWorkResponse(
                        normalizeValue(courseId),
                        normalizeValue(studentId),
                        normalizeValue(deliveryName),
                        "TP — Algoritmos de ordenamiento",
                        "def bubble_sort(lista):\n" + //
                        "        n = len(lista)\n" + //
                        "\n" + //
                        "        for i in range(n):\n" + //
                        "            for j in range(0, n-1):\n" + //
                        "                if lista[j] > lista[j+1]:\n" + //
                        "                    lista[j], lista[j+1] = lista[j+1], lista[j]\n" + //
                        "\n" + //
                        "        return lista\n" //
                        ,
                        "El algoritmo de ordenamiento burbuja es un método de clasificación simple que funciona " +
                        "comparando repetidamente elementos adyacentes e intercambiándolos si están en el orden incorrecto, " +
                        "con una complejidad temporal de O(n²) en el peor caso."
                )
        );
    }

    @Override
    public ExerciseWorkResponse getExerciseWork(String courseId, String studentId, String deliveryName, String exerciseNumber) {
        return mockedExerciseWorks.getOrDefault(
                deliveryName,
                new ExerciseWorkResponse(
                        normalizeValue(courseId),
                        normalizeValue(studentId),
                        normalizeValue(deliveryName),
                        normalizeValue(exerciseNumber),
                        "v = d / t\n"
                        + "v = 100 / 20\n"
                        + "v = 5 m/s",
                        "Calcular la velocidad media a partir de los datos de distancia y tiempo presentados."
                )
        );
    }

    private String normalizeValue(String value) {
        return value == null || value.isBlank() ? "default" : value;
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
    }

    private void validateObservationRequest(GenerateObservationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request es obligatorio");
        }

        if (request.submittedWork() == null || request.submittedWork().isBlank()) {
            throw new IllegalArgumentException("El desarrollo entregado por el alumno es requerido");
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
