---
name: plan-mobile-automation
description: Explore an authorized mobile app with Appium MCP, preserve sanitized screen and locator evidence, and write approval-ready manual mobile test cases before Appium automation is implemented.
---

# Plan Mobile Automation

Produce evidence-backed mobile cases; do not implement or modify automation code.

## Discover with Appium MCP

1. Read `AGENTS.md`, `README.md`, the assignment, existing mobile tests/screens, and relevant evidence.
2. Confirm the platform, app artifact/package, authorized emulator or device, and required clean-state behavior. Do not operate an unrelated device or account.
3. For a local device, use Appium MCP device selection before creating an isolated session. Use a remote URL only when the caller explicitly provides one. Always delete the MCP session after exploration.
4. Start from deterministic app data. Prefer a new session with `noReset=false`; otherwise use the app's documented reset mechanism and record it.
5. Capture a screenshot and focused hierarchy observation at every new screen or material state. Use page source, attributes, and element text to prove locators; do not infer selectors from appearance alone.
6. Prefer accessibility ID, then resource ID, Android UIAutomator/iOS native strategies, and XPath only when no stable alternative exists. Use MCP `scroll_to_element` for off-screen targets.
7. Exercise each proposed case once, including setup, actions, assertions, resulting state, and cleanup/reset. Record errors and compatibility dialogs rather than silently dismissing them.

Read [references/evidence-format.md](references/evidence-format.md) before writing evidence. Evidence belongs under `docs/mobile-evidence/<app-slug>/`; never overwrite an earlier observation. Do not store passwords, tokens, personal data, notification contents, clipboard contents, or unsanitized screenshots/page source.

## Design and deliver cases

- Keep every test independent even though the Android suite runs serially. Do not use TestNG dependencies or priority.
- State device/app prerequisites, clean-state setup, every gesture/input, exact observable result, and post-test reset.
- Distinguish requirements, live observations, and assumptions. An observation on one device/API/app build is not a universal contract.
- Keep coverage proportional to the request and avoid duplicate UI paths.
- Write RFC 4180 CSV under `src/test/resources/testcases` with exactly `Test Case Name`, `Steps`, and `Expectation` unless the caller requests more columns.
- Map every retained case to one successful evidence record. Do not write a verified expectation when MCP execution was incomplete or failed.
- Never overwrite an existing CSV without explicit approval and never modify Java, suite XML, runtime configuration, or unrelated documentation.

Return the CSV and evidence paths, verified/unverified case counts, device/app metadata, and material gaps. Request approval before `$write-appium-tests` implements the plan.
