package com.nbai.automation.core.reporting;

import com.nbai.automation.core.config.ConfigLoader;
import com.nbai.automation.core.config.FrameworkConfig;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.IExecutionListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class FrameworkTestListener implements ITestListener, IExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkTestListener.class);

    @Override
    public void onExecutionStart() {
        FrameworkConfig config = ConfigLoader.load();
        LOGGER.info("Starting test execution with {}", config);
        writeAllureEnvironment(config);
    }

    @Override
    public void onTestStart(ITestResult result) {
        MDC.put("testName", qualifiedTestName(result));
        LOGGER.info("Test started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("Test passed");
        MDC.clear();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("Test failed", result.getThrowable());
        attachFailure(result.getThrowable());
        MDC.clear();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn("Test skipped: {}", result.getThrowable() == null ? "no reason supplied" : result.getThrowable().toString());
        MDC.clear();
    }

    private String qualifiedTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }

    private void attachFailure(Throwable throwable) {
        if (throwable == null || Allure.getLifecycle().getCurrentTestCaseOrStep().isEmpty()) {
            return;
        }
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        Allure.addAttachment("Failure stack trace", "text/plain", writer.toString(), ".txt");
    }

    private void writeAllureEnvironment(FrameworkConfig config) {
        Path resultsDirectory = Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
        Properties properties = new Properties();
        properties.setProperty("Environment", config.environment());
        properties.setProperty("API base URI", config.apiBaseUri().toString());
        properties.setProperty("Java", System.getProperty("java.version"));
        try {
            Files.createDirectories(resultsDirectory);
            try (var stream = Files.newOutputStream(resultsDirectory.resolve("environment.properties"))) {
                properties.store(stream, "Non-sensitive test environment");
            }
        } catch (Exception exception) {
            LOGGER.warn("Unable to write Allure environment properties: {}", exception.toString());
        }
    }
}

