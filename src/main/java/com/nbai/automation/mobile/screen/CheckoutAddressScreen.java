package com.nbai.automation.mobile.screen;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class CheckoutAddressScreen extends BaseScreen {

    @AndroidFindBy(accessibility = "checkout address screen")
    private WebElement checkoutAddressScreen;

    @AndroidFindBy(accessibility = "Full Name* input field")
    private WebElement fullNameField;

    @AndroidFindBy(accessibility = "Address Line 1* input field")
    private WebElement addressLineOneField;

    @AndroidFindBy(accessibility = "City* input field")
    private WebElement cityField;

    @AndroidFindBy(accessibility = "Zip Code* input field")
    private WebElement zipCodeField;

    @AndroidFindBy(accessibility = "Country* input field")
    private WebElement countryField;

    @AndroidFindBy(accessibility = "To Payment button")
    private WebElement toPaymentButton;

    public CheckoutAddressScreen(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public boolean isLoaded() {
        return isDisplayed(checkoutAddressScreen);
    }

    public boolean isFullNameRequiredInputDisplayed() {
        return isDisplayed(fullNameField);
    }

    public boolean isAddressLineOneRequiredInputDisplayed() {
        return isDisplayed(addressLineOneField);
    }

    public boolean isCityRequiredInputDisplayed() {
        return isDisplayed(cityField);
    }

    public boolean isZipCodeRequiredInputDisplayed() {
        return isDisplayed(zipCodeField);
    }

    public boolean isCountryRequiredInputDisplayed() {
        return isDisplayed(countryField);
    }

    public boolean isToPaymentDisplayed() {
        return isDisplayed(toPaymentButton);
    }
}
