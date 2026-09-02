package com.nbai.automation.core.reporting;

import java.time.Instant;

public record TestRunSummary(
        String runId,
        String environment,
        Instant completedAt,
        int passed,
        int failed,
        int skipped) {
}

