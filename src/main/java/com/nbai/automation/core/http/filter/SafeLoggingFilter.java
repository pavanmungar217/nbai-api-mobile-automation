package com.nbai.automation.core.http.filter;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class SafeLoggingFilter implements OrderedFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SafeLoggingFilter.class);

    private final SensitiveDataSanitizer sanitizer;

    public SafeLoggingFilter(SensitiveDataSanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext context) {
        String safeUri = sanitizer.sanitizeUri(requestSpec.getURI());
        LOGGER.info("HTTP request {} {}", requestSpec.getMethod(), safeUri);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Request headers: {}", sanitizer.sanitizeHeaders(headers(requestSpec)));
            LOGGER.debug("Request body: {}", sanitizer.sanitizeBody(requestSpec.getBody()));
        }

        long startedAt = System.nanoTime();
        try {
            Response response = context.next(requestSpec, responseSpec);
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
            LOGGER.info("HTTP response {} {} in {} ms", response.statusCode(), safeUri, durationMs);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Response headers: {}", sanitizer.sanitizeHeaders(response.getHeaders().asList().stream()
                        .collect(LinkedHashMap::new, (map, header) -> map.put(header.getName(), header.getValue()), Map::putAll)));
                LOGGER.debug("Response body: {}", sanitizer.sanitizeBody(response.asString()));
            }
            return response;
        } catch (RuntimeException exception) {
            LOGGER.warn("HTTP request failed {} {}: {}", requestSpec.getMethod(), safeUri, exception.toString());
            throw exception;
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Map<String, String> headers(FilterableRequestSpecification requestSpec) {
        return requestSpec.getHeaders().asList().stream()
                .collect(LinkedHashMap::new, (map, header) -> map.put(header.getName(), header.getValue()), Map::putAll);
    }
}

