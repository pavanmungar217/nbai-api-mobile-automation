package com.nbai.automation.mobile;

import com.nbai.automation.mobile.config.MobileConfig;
import com.nbai.automation.mobile.config.MobileCredentialFactory;
import com.nbai.automation.mobile.screen.CatalogScreen;
import io.appium.java_client.android.AndroidDriver;

import java.util.Objects;

public final class MobileObjects {

    private final MobileConfig config;
    private final AndroidDriver driver;
    private final MobileCredentialFactory credentials;
    private final CatalogScreen catalog;

    MobileObjects(
            MobileConfig config,
            AndroidDriver driver,
            MobileCredentialFactory credentials,
            CatalogScreen catalog) {
        this.config = Objects.requireNonNull(config);
        this.driver = Objects.requireNonNull(driver);
        this.credentials = Objects.requireNonNull(credentials);
        this.catalog = Objects.requireNonNull(catalog);
    }

    public MobileConfig config() {
        return config;
    }

    public AndroidDriver driver() {
        return driver;
    }

    public MobileCredentialFactory credentials() {
        return credentials;
    }

    public CatalogScreen catalog() {
        return catalog;
    }
}
