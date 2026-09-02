package com.nbai.automation.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

public final class ConfigLoader {

    private static final String DEFAULT_ENVIRONMENT = "qa";
    private static final Pattern SAFE_ENVIRONMENT_NAME = Pattern.compile("[A-Za-z0-9_-]+");

    private ConfigLoader() {
    }

    public static FrameworkConfig load() {
        String environment = firstNonBlank(
                System.getProperty("test.env"),
                System.getenv("TEST_ENV"),
                DEFAULT_ENVIRONMENT);

        if (!SAFE_ENVIRONMENT_NAME.matcher(environment).matches()) {
            throw new ConfigurationException("Environment contains unsupported characters: " + environment);
        }

        Properties properties = new Properties();
        loadRequired(properties, "/config/default.properties");
        loadRequired(properties, "/config/" + environment + ".properties");

        String baseUri = required(resolve(
                "api.baseUri", "API_BASE_URI", properties), "api.baseUri");
        long connectTimeoutMs = positiveLong(resolve(
                "api.connectTimeoutMs", "API_CONNECT_TIMEOUT_MS", properties), "api.connectTimeoutMs");
        long readTimeoutMs = positiveLong(resolve(
                "api.readTimeoutMs", "API_READ_TIMEOUT_MS", properties), "api.readTimeoutMs");

        String username = resolve("booker.username", "BOOKER_USERNAME", properties);
        String password = resolve("booker.password", "BOOKER_PASSWORD", properties);
        Optional<Credentials> credentials = credentials(username, password);

        try {
            return new FrameworkConfig(
                    environment,
                    URI.create(baseUri),
                    Duration.ofMillis(connectTimeoutMs),
                    Duration.ofMillis(readTimeoutMs),
                    credentials);
        } catch (IllegalArgumentException exception) {
            throw new ConfigurationException("Invalid framework configuration", exception);
        }
    }

    private static Optional<Credentials> credentials(String username, String password) {
        boolean hasUsername = username != null && !username.isBlank();
        boolean hasPassword = password != null && !password.isBlank();
        if (hasUsername != hasPassword) {
            throw new ConfigurationException("BOOKER_USERNAME and BOOKER_PASSWORD must be supplied together");
        }
        return hasUsername ? Optional.of(new Credentials(username, password)) : Optional.empty();
    }

    private static String resolve(String systemProperty, String environmentVariable, Properties properties) {
        return firstNonBlank(
                System.getProperty(systemProperty),
                System.getenv(environmentVariable),
                properties.getProperty(systemProperty));
    }

    private static void loadRequired(Properties target, String resourcePath) {
        try (InputStream stream = ConfigLoader.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new ConfigurationException("Missing configuration resource: " + resourcePath);
            }
            target.load(stream);
        } catch (IOException exception) {
            throw new ConfigurationException("Unable to load configuration resource: " + resourcePath, exception);
        }
    }

    private static String required(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new ConfigurationException("Missing required configuration: " + key);
        }
        return value.trim();
    }

    private static long positiveLong(String value, String key) {
        String requiredValue = required(value, key);
        try {
            long parsed = Long.parseLong(requiredValue);
            if (parsed <= 0) {
                throw new NumberFormatException("not positive");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new ConfigurationException(key + " must be a positive whole number", exception);
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}

