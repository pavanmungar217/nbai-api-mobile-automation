package com.nbai.automation.mobile.screen;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class CatalogScreen extends BaseScreen {

    @AndroidFindBy(accessibility = "products screen")
    private WebElement productsScreen;

    @AndroidFindBy(accessibility = "open menu")
    private WebElement openMenuButton;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Sauce Labs Backpack\")")
    private WebElement backpack;

    public CatalogScreen(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public boolean isLoaded() {
        return isDisplayed(productsScreen);
    }

    public MenuScreen openMenu() {
        tap(openMenuButton);
        return new MenuScreen(driver, timeout);
    }

    public ProductScreen openBackpack() {
        tap(backpack);
        return new ProductScreen(driver, timeout);
    }
}
