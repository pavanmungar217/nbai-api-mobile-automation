package com.nbai.automation.api.booker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbai.automation.api.booker.data.BookingDataFactory;
import com.nbai.automation.api.booker.model.response.AuthResponse;
import com.nbai.automation.api.booker.service.BookerServices;
import com.nbai.automation.api.booker.service.BookerServicesFactory;
import com.nbai.automation.core.config.ConfigLoader;
import com.nbai.automation.core.config.FrameworkConfig;
import com.nbai.automation.core.http.ApiClient;
import com.nbai.automation.core.http.ApiClientFactory;
import com.nbai.automation.core.http.HttpStatus;
import com.nbai.automation.core.json.JsonMapperFactory;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

public abstract class BaseApiTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseApiTest.class);

    protected FrameworkConfig config;
    protected ObjectMapper objectMapper;
    protected BookerServices services;
    protected BookingDataFactory bookingData;

    @BeforeClass(alwaysRun = true)
    public void initializeFramework() {
        config = ConfigLoader.load();
        objectMapper = JsonMapperFactory.create();
        ApiClient apiClient = ApiClientFactory.create(config, objectMapper);
        services = BookerServicesFactory.create(apiClient);
        bookingData = new BookingDataFactory();
    }

    protected String freshAuthenticationToken() {
        Response response = services.auth().createToken(config.requireCredentials());
        Assert.assertEquals(response.statusCode(), HttpStatus.OK, "Authentication request failed");
        AuthResponse authResponse = response.as(AuthResponse.class);
        Assert.assertNotNull(authResponse.getToken(), "Authentication response did not contain a token");
        Assert.assertFalse(authResponse.getToken().isBlank(), "Authentication token was blank");
        return authResponse.getToken();
    }

    protected void deleteQuietly(int bookingId, String token) {
        if (bookingId <= 0 || token == null || token.isBlank()) {
            return;
        }
        try {
            Response response = services.bookings().delete(bookingId, token);
            if (response.statusCode() != HttpStatus.CREATED && response.statusCode() != HttpStatus.NOT_FOUND) {
                LOGGER.warn("Cleanup for booking {} returned HTTP {}", bookingId, response.statusCode());
            }
        } catch (RuntimeException exception) {
            LOGGER.warn("Cleanup failed for booking {}: {}", bookingId, exception.toString());
        }
    }
}
