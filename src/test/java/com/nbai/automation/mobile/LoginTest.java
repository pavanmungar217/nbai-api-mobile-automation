package com.nbai.automation.mobile;

import com.nbai.automation.mobile.config.MobileCredential;
import com.nbai.automation.mobile.screen.LoginScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class LoginTest extends BaseMobileTest {

    @Test(groups = "mobile", description = "Log in with valid credentials")
    public void validLoginReturnsToCatalog() {
        MobileCredential credential = mobile.credentials().valid();
        LoginScreen login = mobile.catalog().openMenu().openLogin();
        Assert.assertTrue(login.isLoaded(), "Login screen was not displayed");

        var catalog = login.loginSuccessfully(credential.username(), credential.password());

        Assert.assertTrue(catalog.isLoaded(), "Valid login did not return to the product catalog");
    }

    @Test(groups = "mobile", description = "Reject invalid login credentials")
    public void invalidLoginShowsError() {
        MobileCredential credential = mobile.credentials().invalid();
        LoginScreen login = mobile.catalog().openMenu().openLogin();

        login.loginExpectingFailure(credential.username(), credential.password());

        Assert.assertEquals(
                login.errorMessage(),
                "Provided credentials do not match any user in this service.");
        Assert.assertTrue(login.isLoaded(), "Invalid login unexpectedly left the login screen");
    }
}
