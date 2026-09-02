package com.nbai.automation.api.booker;

import com.nbai.automation.api.booker.model.request.AuthRequest;
import com.nbai.automation.api.booker.model.response.AuthResponse;
import com.nbai.automation.core.config.Credentials;
import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Restful Booker API")
@Feature("Authentication")
public final class AuthTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "smoke"}, description = "Create token with valid credentials")
    public void createTokenWithValidCredentials() {
        Response response = services.auth().createToken(config.requireCredentials());
        assertJson(response, HttpStatus.OK);
        AuthResponse body = response.as(AuthResponse.class);
        Assert.assertNotNull(body.getToken(), "token must exist");
        Assert.assertFalse(body.getToken().isBlank(), "token must be nonblank");
        Assert.assertNull(body.getReason(), "reason must be absent for valid credentials");
        Assert.assertEquals(response.jsonPath().getMap("$").size(), 1, "body must contain only token");
    }

    @Test(groups = {"api", "negative"}, description = "Reject invalid authentication password")
    public void rejectInvalidAuthenticationPassword() {
        Credentials credentials = config.requireCredentials();
        AuthRequest request = AuthRequest.builder()
                .username(credentials.username())
                .password("Invalid-" + runId())
                .build();
        assertBadCredentials(services.auth().createToken(request));
    }

    @Test(groups = {"api", "negative"}, description = "Reject authentication without username")
    public void rejectAuthenticationWithoutUsername() {
        assertBadCredentials(services.auth().createToken(
                AuthRequest.builder().password("Invalid-password").build()));
    }
}
