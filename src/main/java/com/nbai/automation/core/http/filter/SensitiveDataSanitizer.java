package com.nbai.automation.core.http.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class SensitiveDataSanitizer {

    private static final String REDACTED = "<redacted>";
    private static final int MAX_BODY_LENGTH = 10_000;
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie", "x-api-key", "proxy-authorization");
    private static final Set<String> SAFE_HEADERS = Set.of(
            "accept", "content-type", "content-length", "etag", "location", "retry-after",
            "x-request-id", "x-correlation-id");
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password", "passwd", "token", "secret", "authorization", "cookie", "apikey", "api_key");
    private static final Pattern SENSITIVE_QUERY_VALUE = Pattern.compile(
            "(?i)([?&](?:password|passwd|token|secret|api[_-]?key|authorization)=)[^&]*");

    private final ObjectMapper objectMapper;

    public SensitiveDataSanitizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> sanitizeHeaders(Map<String, ?> headers) {
        Map<String, String> sanitized = new LinkedHashMap<>();
        headers.forEach((name, value) -> {
            String normalized = name.toLowerCase(Locale.ROOT);
            if (SENSITIVE_HEADERS.contains(normalized)) {
                sanitized.put(name, REDACTED);
            } else if (SAFE_HEADERS.contains(normalized)) {
                sanitized.put(name, String.valueOf(value));
            } else {
                sanitized.put(name, "<omitted>");
            }
        });
        return sanitized;
    }

    public String sanitizeBody(Object body) {
        if (body == null) {
            return "<empty>";
        }

        try {
            JsonNode tree = body instanceof String value
                    ? objectMapper.readTree(value)
                    : objectMapper.valueToTree(body);
            redact(tree);
            return truncate(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree));
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            return "<body omitted: unsupported or non-JSON content>";
        }
    }

    public String sanitizeUri(String uri) {
        if (uri == null) {
            return "<unknown-uri>";
        }
        return SENSITIVE_QUERY_VALUE.matcher(uri).replaceAll("$1" + REDACTED);
    }

    private void redact(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            objectNode.properties().forEach(entry -> {
                if (isSensitive(entry.getKey())) {
                    objectNode.put(entry.getKey(), REDACTED);
                } else {
                    redact(entry.getValue());
                }
            });
        } else if (node instanceof ArrayNode arrayNode) {
            arrayNode.forEach(this::redact);
        }
    }

    private boolean isSensitive(String fieldName) {
        String normalized = fieldName.toLowerCase(Locale.ROOT).replace("-", "_");
        return SENSITIVE_FIELDS.contains(normalized)
                || normalized.endsWith("password")
                || normalized.endsWith("token")
                || normalized.endsWith("secret");
    }

    private String truncate(String value) {
        if (value.length() <= MAX_BODY_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_BODY_LENGTH) + "\n<truncated>";
    }
}
