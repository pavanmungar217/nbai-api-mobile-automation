package com.nbai.automation.core.http;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Objects;

public final class RestAssuredApiClient implements ApiClient {

    private final RequestSpecificationFactory requestSpecificationFactory;

    public RestAssuredApiClient(RequestSpecificationFactory requestSpecificationFactory) {
        this.requestSpecificationFactory = Objects.requireNonNull(requestSpecificationFactory);
    }

    @Override
    public Response execute(ApiRequest request) {
        RequestSpecification specification = RestAssured.given()
                .spec(requestSpecificationFactory.create());
        if (!request.headers().isEmpty()) {
            specification.headers(request.headers());
        }
        if (!request.queryParameters().isEmpty()) {
            specification.queryParams(request.queryParameters());
        }
        if (request.body() != null) {
            specification.body(request.body());
        }
        return specification.request(request.method(), request.path());
    }
}
