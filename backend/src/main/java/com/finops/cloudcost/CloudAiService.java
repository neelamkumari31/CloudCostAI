package com.finops.cloudcost;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class CloudAiService {

    @Value("${spring.ai.google.api-key}")
    private String apiKey;

    // Adjust this based on what `GET /v1beta/models?key=YOUR_KEY` returns as
    // available to your key. gemini-2.5-flash-lite gives the highest free-tier
    // RPM if you're rate-limit constrained; use gemini-2.5-flash for better quality
    // at a lower RPM cap.
    private static final String GEMINI_MODEL = "gemini-3.5-flash-lite";

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent";

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateOptimizationFix(String serverId, int cpu, int cost) {
        String cleanKey = apiKey.trim().replaceAll("\\s+", "");

        String prompt = "You are a Cloud FinOps Engineer. " +
                "Fix this underutilized server resource. " +
                "Server ID: " + serverId + ", " +
                "CPU Utilization: " + cpu + "%, " +
                "Monthly Cost: " + cost + " INR. " +
                "Output exactly in this layout format: " +
                "SUMMARY: [1-sentence plan] SCRIPT: [1-line AWS CLI shell command]";

        GeminiPayload payload = new GeminiPayload(prompt);

        HttpHeaders headers = new HttpHeaders();
        // Key travels in the header, not the URL — nothing sensitive ends up in logs.
        headers.set("x-goog-api-key", cleanKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiPayload> requestEntity = new HttpEntity<>(payload, headers);

        try {
            System.out.println("Calling Gemini endpoint: " + GEMINI_ENDPOINT + " for server " + serverId);

            Map<?, ?> rawResponse = restTemplate.postForObject(GEMINI_ENDPOINT, requestEntity, Map.class);
            String text = extractText(rawResponse);

            return text != null
                    ? text
                    : "SUMMARY: Optimization logged. SCRIPT: # Verification required";

        } catch (RestClientResponseException e) {
            // HTTP-level error: 401 bad key, 403 API not enabled, 404 bad model name,
            // 429 quota exceeded, 400 malformed request, etc.
            System.err.println("Gemini API returned HTTP " + e.getRawStatusCode()
                    + " for server " + serverId + ": " + e.getStatusText());
            return "SUMMARY: API request failed (HTTP " + e.getRawStatusCode()
                    + "). SCRIPT: # Check API key, model name, and request payload";

        } catch (RestClientException e) {
            // Genuine network/connectivity failure (timeout, DNS, connection refused).
            System.err.println("Network error calling Gemini API for server " + serverId
                    + ": " + e.getMessage());
            return "SUMMARY: Connection dropped. SCRIPT: # Check network connectivity";

        } catch (Exception e) {
            // Unexpected parsing or runtime error.
            System.err.println("Unexpected error processing Gemini response for server "
                    + serverId + ": " + e.getMessage());
            return "SUMMARY: Unexpected error. SCRIPT: # Review logs for details";
        }
    }

    /**
     * Safely walks the Gemini response body, returning null instead of throwing
     * if any expected field is missing (e.g. a safety-filtered response with no
     * content/parts).
     */
    private String extractText(Map<?, ?> rawResponse) {
        if (rawResponse == null || !rawResponse.containsKey("candidates")) {
            return null;
        }
        if (!(rawResponse.get("candidates") instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }
        if (!(candidates.get(0) instanceof Map<?, ?> firstCandidate)) {
            return null;
        }
        if (!(firstCandidate.get("content") instanceof Map<?, ?> content)) {
            return null;
        }
        if (!(content.get("parts") instanceof List<?> parts) || parts.isEmpty()) {
            return null;
        }
        if (!(parts.get(0) instanceof Map<?, ?> firstPart)) {
            return null;
        }
        Object text = firstPart.get("text");
        return text instanceof String ? (String) text : null;
    }

    private static class GeminiPayload {
        public List<Content> contents;

        public GeminiPayload(String promptText) {
            this.contents = List.of(new Content(promptText));
        }

        public static class Content {
            public List<Part> parts;

            public Content(String promptText) {
                this.parts = List.of(new Part(promptText));
            }

            public static class Part {
                public String text;

                public Part(String promptText) {
                    this.text = promptText;
                }
            }
        }
    }
}