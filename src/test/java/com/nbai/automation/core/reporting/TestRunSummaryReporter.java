package com.nbai.automation.core.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbai.automation.core.config.ConfigLoader;
import com.nbai.automation.core.json.JsonMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.xml.XmlSuite;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class TestRunSummaryReporter implements IReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunSummaryReporter.class);

    private final ObjectMapper objectMapper = JsonMapperFactory.create();
    private final TestRunPublisher publisher = new NoOpTestRunPublisher();

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        int passed = 0;
        int failed = 0;
        int skipped = 0;

        for (ISuite suite : suites) {
            for (ISuiteResult result : suite.getResults().values()) {
                passed += result.getTestContext().getPassedTests().size();
                failed += result.getTestContext().getFailedTests().size();
                skipped += result.getTestContext().getSkippedTests().size();
            }
        }

        TestRunSummary summary = new TestRunSummary(
                runId(),
                ConfigLoader.load().environment(),
                Instant.now(),
                passed,
                failed,
                skipped);
        writeSummary(summary);
        publisher.publish(summary);
    }

    private String runId() {
        String configured = System.getProperty("test.runId");
        if (configured == null || configured.isBlank()) {
            configured = System.getenv("CI_RUN_ID");
        }
        return configured == null || configured.isBlank() ? UUID.randomUUID().toString() : configured;
    }

    private void writeSummary(TestRunSummary summary) {
        Path output = Path.of("target", "test-run-summary.json");
        try {
            Files.createDirectories(output.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), summary);
            LOGGER.info("Wrote local test-run summary to {}", output);
        } catch (Exception exception) {
            LOGGER.error("Unable to write local test-run summary", exception);
        }
    }
}

