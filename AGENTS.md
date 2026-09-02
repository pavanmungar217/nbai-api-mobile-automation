# NBAI Automation Project Instructions

Read `README.md` before creating or changing automation.

Use the narrowest repository skill for the task:

- `$plan-api-automation` before implementing new endpoint coverage.
- `$write-restassured-tests` for approved API coverage.
- `$testng-conventions` when changing TestNG lifecycle, groups, data providers, or parallelism.
- `$framework-architecture` for client, service, configuration, factory, filter, or reporting changes.
- `$debug-api-tests` for reproduced failures.
- `$review-automation` for read-only audits.
- `$plan-mobile-automation` before implementing new mobile flows or locators.
- `$write-appium-tests` for approved Appium coverage.
- `$review-mobile-automation` for read-only mobile audits.
- `$heal-mobile-tests` for reproduced Appium/TestNG failures that require diagnosis and repair.

Keep shared code in `src/main/java/com/nbai/automation/core`, Restful Booker code in
`src/main/java/com/nbai/automation/api/booker`, executable tests and test support in
`src/test/java`, and non-secret environment files, schemas, and runtime configuration in
`src/test/resources`. Keep Appium driver/config/screens in `src/main/java/com/nbai/automation/mobile`
and executable mobile tests/support in `src/test/java/com/nbai/automation/mobile`; API code must not
depend on mobile code.

Framework invariants:

- `api-testng.xml` owns API test selection and parallel settings; Surefire launches that suite.
- API tests run alone, in any order, and in method-level parallel mode.
- Mobile tests run alone and in any order, while `android-testng.xml` executes them serially with one thread.
- Use unique test data and remove only data created by the current test.
- Do not use TestNG method dependencies or priority to impose order.
- Do not mutate global REST Assured state.
- Keep endpoints and transport calls in services; keep business assertions in tests or
  assertion helpers.
- Do not retry mutating requests. Any future GET retry requires bounded transient-failure
  evidence and must remain visible in logs and reports.
- Do not hardcode credentials, tokens, cookies, environment URLs, or sensitive payloads.
- Sanitize data before logging or attaching it to Allure.
- Do not invent case IDs, endpoint behavior, status codes, or live locators.
- Discover mobile behavior and locators with Appium MCP on an authorized test device. Preserve
  sanitized, append-only observations under `docs/mobile-evidence/<app-slug>` and prefer accessibility
  ID, resource ID, then platform-native locators; XPath is a documented last resort.
- The API planner may write only generated `.csv` files under `src/test/resources/testcases`; it
  must not overwrite an existing CSV without explicit approval.
- The mobile planner may write generated `.csv` files under `src/test/resources/testcases` and
  sanitized evidence under `docs/mobile-evidence`; it must not overwrite either without approval.
- External Jira, Xray, or Zephyr writes require explicit authorization and must run after
  the suite, never from parallel test methods.
- Do not commit, push, or create a pull request unless explicitly requested.

Before handoff, run `scripts/check-framework.sh`, the focused local framework tests, compilation,
and the narrowest relevant live test when credentials and network authorization are available.
