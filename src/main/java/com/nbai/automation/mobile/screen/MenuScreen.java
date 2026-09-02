package com.nbai.automation.mobile.screen;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public final class MenuScreen extends BaseScreen {

    @AndroidFindBy(accessibility = "menu item catalog")
    private WebElement menu;

    @AndroidFindBy(accessibility = "menu item log in")
    private WebElement loginButton;

    public MenuScreen(AndroidDriver driver, Duration timeout) {
        super(driver, timeout);
    }

    public boolean isLoaded() {
        return isDisplayed(menu);
    }

    public LoginScreen openLogin() {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().description(\"menu item log in\"))"));
        tap(loginButton);
        return new LoginScreen(driver, timeout);
    }
}
