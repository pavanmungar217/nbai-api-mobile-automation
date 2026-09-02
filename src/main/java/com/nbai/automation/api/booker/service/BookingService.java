package com.nbai.automation.api.booker.service;

import com.nbai.automation.api.booker.BookingEndpoints;
import com.nbai.automation.api.booker.model.request.BookingPatchRequest;
import com.nbai.automation.api.booker.model.request.BookingRequest;
import com.nbai.automation.core.config.Credentials;
import com.nbai.automation.core.http.ApiClient;
import com.nbai.automation.core.http.ApiRequest;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.Map;
import java.util.Objects;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.http.ContentType.JSON;

public final class BookingService {

    private final ApiClient apiClient;

    public BookingService(ApiClient apiClient) {
        this.apiClient = Objects.requireNonNull(apiClient);
    }

    public Response create(BookingRequest booking) {
        return createJson(booking, JSON.toString(), "application/json");
    }

    public Response createJson(BookingRequest booking, String accept, String contentType) {
        return apiClient.execute(ApiRequest.request(Method.POST, BookingEndpoints.BOOKING)
                .header("Content-Type", contentType)
                .header("Accept", accept)
                .body(booking)
                .build());
    }

    public Response createXml(String xmlBody) {
        return apiClient.execute(ApiRequest.request(Method.POST, BookingEndpoints.BOOKING)
                .header("Content-Type", "text/xml")
                .header("Accept", JSON.toString())
                .body(xmlBody)
                .build());
    }

    public Response get(int bookingId) {
        return get(bookingId, JSON.toString());
    }

    public Response get(int bookingId, String accept) {
        return apiClient.execute(ApiRequest.request(Method.GET, BookingEndpoints.bookingById(bookingId))
                .header("Accept", accept)
                .build());
    }

    public Response find(Map<String, ?> filters) {
        return apiClient.execute(ApiRequest.request(Method.GET, BookingEndpoints.BOOKING)
                .header("Accept", JSON.toString())
                .queryParameters(filters)
                .build());
    }

    public Response update(int bookingId, BookingRequest booking, String token) {
        return apiClient.execute(ApiRequest.request(Method.PUT, BookingEndpoints.bookingById(bookingId))
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .header("Cookie", tokenCookie(token))
                .body(booking)
                .build());
    }

    public Response updateWithoutAuthentication(int bookingId, BookingRequest booking) {
        return apiClient.execute(ApiRequest.request(Method.PUT, BookingEndpoints.bookingById(bookingId))
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .body(booking)
                .build());
    }

    public Response updateWithBasicAuthentication(int bookingId, BookingRequest booking, Credentials credentials) {
        return apiClient.execute(ApiRequest.request(Method.PUT, BookingEndpoints.bookingById(bookingId))
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .header("Authorization", basicAuthorization(credentials))
                .body(booking)
                .build());
    }

    public Response partialUpdate(int bookingId, BookingPatchRequest patch, String token) {
        return apiClient.execute(ApiRequest.request(Method.PATCH, BookingEndpoints.bookingById(bookingId))
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .header("Cookie", tokenCookie(token))
                .body(patch)
                .build());
    }

    public Response partialUpdateWithoutAuthentication(int bookingId, BookingPatchRequest patch) {
        return apiClient.execute(ApiRequest.request(Method.PATCH, BookingEndpoints.bookingById(bookingId))
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .body(patch)
                .build());
    }

    public Response partialUpdateWithBasicAuthentication(
            int bookingId, BookingPatchRequest patch, Credentials credentials) {
        return apiClient.execute(ApiRequest.request(Method.PATCH, BookingEndpoints.bookingById(bookingId))
                .header("Content-Type", JSON.toString())
                .header("Accept", JSON.toString())
                .header("Authorization", basicAuthorization(credentials))
                .body(patch)
                .build());
    }

    public Response delete(int bookingId, String token) {
        return apiClient.execute(ApiRequest.request(Method.DELETE, BookingEndpoints.bookingById(bookingId))
                .header("Cookie", tokenCookie(token))
                .build());
    }

    public Response deleteWithoutAuthentication(int bookingId) {
        return apiClient.execute(ApiRequest.request(Method.DELETE, BookingEndpoints.bookingById(bookingId)).build());
    }

    public Response deleteWithBasicAuthentication(int bookingId, Credentials credentials) {
        return apiClient.execute(ApiRequest.request(Method.DELETE, BookingEndpoints.bookingById(bookingId))
                .header("Authorization", basicAuthorization(credentials))
                .build());
    }

    private String tokenCookie(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Authentication token must not be blank");
        }
        return "token=" + token;
    }

    private String basicAuthorization(Credentials credentials) {
        Objects.requireNonNull(credentials, "Credentials must not be null");
        String value = credentials.username() + ":" + credentials.password();
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
