package com.nbai.automation.api.booker.service;

import com.nbai.automation.api.booker.BookingEndpoints;
import com.nbai.automation.core.http.ApiClient;
import com.nbai.automation.core.http.ApiRequest;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Objects;

public final class HealthService {

    private final ApiClient apiClient;

    public HealthService(ApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient);
    }

    public Response ping() {
        return apiClient.execute(ApiRequest.request(Method.GET, BookingEndpoints.PING).build());
    }
}

