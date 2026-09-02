package com.nbai.automation.api.booker.service;

import com.nbai.automation.api.booker.BookingEndpoints;
import com.nbai.automation.api.booker.model.request.AuthRequest;
import com.nbai.automation.core.config.Credentials;
import com.nbai.automation.core.http.ApiClient;
import com.nbai.automation.core.http.ApiRequest;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Objects;

import static io.restassured.http.ContentType.JSON;

public final class AuthService {

    private final ApiClient apiClient;

    public AuthService(ApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient);
    }

    public Response createToken(Credentials credentials) {
        return createToken(AuthRequest.builder()
                .username(credentials.username())
                .password(credentials.password())
                .build());
    }

    public Response createToken(AuthRequest request) {
        return apiClient.execute(ApiRequest.request(Method.POST, BookingEndpoints.AUTH)
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .body(request)
                .build());
    }
}
