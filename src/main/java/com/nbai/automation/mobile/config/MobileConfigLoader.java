package com.nbai.automation.mobile.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

public final class MobileConfigLoader {

    private static final String DEFAULT_APK = "apps/Android-MyDemoAppRN.1.3.0.build-244.apk";
    private static final String DEFAULT_DEVICE_NAME = "Android Emulator";
    private static final long DEFAULT_WAIT_SECONDS = 15;

    private MobileConfigLoader() {
    }

    public static MobileConfig load() {
        Properties defaults = loadDefaults();
        URI serverUri = URI.create(required(resolve(
                "appium.serverUrl", "APPIUM_SERVER_URL", defaults.getProperty("appium.serverUrl")),
                "appium.serverUrl"));
        Path appPath = Path.of(resolve("android.app", "ANDROID_APP", DEFAULT_APK))
                .toAbsolutePath()
                .normalize();
        String deviceName = resolve("android.deviceName", "ANDROID_DEVICE_NAME", DEFAULT_DEVICE_NAME);
        String udid = resolve("android.udid", "ANDROID_UDID", null);
        long waitSeconds = positiveLong(resolve(
                "mobile.waitSeconds", "MOBILE_WAIT_SECONDS", Long.toString(DEFAULT_WAIT_SECONDS)),
                "mobile.waitSeconds");

        return new MobileConfig(
                serverUri,
                appPath,
                deviceName,
                Optional.ofNullable(udid),
                Duration.ofSeconds(waitSeconds));
    }

    private static Properties loadDefaults() {
        Properties properties = new Properties();
        try (InputStream stream = MobileConfigLoader.class.getResourceAsStream("/config/mobile.properties")) {
            if (stream == null) {
                throw new IllegalArgumentException("Missing configuration resource: /config/mobile.properties");
            }
            properties.load(stream);
            return properties;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to load mobile configuration", exception);
        }
    }

    private static String required(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required mobile configuration: " + key);
        }
        return value.trim();
    }

    private static String resolve(String systemProperty, String environmentVariable, String defaultValue) {
        String systemValue = System.getProperty(systemProperty);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return defaultValue;
    }

    private static long positiveLong(String value, String key) {
        try {
            long parsed = Long.parseLong(value);
            if (parsed <= 0) {
                throw new NumberFormatException("not positive");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(key + " must be a positive whole number", exception);
        }
    }
}
