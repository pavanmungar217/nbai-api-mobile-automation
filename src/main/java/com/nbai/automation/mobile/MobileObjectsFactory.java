package com.nbai.automation.mobile;

import com.nbai.automation.mobile.config.MobileConfig;
import com.nbai.automation.mobile.config.MobileConfigLoader;
import com.nbai.automation.mobile.config.MobileCredentialFactory;
import com.nbai.automation.mobile.driver.AndroidDriverFactory;
import com.nbai.automation.mobile.screen.CatalogScreen;
import io.appium.java_client.android.AndroidDriver;

public final class MobileObjectsFactory {

    private MobileObjectsFactory() {
    }

    public static MobileObjects create() {
        MobileConfig config = MobileConfigLoader.load();
        AndroidDriver driver = AndroidDriverFactory.create(config);
        return new MobileObjects(
                config,
                driver,
                new MobileCredentialFactory(),
                new CatalogScreen(driver, config.explicitWait()));
    }
}
