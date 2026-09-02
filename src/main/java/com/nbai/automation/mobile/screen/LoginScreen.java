package com.nbai.automation.mobile.screen;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class LoginScreen extends BaseScreen {

    @AndroidFindBy(accessibility = "login screen")
    private WebElement loginScreen;

    @AndroidFindBy(accessibility = "Username input field")
    private WebElement usernameField;

    @AndroidFindBy(accessibility = "Password input field")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "Login button")
    private WebElement loginButton;

    @AndroidFindBy(accessibility = "generic-error-message")
    private WebElement genericErrorMessage;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Provided credentials do not match any user in this service.\")")
    private WebElement invalidCredentialsMessage;

    public LoginScreen(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public boolean isLoaded() {
        return isDisplayed(loginScreen);
    }

    public CatalogScreen loginSuccessfully(String username, String password) {
        submit(username, password);
        return new CatalogScreen(driver, timeout);
    }

    public LoginScreen loginExpectingFailure(String username, String password) {
        submit(username, password);
        return this;
    }

    public String errorMessage() {
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(genericErrorMessage));
        return wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(invalidCredentialsMessage)).getText();
    }

    private void submit(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
        tap(loginButton);
    }
}
