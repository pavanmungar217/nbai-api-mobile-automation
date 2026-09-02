---
name: framework-architecture
description: Create or change API/mobile framework boundaries, configuration, clients, services, factories, REST Assured filters, logging, reporting, and extension points without over-abstraction.
---

# Framework Architecture

Maintain one repository with independent API and future mobile adapters sharing a small core.

- `core` owns typed configuration, JSON, logging support, reporting contracts, and transport primitives.
- API services own endpoint calls but not business assertions.
- Future mobile screens own Appium interactions but must not reuse or depend on the API client.
- Prefer composition and typed construction over deep base classes or global service locators.
- Add interfaces only at real variation boundaries, such as API transport or test-run publishing.
- Use factories where construction enforces fresh or valid objects: request specifications, services, and test data.
- Use REST Assured filters as the decorator chain for correlation, sanitization, logging, and Allure evidence.
- Keep decorator order explicit and sanitize before any persisted output.
- Keep configuration immutable with system property, environment variable, non-secret file, then default precedence.

Avoid speculative abstractions, duplicate wrappers around REST Assured, mobile placeholders, and
production integrations that have no approved target or credentials.

