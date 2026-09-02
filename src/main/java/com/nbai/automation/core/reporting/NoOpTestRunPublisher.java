package com.nbai.automation.core.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NoOpTestRunPublisher implements TestRunPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpTestRunPublisher.class);

    @Override
    public void publish(TestRunSummary summary) {
        LOGGER.info("External result publishing is disabled; local summary retained for run {}", summary.runId());
    }
}
