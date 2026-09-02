---
name: testng-conventions
description: Design or review TestNG lifecycle, grouping, data providers, listeners, reporters, and parallel execution for this framework.
---

# TestNG Conventions

Preserve method independence and low-thread parallel execution.

- Use `@BeforeClass` only for immutable or stateless framework objects; keep mutable test data local to each method.
- Use `@BeforeMethod` and `@AfterMethod` only when setup or cleanup genuinely applies to every method and remains thread-safe.
- Use groups for selection, not priority or method dependencies for sequencing.
- Keep DataProviders sequential unless their data and target system capacity are proven safe.
- Use `ITestListener` for lifecycle context and failure evidence; use `IReporter` only after suite completion for aggregation.
- Keep external publishing outside test threads and disabled locally.
- Do not use static mutable IDs, tokens, responses, request specifications, or drivers.
- Keep a one-thread profile for diagnosis; do not hide failures with TestNG retry analyzers.

When changing concurrency, run both one-thread and low-thread executions and inspect cleanup,
logs, Surefire XML, Allure results, and the local test-run summary.

