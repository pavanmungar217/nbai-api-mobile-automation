package com.nbai.automation.mobile;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

public abstract class BaseMobileTest {

    protected MobileObjects mobile;

    @BeforeMethod(alwaysRun = true)
    public void startAndroidSession() {
        mobile = MobileObjectsFactory.create();
        dismissAndroidCompatibilityDialogIfPresent();
        if (!mobile.catalog().isLoaded()) {
            throw new IllegalStateException("My Demo App did not reach the product catalog");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void stopAndroidSession(ITestResult result) {
        if (mobile == null) {
            return;
        }
        AndroidDriver driver = mobile.driver();
        try {
            if (!result.isSuccess()) {
                clearVisibleCredentialFields();
                byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(
                        "Android failure screenshot",
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png");
            }
        } catch (RuntimeException ignored) {
            // Session teardown must still run when evidence collection is unavailable.
        } finally {
            try {
                driver.quit();
            } catch (RuntimeException ignored) {
                // Preserve the original test result when the Appium session is already gone.
            }
        }
    }

    private void dismissAndroidCompatibilityDialogIfPresent() {
        new WebDriverWait(mobile.driver(), mobile.config().explicitWait()).until(currentDriver -> {
            try {
                var alert = currentDriver.switchTo().alert();
                if (alert.getText().contains("Android App Compatibility")) {
                    alert.dismiss();
                    return true;
                }
                return false;
            } catch (NoAlertPresentException ignored) {
                return !currentDriver.findElements(AppiumBy.accessibilityId("products screen")).isEmpty();
            }
        });
    }

    private void clearVisibleCredentialFields() {
        AndroidDriver driver = mobile.driver();
        clearIfPresent("Username input field");
        clearIfPresent("Password input field");
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (RuntimeException ignored) {
            // The screenshot is still safe once visible credential fields have been cleared.
        }
    }

    private void clearIfPresent(String accessibilityId) {
        for (WebElement element : mobile.driver().findElements(AppiumBy.accessibilityId(accessibilityId))) {
            if (element.isDisplayed()) {
                element.clear();
            }
        }
    }
}
