---
name: write-restassured-tests
description: Implement approved REST Assured API coverage in this Java framework through models, services, TestNG tests, schemas, unique data, cleanup, and Allure evidence.
---

# Write REST Assured Tests

Use only after API scope is approved.

1. Read `AGENTS.md`, `README.md`, the approved plan, and nearby endpoint/service/test code.
2. Reuse `ApiClient`, fresh request specifications, the service factory, sanitizer filters, and test lifecycle.
3. Add relative endpoint paths, typed request/response models, one service operation per API operation, then tests.
4. Keep transport behavior in services and observable business assertions in tests or assertion helpers.
5. Generate unique data per method and clean up only owned entities in a `finally` path.
6. Cover confirmed success, contract, authorization, and negative behavior without guessing.
7. Add real traceability metadata only when identifiers are supplied.

Never change global REST Assured state, use order dependencies, log secrets, retry mutations,
or call external result systems from tests. Validate static rules, framework checks, compilation,
and the narrowest relevant live tests.

