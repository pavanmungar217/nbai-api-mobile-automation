package com.nbai.automation.core.http.filter;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.MDC;

import java.util.UUID;

public final class CorrelationIdFilter implements OrderedFilter {

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext context) {
        String previousCorrelationId = MDC.get("correlationId");
        MDC.put("correlationId", UUID.randomUUID().toString());
        try {
            return context.next(requestSpec, responseSpec);
        } finally {
            if (previousCorrelationId == null) {
                MDC.remove("correlationId");
            } else {
                MDC.put("correlationId", previousCorrelationId);
            }
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

