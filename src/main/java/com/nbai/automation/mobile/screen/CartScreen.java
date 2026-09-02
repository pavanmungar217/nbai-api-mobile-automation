package com.nbai.automation.mobile.screen;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public final class CartScreen extends BaseScreen {

    @AndroidFindBy(accessibility = "cart screen")
    private WebElement cartScreen;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Sauce Labs Backpack\")")
    private WebElement backpack;

    @AndroidFindBy(accessibility = "counter amount")
    private WebElement quantityCounter;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"$29.99\")")
    private List<WebElement> backpackPrices;

    @AndroidFindBy(accessibility = "Proceed To Checkout button")
    private WebElement proceedToCheckoutButton;

    public CartScreen(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public boolean isLoaded() {
        return isDisplayed(cartScreen);
    }

    public String backpackName() {
        return wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(backpack)).getText();
    }

    public String quantity() {
        WebElement counter = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(quantityCounter));
        return counter.findElement(io.appium.java_client.AppiumBy.className("android.widget.TextView")).getText();
    }

    public List<String> displayedItemAndTotalPrices() {
        return wait.until(ignored -> {
            List<String> prices = backpackPrices.stream()
                    .filter(WebElement::isDisplayed)
                    .map(WebElement::getText)
                    .toList();
            return prices.size() >= 2 ? prices : null;
        });
    }

    public CheckoutAddressScreen proceedToCheckout() {
        tap(proceedToCheckoutButton);
        return new CheckoutAddressScreen(driver, timeout);
    }
}
