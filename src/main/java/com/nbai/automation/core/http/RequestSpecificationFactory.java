package com.nbai.automation.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbai.automation.core.config.FrameworkConfig;
import com.nbai.automation.core.http.filter.AllureEvidenceFilter;
import com.nbai.automation.core.http.filter.CorrelationIdFilter;
import com.nbai.automation.core.http.filter.SafeLoggingFilter;
import com.nbai.automation.core.http.filter.SensitiveDataSanitizer;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

import static io.restassured.config.EncoderConfig.encoderConfig;

public final class RequestSpecificationFactory {

    private final FrameworkConfig frameworkConfig;
    private final ObjectMapper objectMapper;
    private final SensitiveDataSanitizer sanitizer;

    public RequestSpecificationFactory(FrameworkConfig frameworkConfig, ObjectMapper objectMapper) {
        this.frameworkConfig = frameworkConfig;
        this.objectMapper = objectMapper;
        this.sanitizer = new SensitiveDataSanitizer(objectMapper);
    }

    public RequestSpecification create() {
        RestAssuredConfig restAssuredConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", Math.toIntExact(frameworkConfig.connectTimeout().toMillis()))
                        .setParam("http.socket.timeout", Math.toIntExact(frameworkConfig.readTimeout().toMillis())))
                .encoderConfig(encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false))
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, charset) -> objectMapper));

        return new RequestSpecBuilder()
                .setBaseUri(frameworkConfig.apiBaseUri().toString())
                .setConfig(restAssuredConfig)
                .addFilter(new CorrelationIdFilter())
                .addFilter(new SafeLoggingFilter(sanitizer))
                .addFilter(new AllureEvidenceFilter(sanitizer))
                .build();
    }
}
