package com.nbai.automation.core.http.filter;

import com.nbai.automation.core.json.JsonMapperFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public final class SensitiveDataSanitizerTest {

    private final SensitiveDataSanitizer sanitizer = new SensitiveDataSanitizer(JsonMapperFactory.create());

    @Test(groups = "framework")
    public void redactsSensitiveHeadersAndJsonFields() {
        Map<String, String> headers = sanitizer.sanitizeHeaders(Map.of(
                "Authorization", "sensitive-value",
                "Accept", "application/json",
                "X-Unreviewed-Header", "sensitive-value"));
        String body = sanitizer.sanitizeBody("{\"username\":\"user\",\"password\":\"sensitive-value\",\"nested\":{\"token\":\"sensitive-value\"}}");
        String uri = sanitizer.sanitizeUri("https://example.invalid/resource?token=sensitive-value&safe=value");

        Assert.assertEquals(headers.get("Authorization"), "<redacted>");
        Assert.assertEquals(headers.get("Accept"), "application/json");
        Assert.assertEquals(headers.get("X-Unreviewed-Header"), "<omitted>");
        Assert.assertFalse(body.contains("sensitive-value"));
        Assert.assertTrue(body.contains("<redacted>"));
        Assert.assertFalse(uri.contains("sensitive-value"));
    }
}
