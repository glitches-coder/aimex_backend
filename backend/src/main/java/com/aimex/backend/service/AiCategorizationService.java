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
    private static final Map<String, List<String>> KEYWORD_HINTS = Map.of(
            "food", List.of("swiggy", "zomato", "dine", "restaurant", "cafe"),
            "travel", List.of("uber", "ola", "flight", "hotel", "airlines"),
            "shopping", List.of("amazon", "flipkart", "mall", "shopping"),
            "entertainment", List.of("movie", "netflix", "bookmyshow", "entertainment")
    );

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
            return Optional.empty();
        }

        Optional<AiCategorySuggestion> suggestion;

        if (apiKey == null || apiKey.isBlank()) {
            suggestion = heuristicGuess(merchant, categories);
        } else {
            suggestion = callGeminiAI(expense, categories);
            if (suggestion.isEmpty()) {
                suggestion = heuristicGuess(merchant, categories);
            }
        }

        suggestion.ifPresent(result -> merchantCache.put(cacheKey, result));
        return suggestion;
    }

    private Optional<AiCategorySuggestion> callGeminiAI(Expense expense, List<Category> categories) {
        try {
            String prompt = buildPrompt(expense, categories);

            Map<String, Object> request = Map.of(
                    "model", model,
                    "temperature", 0.2,
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content",
                                    "You classify financial transactions into one of the provided categories. " +
                                            "Respond ONLY with JSON in the form {\"categoryName\": \"Food\", \"confidence\": 0.82, \"reason\": \"...\"}."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            String response = restClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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
                .orElse("Food, Travel, Shopping");

        return """
                Amount: %s
                Merchant: %s
                Description: %s
                Categories: [%s]
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
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return Optional.empty();
            }

            String content = choices.get(0).path("message").path("content").asText();
            if (content == null || content.isBlank()) {
                return Optional.empty();
            }

            JsonNode parsedContent = objectMapper.readTree(content);
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

    private Optional<AiCategorySuggestion> heuristicGuess(String merchant, List<Category> categories) {
        for (Map.Entry<String, List<String>> hintEntry : KEYWORD_HINTS.entrySet()) {
            boolean matches = hintEntry.getValue().stream().anyMatch(merchant::contains);
            if (!matches) {
                continue;
            }

            Optional<Category> matchedCategory = categories.stream()
                    .filter(category -> category.getName().toLowerCase().contains(hintEntry.getKey()))
                    .findFirst();

            if (matchedCategory.isPresent()) {
                Category category = matchedCategory.get();
                return Optional.of(new AiCategorySuggestion(
                        category.getId(),
                        category.getName(),
                        0.55,
                        "Matched merchant keywords to category " + category.getName()
                ));
            }
        }

        Category fallback = categories.getFirst();
        return Optional.of(new AiCategorySuggestion(
                fallback.getId(),
                fallback.getName(),
                0.4,
                "Fallback category due to missing AI key/keywords"
        ));
    }
}

