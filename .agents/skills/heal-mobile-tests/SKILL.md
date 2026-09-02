---
name: heal-mobile-tests
description: Reproduce and repair Java Appium/TestNG failures using run artifacts and Appium MCP runtime evidence without masking product defects or weakening verified behavior.
---

# Heal Mobile Tests

Diagnose from evidence before changing code. This is a repair workflow, not runtime self-healing.

1. Read `AGENTS.md`, `README.md`, the mapped mobile CSV/evidence, failing screen/test code, Surefire output, Allure attachments, sanitized logs, and local summary.
2. Reproduce the narrowest failing method serially with the same app build, device/API, Appium server, capabilities, and initial state.
3. Classify the failure: automation defect, stale/incorrect locator evidence, app behavior change, dirty persisted state, wait/animation/keyboard issue, Appium or UiAutomator2 mismatch, server/device configuration, API-level compatibility, or product defect.
4. When runtime state is uncertain, create a separate clean Appium MCP session after the Java session ends. Capture a screenshot, focused hierarchy, attributes, exact steps, and state transition; delete the session afterward.
5. If current MCP behavior conflicts with approved evidence, do not silently bless the new behavior. Append a sanitized UTC-stamped record under `docs/mobile-evidence/<app-slug>/conflicts/` with the case, environment, old evidence, current observation, repetitions, reset outcome, classification, and recommended owner/action.
6. When evidence proves an automation defect, apply only the smallest responsible change to the screen, wait, driver/config seam, test, or test support. Preserve the exact behavior assertion and framework boundaries.

Never add fixed sleeps, blind retries, catch-all locator chains, coordinate taps, method dependencies, priorities, skips, inflated global timeouts, or weaker assertions merely to make a run green. Do not treat a changed locator as a product defect, or a real product defect as a locator issue. Do not modify the approved CSV/evidence baseline unless the caller explicitly authorizes replanning.

After repair, rerun the focused method, relevant local tests, compilation, `scripts/check-framework.sh`, and the serial mobile suite when the authorized emulator is available. Inspect teardown, screenshots, Surefire, Allure, logs, and the local summary. Report root cause, runtime evidence/conflict path, changed files, and remaining failures. Do not commit, push, or publish externally.
