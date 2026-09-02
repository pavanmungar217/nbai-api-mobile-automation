package com.nbai.automation.api.booker.service;

import com.nbai.automation.core.http.ApiClient;

import java.util.Objects;

public final class BookerServicesFactory {

    private BookerServicesFactory() {
    }

    public static BookerServices create(ApiClient apiClient) {
        ApiClient sharedApiClient = Objects.requireNonNull(apiClient);
        return new BookerServices(
                new AuthService(sharedApiClient),
                new BookingService(sharedApiClient),
                new HealthService(sharedApiClient));
    }
}
