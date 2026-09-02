package com.nbai.automation.core.http;

import io.restassured.http.Method;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record ApiRequest(
        Method method,
        String path,
        Map<String, Object> headers,
        Map<String, Object> queryParameters,
        Object body) {

    public ApiRequest {
        Objects.requireNonNull(method, "HTTP method must not be null");
        if (path == null || !path.startsWith("/") || path.startsWith("//")) {
            throw new IllegalArgumentException("API path must be a relative path beginning with '/'");
        }
        headers = Map.copyOf(headers == null ? Map.of() : headers);
        queryParameters = Map.copyOf(queryParameters == null ? Map.of() : queryParameters);
    }

    public static Builder request(Method method, String path) {
        return new Builder(method, path);
    }

    public static final class Builder {

        private final Method method;
        private final String path;
        private final Map<String, Object> headers = new LinkedHashMap<>();
        private final Map<String, Object> queryParameters = new LinkedHashMap<>();
        private Object body;

        private Builder(Method method, String path) {
            this.method = method;
            this.path = path;
        }

        public Builder header(String name, Object value) {
            headers.put(name, value);
            return this;
        }

        public Builder queryParameter(String name, Object value) {
            queryParameters.put(name, value);
            return this;
        }

        public Builder queryParameters(Map<String, ?> values) {
            values.forEach(queryParameters::put);
            return this;
        }

        public Builder body(Object value) {
            body = value;
            return this;
        }

        public ApiRequest build() {
            return new ApiRequest(method, path, headers, queryParameters, body);
        }
    }
}

