package com.nbai.automation.mobile.config;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;

public record MobileConfig(
        URI serverUri,
        Path appPath,
        String deviceName,
        Optional<String> udid,
        Duration explicitWait) {

    public MobileConfig {
        if (serverUri == null || !serverUri.isAbsolute()) {
            throw new IllegalArgumentException("Appium server URI must be absolute");
        }
        if (appPath == null || !Files.isRegularFile(appPath)) {
            throw new IllegalArgumentException(
                    "Android APK was not found at " + appPath + ". See docs/ANDROID_APPIUM.md");
        }
        if (deviceName == null || deviceName.isBlank()) {
            throw new IllegalArgumentException("Android device name must not be blank");
        }
        udid = udid == null ? Optional.empty() : udid.filter(value -> !value.isBlank());
        if (explicitWait == null || explicitWait.isZero() || explicitWait.isNegative()) {
            throw new IllegalArgumentException("Mobile explicit wait must be positive");
        }
    }
}
