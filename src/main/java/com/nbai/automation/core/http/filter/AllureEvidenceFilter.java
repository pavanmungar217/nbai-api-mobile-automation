package com.nbai.automation.core.http.filter;

import io.qameta.allure.Allure;
import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AllureEvidenceFilter implements OrderedFilter {

    private final SensitiveDataSanitizer sanitizer;

    public AllureEvidenceFilter(SensitiveDataSanitizer sanitizer) {
        this.sanitizer = sanitizer;
    }

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext context) {
        Response response = context.next(requestSpec, responseSpec);
        if (Allure.getLifecycle().getCurrentTestCaseOrStep().isEmpty()) {
            return response;
        }

        String requestEvidence = requestSpec.getMethod() + " " + sanitizer.sanitizeUri(requestSpec.getURI())
                + "\nHeaders: " + sanitizer.sanitizeHeaders(headers(requestSpec))
                + "\nBody:\n" + sanitizer.sanitizeBody(requestSpec.getBody());
        String responseEvidence = "Status: " + response.statusCode()
                + "\nHeaders: " + sanitizer.sanitizeHeaders(responseHeaders(response))
                + "\nBody:\n" + sanitizer.sanitizeBody(response.asString());

        Allure.step(requestSpec.getMethod() + " " + sanitizer.sanitizeUri(requestSpec.getURI())
                + " -> " + response.statusCode(), () -> {
            Allure.addAttachment("Sanitized request", "text/plain", requestEvidence, ".txt");
            Allure.addAttachment("Sanitized response", "text/plain", responseEvidence, ".txt");
        });
        return response;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    private Map<String, String> headers(FilterableRequestSpecification requestSpec) {
        return requestSpec.getHeaders().asList().stream()
                .collect(LinkedHashMap::new, (map, header) -> map.put(header.getName(), header.getValue()), Map::putAll);
    }

    private Map<String, String> responseHeaders(Response response) {
        return response.getHeaders().asList().stream()
                .collect(LinkedHashMap::new, (map, header) -> map.put(header.getName(), header.getValue()), Map::putAll);
    }
}
