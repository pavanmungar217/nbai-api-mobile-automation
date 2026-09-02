package com.nbai.automation.core.config;

import java.util.Objects;

public record Credentials(String username, String password) {

    public Credentials {
        if (isBlank(username) || isBlank(password)) {
            throw new ConfigurationException("Both API username and password must be provided");
        }
    }

    @Override
    public String toString() {
        return "Credentials[username=<redacted>, password=<redacted>]";
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.isBlank();
    }
}

