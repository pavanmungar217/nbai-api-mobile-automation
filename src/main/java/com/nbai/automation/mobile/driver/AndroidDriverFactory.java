package com.nbai.automation.mobile.driver;

import com.nbai.automation.mobile.config.MobileConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.time.Duration;

public final class AndroidDriverFactory {

    private AndroidDriverFactory() {
    }

    public static AndroidDriver create(MobileConfig config) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(config.deviceName())
                .setApp(config.appPath().toString())
                .setAppPackage("com.saucelabs.mydemoapp.rn")
                .setAutoGrantPermissions(true)
                .setNoReset(false)
                .setFullReset(true)
                .setNewCommandTimeout(Duration.ofSeconds(60));
        config.udid().ifPresent(options::setUdid);

        try {
            return new AndroidDriver(config.serverUri().toURL(), options);
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException("Invalid Appium server URL: " + config.serverUri(), exception);
        }
    }
}
