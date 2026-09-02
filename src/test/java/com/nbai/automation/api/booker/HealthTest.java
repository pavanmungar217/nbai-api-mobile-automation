package com.nbai.automation.api.booker;

import com.nbai.automation.core.http.HttpStatus;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Restful Booker API")
@Feature("Health")
public final class HealthTest extends RestfulBookerTestSupport {

    @Test(groups = {"api", "smoke"}, description = "Health check returns 201")
    public void healthCheckReturns201() {
        Assert.assertEquals(services.health().ping().statusCode(), HttpStatus.CREATED);
    }
}
