package se.rasha.ai_lab;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiClientService {

    private final String apiKey;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiClientService(@Value("${openai.api.key:}") String apiKey) {

        this.apiKey = apiKey;

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(2000);
        factory.setReadTimeout(8000);

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @PostConstruct
    public void validateApiKey() {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CRITICAL: API key is missing.");
        }

        System.out.println("API Key loaded successfully");


    }
    public String analyzeText(String userInput) {

        String systemPrompt = """
            You are a sentiment analysis AI.

            Return ONLY valid JSON.
            Do not return markdown.
            Do not return explanations.

            JSON format:
            {
              "sentiment": "positive",
              "score": 90
            }
            """;

        String requestBody = """
            {
              "model": "gpt-3.5-turbo",
              "temperature": 0.1,
              "messages": [
                {
                  "role": "system",
                  "content": "%s"
                },
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(systemPrompt, userInput);

        int retries = 3;
        long delay = 1000;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                return restClient.post()
                        .uri("https://api.openai.com/v1/chat/completions")
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .body(requestBody)
                        .retrieve()
                        .body(String.class);

            } catch (HttpClientErrorException e) {

                if (e.getStatusCode().value() == 429) {
                    System.out.println("Rate limit hit. Retrying in " + delay + " ms");

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        return "{\"sentiment\":\"unknown\",\"score\":0}";
                    }

                    delay *= 2;

                } else {
                    System.out.println("HTTP Error: " + e.getStatusCode());
                    return "{\"sentiment\":\"unknown\",\"score\":0}";
                }

            } catch (Exception e) {
                System.out.println("Unexpected Error: " + e.getMessage());
                return "{\"sentiment\":\"unknown\",\"score\":0}";
            }
        }

        return "{\"sentiment\":\"unknown\",\"score\":0}";
    }
    public AiResponseDto parseResponse(String json) {

        try {
            return objectMapper.readValue(json, AiResponseDto.class);

        } catch (JsonProcessingException e) {

            System.out.println("Invalid JSON from AI");

            return new AiResponseDto("unknown", 0);
        }
    }

}