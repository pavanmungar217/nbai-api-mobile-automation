---
name: review-automation
description: Perform a read-only review of Java API automation for correctness, contract coverage, architecture, parallel safety, cleanup, secret handling, reporting, and maintainability.
---

# Review Automation

Lead with actionable findings ordered by severity; do not implement fixes unless asked.

Trace each changed test through data creation, service, client/filter, assertion, cleanup, and report
output. Check that:

- Assertions prove response status, schema where useful, and semantic or round-trip behavior.
- Negative tests validate confirmed behavior.
- Tests own their data and have no order, thread, or retry dependence.
- Services do not assert and tests do not construct transport clients directly.
- Configuration, logs, Allure attachments, and CI cannot expose secrets.
- External publishing is post-suite, idempotent, and explicitly enabled.
- Patterns solve a current variation or construction problem rather than adding ceremony.

Run non-mutating static rules, compilation, test discovery, and framework checks. Do not run live
state-changing tests unless execution is requested.

