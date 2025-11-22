package com.aimex.backend.service;

import com.aimex.backend.models.Category;
import com.aimex.backend.models.Expense;
import com.aimex.backend.service.dto.AiCategorySuggestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AiCategorizationService {

    private static final Logger LOG = LoggerFactory.getLogger(AiCategorizationService.class);

    private final CategoryService categoryService;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String model;
    private final Map<String, AiCategorySuggestion> merchantCache = new ConcurrentHashMap<>();

    public AiCategorizationService(CategoryService categoryService,
                                   RestClient.Builder restClientBuilder,
                                   @Value("${gemini.api.key:}") String apiKey,
                                   @Value("gemini-2.0-flash-lite") String model) {
        this.categoryService = categoryService;
        this.apiKey = apiKey;
        this.model = model;
        // Gemini native base URL
        this.restClient = restClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey) // Native Auth Header
                .defaultHeader("Content-Type", "application/json")
                .build();
    }


    public Optional<AiCategorySuggestion> suggestCategory(String userId, Expense expense) {
        if (expense.getCategoryId() != null) {
            return Optional.empty();
        }

        String merchant = Optional.ofNullable(expense.getMerchant())
                .map(String::trim)
                .map(String::toLowerCase)
                .orElse("");

        if (merchant.isBlank()) {
            return Optional.empty();
        }

        String cacheKey = userId + ":" + merchant;
        AiCategorySuggestion cached = merchantCache.get(cacheKey);
        if (cached != null) {
            return Optional.of(cached);
        }

        List<Category> categories = categoryService.getCategoriesByUser(userId);
        if (categories.isEmpty()) {
            // If no categories are defined by the user, we cannot suggest anything meaningful.
            return Optional.of(new AiCategorySuggestion(
                    null,
                    "Uncategorized",
                    0.0,
                    "No categories defined by user. Please create categories first."
            ));
        }

        Optional<AiCategorySuggestion> suggestion = Optional.empty();

        if (apiKey != null && !apiKey.isBlank()) {
            suggestion = callGeminiAI(expense, categories);
        }

        // If AI categorization failed or API key is missing, provide a "manual selection" suggestion.
        if (suggestion.isEmpty()) {
            return Optional.of(new AiCategorySuggestion(
                    null,
                    "Uncategorized",
                    0.0,
                    "The AI could not identify a category. Please select one manually."
            ));
        }

        suggestion.ifPresent(result -> merchantCache.put(cacheKey, result));
        return suggestion;
    }

    private Optional<AiCategorySuggestion> callGeminiAI(Expense expense, List<Category> categories) {
        try {
            String prompt = buildPrompt(expense, categories);

            Map<String, Object> request = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.2,
                            "responseMimeType", "application/json"
                    )
            );

            String response = restClient.post()
                    .uri("/models/" + model + ":generateContent")
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            return parseResponse(response, categories);

        } catch (Exception ex) {
            LOG.warn("AI categorization failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }


    private String buildPrompt(Expense expense, List<Category> categories) {
        String categoryOptions = categories.stream()
                .map(Category::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return """
            You are an AI that categorizes personal financial expense transactions.
            Infer the meaning of categories only from their names. Do not assume predefined meanings.
            Choose the most semantically appropriate category.

            Respond ONLY with a JSON object in the exact format:
            {"categoryName": "...", "confidence": 0.0, "reason": "..."}

            Notes:
            - If the transaction clearly refers to a digital game or gaming platform (e.g., Steam, Xbox, PS5, PlayStation, Battlefield etc.) -> choose the closest matching category like Gaming or similar if available.
            - If unclear or confidence is low, pick "Uncategorized" with low confidence.

            Transaction:
            Amount: %s
            Merchant: %s
            Description: %s
            Available Categories: [%s]
            """.formatted(
                expense.getAmount(),
                expense.getMerchant(),
                Optional.ofNullable(expense.getDescription()).orElse(""),
                categoryOptions
        );
    }


    private Optional<AiCategorySuggestion> parseResponse(String response, List<Category> categories) {
        try {
            JsonNode root = objectMapper.readTree(response);

            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                return Optional.empty();
            }

            String contentText = candidates.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            if (contentText == null || contentText.isBlank()) {
                return Optional.empty();
            }

            JsonNode parsedContent = objectMapper.readTree(contentText);

            String categoryName = parsedContent.path("categoryName").asText(null);
            double confidence = parsedContent.path("confidence").asDouble(0.6);
            String reason = parsedContent.path("reason").asText("AI suggested category");

            return categories.stream()
                    .filter(category -> category.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .map(category -> new AiCategorySuggestion(category.getId(), category.getName(), confidence, reason));

        } catch (Exception ex) {
            LOG.warn("Failed to parse GeminiAI response: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
