package com.example.pcbuilder.config;


import com.example.pcbuilder.pojo.AiBuildRequest;
import com.example.pcbuilder.pojo.AiBuildResponse;
import com.example.pcbuilder.pojo.CandidateParts;
import com.example.pcbuilder.config.PromptBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class OpenRouterClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private final String apiKey;
    private final String model;

    public OpenRouterClient(
             @Value("${openrouter.api-key}") String apiKey,
            @Value("${openrouter.model}") String model
    ) {
        this.webClient = WebClient.create(
                "https://openrouter.ai/api/v1"
        );

        this.objectMapper = new ObjectMapper();

        this.apiKey = apiKey;
        this.model = model;
    }

    public AiBuildResponse recommendBuild(
            AiBuildRequest request,
            CandidateParts candidates
    ) throws JsonProcessingException {

        String prompt = PromptBuilder.buildPrompt(request,candidates);

        System.out.println("========== PROMPT SENT TO AI ==========");
        System.out.println(prompt);
        System.out.println("=======================================");

        OpenRouterRequest body = new OpenRouterRequest(
                model,
                List.of(
                        new OpenRouterRequest.Message(
                                "system",
                                """
                                You are an expert PC hardware configuration engine.
    
                                You must follow the user's output format exactly.
                                Return ONLY the requested JSON object.
                                Do not output reasoning or analysis.
                                """
                        ),
                        new OpenRouterRequest.Message(
                                "user",
                                prompt
                        )
                ),
                0.1,
                10000
        );

        String rawResponse = webClient
                .post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("HTTP-Referer", "http://localhost:5173")
                .header("X-Title", "PC Builder")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("========== OPENROUTER RAW RESPONSE ==========");
        System.out.println(rawResponse);
        System.out.println("==============================================");

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new RuntimeException("OpenRouter returned an empty response");
        }

        try {

            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(rawResponse);

            System.out.println("========== OPENROUTER PARSED ==========");
            System.out.println(
                    objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(root)
            );
            System.out.println("=======================================");

            com.fasterxml.jackson.databind.JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {

                String errorMessage = root
                        .path("error")
                        .path("message")
                        .asText("");

                if (!errorMessage.isBlank()) {
                    throw new RuntimeException(
                            "OpenRouter API error: " + errorMessage
                    );
                }

                throw new RuntimeException(
                        "OpenRouter returned no choices. Full response:\n"
                                + rawResponse
                );
            }

            com.fasterxml.jackson.databind.JsonNode choice = choices.get(0);

            String finishReason =
                    choice.path("finish_reason").asText("");

            System.out.println("Finish reason: " + finishReason);

            JsonNode message = choice.path("message");

            String content =
                    message.path("content").asText("");

            System.out.println("========== AI CONTENT ==========");
            System.out.println(content);
            System.out.println("================================");

            if (content.isBlank()) {

                String reasoning =
                        message.path("reasoning").asText("");

                throw new RuntimeException(
                        "OpenRouter returned an empty message content.\n"
                                + "finish_reason=" + finishReason
                                + "\nreasoning=" + reasoning
                );
            }

            return parseResponse(content);

        } catch (Exception e) {

            if (e instanceof RuntimeException) {
                throw e;
            }

            throw new RuntimeException(
                    "Failed to process OpenRouter response:\n"
                            + rawResponse,
                    e
            );
        }
    }

    private AiBuildResponse parseResponse(String content) {

        System.out.println("========== AI RESPONSE ==========");
        System.out.println(content);
        System.out.println("=================================");

        try {
            String json = extractJson(content);

            System.out.println("========== EXTRACTED JSON ==========");
            System.out.println(json);
            System.out.println("====================================");

            return objectMapper.readValue(json, AiBuildResponse.class);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse AI response as JSON. AI response was:\n" + content,
                    e
            );
        }
    }

    private String extractJson(String content) {

        content = content.trim();

        // Remove markdown code fences if the model uses them
        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }

        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }

        content = content.trim();

        // Find the first JSON object
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException(
                    "AI response does not contain a JSON object"
            );
        }

        return content.substring(start, end + 1);
    }
}