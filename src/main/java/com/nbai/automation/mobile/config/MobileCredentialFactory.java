package com.nbai.automation.mobile.config;

public final class MobileCredentialFactory {

    public MobileCredential valid() {
        return create(
                "mobile.validUsername",
                "MOBILE_VALID_USERNAME",
                "mobile.validPassword",
                "MOBILE_VALID_PASSWORD");
    }

    public MobileCredential invalid() {
        return create(
                "mobile.invalidUsername",
                "MOBILE_INVALID_USERNAME",
                "mobile.invalidPassword",
                "MOBILE_INVALID_PASSWORD");
    }

    private MobileCredential create(
            String usernameProperty,
            String usernameEnvironment,
            String passwordProperty,
            String passwordEnvironment) {
        String username = required(resolve(usernameProperty, usernameEnvironment), usernameProperty);
        String password = required(resolve(passwordProperty, passwordEnvironment), passwordProperty);
        return new MobileCredential(username, password);
    }

    private String resolve(String systemProperty, String environmentVariable) {
        String systemValue = System.getProperty(systemProperty);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return null;
    }

    private String required(String value, String key) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required mobile credential configuration: " + key);
        }
        return value;
    }
}
