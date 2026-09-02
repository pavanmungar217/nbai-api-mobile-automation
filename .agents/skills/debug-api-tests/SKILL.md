---
name: debug-api-tests
description: Diagnose reproduced REST Assured or TestNG failures using Surefire, Allure, sanitized logs, configuration, and controlled sequential or parallel reruns.
---

# Debug API Tests

Diagnose before changing behavior.

1. Reproduce the narrowest failing method with the same environment and group selection.
2. Inspect Surefire XML/text, Allure attachments, `target/logs/automation.log`, and the local run summary without exposing secrets.
3. Classify the failure as contract/assertion, data collision, cleanup, configuration, authentication, transport, public-environment reset, or framework defect.
4. Rerun once with one thread when concurrency is a plausible cause; compare rather than assuming.
5. Fix the root cause only when requested, then rerun the narrow test and relevant framework checks.

Do not add sleeps, broad retries, method dependencies, larger timeouts, skips, or weaker assertions
without evidence. Never retry POST, PUT, PATCH, or DELETE automatically.

