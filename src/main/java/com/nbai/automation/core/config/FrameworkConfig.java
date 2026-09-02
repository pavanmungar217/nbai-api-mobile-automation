package com.nbai.automation.core.config;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

public record FrameworkConfig(
        String environment,
        URI apiBaseUri,
        Duration connectTimeout,
        Duration readTimeout,
        Optional<Credentials> credentials) {

    public FrameworkConfig {
        if (environment == null || environment.isBlank()) {
            throw new ConfigurationException("Environment must not be blank");
        }
        if (apiBaseUri == null || !apiBaseUri.isAbsolute()) {
            throw new ConfigurationException("API base URI must be absolute");
        }
        if (connectTimeout == null || connectTimeout.isNegative() || connectTimeout.isZero()) {
            throw new ConfigurationException("Connect timeout must be positive");
        }
        if (readTimeout == null || readTimeout.isNegative() || readTimeout.isZero()) {
            throw new ConfigurationException("Read timeout must be positive");
        }
        credentials = credentials == null ? Optional.empty() : credentials;
    }

    public Credentials requireCredentials() {
        return credentials.orElseThrow(() -> new ConfigurationException(
                "API credentials are required. Set BOOKER_USERNAME and BOOKER_PASSWORD "
                        + "or the equivalent -Dbooker.username/-Dbooker.password properties."));
    }

    @Override
    public String toString() {
        return "FrameworkConfig[environment=" + environment
                + ", apiBaseUri=" + apiBaseUri
                + ", connectTimeout=" + connectTimeout
                + ", readTimeout=" + readTimeout
                + ", credentials=<redacted>]";
    }
}

