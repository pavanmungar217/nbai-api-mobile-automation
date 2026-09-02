package com.nbai.automation.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbai.automation.core.config.FrameworkConfig;

import java.util.Objects;

public final class ApiClientFactory {

    private ApiClientFactory() {
    }

    public static ApiClient create(FrameworkConfig config, ObjectMapper objectMapper) {
        FrameworkConfig requiredConfig = Objects.requireNonNull(config);
        ObjectMapper requiredObjectMapper = Objects.requireNonNull(objectMapper);
        RequestSpecificationFactory specificationFactory =
                new RequestSpecificationFactory(requiredConfig, requiredObjectMapper);
        return new RestAssuredApiClient(specificationFactory);
    }
}
