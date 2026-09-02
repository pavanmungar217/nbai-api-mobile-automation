package com.nbai.automation.mobile.config;

public final class MobileCredential {

    private final String username;
    private final String password;

    MobileCredential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return "MobileCredential[username=<redacted>, password=<redacted>]";
    }
}
