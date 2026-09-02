---
name: review-mobile-automation
description: Perform a read-only review of Java Appium automation for evidence coverage, locator stability, Page Object boundaries, serial TestNG safety, state isolation, reporting, and maintainability.
---

# Review Mobile Automation

Lead with actionable findings ordered by severity; do not edit files.

Read the approved mobile CSV and every mapped `docs/mobile-evidence` record. Trace each test through driver configuration, lifecycle, screen actions, waits, assertions, failure evidence, and reset/teardown. Check that:

- Every approved case has exactly one independent automated counterpart and no extra invented behavior.
- Accessibility/resource/native selectors match recorded Appium MCP evidence; XPath and scroll behavior have a documented justification.
- Screens encapsulate interactions, tests own business assertions, and PageFactory is used without static/global drivers.
- Tests start from deterministic data, run alone and in any order, and do not depend on serial ordering even though the suite is one-threaded.
- Waits are state-based; failures are not hidden by sleeps, retries, skips, fallback locators, oversized timeouts, or weak assertions.
- Configuration is externalized, teardown always quits the owned session, and screenshots/logs/Allure cannot expose sensitive input or unrelated device content.
- The suite XML explicitly selects only mobile tests with `parallel=false` and one thread.

Run non-mutating static checks, compilation, and test discovery. When live verification is requested and the authorized emulator is available, Appium MCP may create a clean isolated session to confirm disputed locators or states; record what was observed and delete the session. Report findings with file/line, impact, evidence reference, concrete correction, and any unverified runtime assumptions.
