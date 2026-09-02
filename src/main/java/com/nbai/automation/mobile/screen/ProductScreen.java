package com.nbai.automation.mobile.screen;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class ProductScreen extends BaseScreen {

    @AndroidFindBy(accessibility = "product screen")
    private WebElement productScreen;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Sauce Labs Backpack\")")
    private WebElement backpackName;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"$29.99\")")
    private WebElement backpackPrice;

    @AndroidFindBy(accessibility = "Add To Cart button")
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = "cart badge")
    private WebElement cartBadge;

    public ProductScreen(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public boolean isLoaded() {
        return isDisplayed(productScreen);
    }

    public String productName() {
        return wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(backpackName)).getText();
    }

    public String productPrice() {
        return wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(backpackPrice)).getText();
    }

    public ProductScreen addToCart() {
        tap(addToCartButton);
        return this;
    }

    public String cartCount() {
        WebElement badge = wait.until(
                org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf(cartBadge));
        return badge.findElement(AppiumBy.className("android.widget.TextView")).getText();
    }

    public CartScreen openCart() {
        tap(cartBadge);
        return new CartScreen(driver, timeout);
    }
}
