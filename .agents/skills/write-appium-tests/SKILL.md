---
name: write-appium-tests
description: Implement approved, Appium-MCP-verified mobile cases in this Java framework using Appium, TestNG, Page Object Model screens, PageFactory locators, explicit waits, and isolated device state.
---

# Write Appium Tests

Use only after mobile scope and its matching runtime evidence are approved.

1. Read `AGENTS.md`, `README.md`, the complete approved mobile CSV, matching `docs/mobile-evidence` records, and nearby driver/screen/test code.
2. Require a one-to-one mapping from each exact manual case name to one independent TestNG method or explicit sequential data-provider invocation. Put the case name in the TestNG description or Allure metadata.
3. Implement only behavior and locators proven by the evidence. If the CSV and evidence disagree, stop that case and return it to the planner. Appium MCP may confirm current runtime state, but implementation must not silently rewrite source-of-truth evidence.
4. Keep Appium configuration and driver construction in `src/main/java/com/nbai/automation/mobile/config` and `driver`; screens in `mobile/screen`; executable tests and support in `src/test/java/com/nbai/automation/mobile`.
5. Use Page Object Model screens with Appium PageFactory. Put UI actions and screen-state queries in screens; put business assertions in tests or focused assertion helpers.
6. Prefer accessibility ID, resource ID, and platform-native locators in that order. Do not add coordinate locators, copied absolute XPath, unproven fallback chains, or runtime AI/visual self-healing.
7. Use explicit state-based waits. Do not add fixed sleeps, blind retries, larger timeouts, TestNG dependencies, priorities, static drivers, or shared mutable test data.
8. Give every method its own driver lifecycle and deterministic clean state. Tests remain independent and the mobile TestNG XML remains explicitly serial (`parallel=false`, one thread).
9. Read paths, server URL, UDID, and device settings from runtime configuration. Never hardcode secrets or attach sensitive entered values/page source to logs or Allure. Capture a sanitized screenshot on failure when a session is available.

Before handoff, reconcile all approved case names, run `scripts/check-framework.sh`, compilation, focused local tests, and the narrowest mobile method against the authorized emulator/device when Appium is available. Then run the serial mobile suite and inspect Surefire, Allure, logs, screenshots, device reset, and the local summary. Do not commit, push, publish externally, or modify approved CSV/evidence without separate authorization.
