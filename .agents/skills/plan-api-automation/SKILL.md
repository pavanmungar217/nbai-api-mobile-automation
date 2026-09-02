---
name: plan-api-automation
description: Discover REST API behavior from supplied documents, specifications, code, and authorized curl probes, then write detailed evidence-backed API test-case CSV files under src/test/resources/testcases before automation code is written.
---

# Plan API Automation

Produce approval-ready API test cases; do not implement automation code.

## Gather evidence

1. Read `README.md`, `AGENTS.md`, the assignment or story, and every relevant supplied source. Sources may include PDFs, OpenAPI/Swagger files, Postman collections, API documentation, tickets, sample requests, existing services/tests, schemas, logs, or plain-language requirements.
2. Treat an authoritative contract and repeatable observations as evidence. Treat examples, existing tests, and prose that conflict with the contract as questions to resolve.
3. Inspect nearby services, models, tests, schemas, environment configuration, and result hooks when working inside an existing repository.
4. Record which behaviors are documented, which were observed, and which remain unknown. Never turn an assumption into an exact expected result.

## Explore with curl

Use terminal `curl` calls when the caller has supplied or selected an authorized test target and live exploration will materially improve the cases.

- Resolve the base URL from supplied documentation or configuration; do not discover unrelated hosts or broaden the target.
- Start with the narrowest non-mutating request. Use explicit connect and total timeouts, low request volume, and ordinary supported inputs. Do not fuzz or load test.
- Do not use `curl --verbose`, shell tracing, or commands that print credentials, cookies, authorization headers, or secret-bearing URLs.
- Read credentials from the approved runtime secret source. Use placeholders such as `<AUTH_TOKEN>` and sanitized example values in all notes and CSV output.
- POST, PUT, PATCH, DELETE, or any operation that can change external state requires explicit authorization for that target. Create uniquely identifiable data, capture its identifier, and clean up only that owned data in the same exploration when cleanup is supported.
- Never probe production, destructive administrative operations, billing actions, broad deletes, or unknown mutation semantics without explicit authorization.
- Capture the method, sanitized URL, request shape, response status, relevant headers, response shape, and any stable behavioral observation. A transient or one-off response is not automatically a contract.
- If the target, authentication, network access, or mutation authority is unavailable, continue from documentary evidence and clearly leave unverified behavior out of executable expectations.

## Design the cases

- Cover supported positive, negative, authentication/authorization, validation, contract/schema, filtering/pagination, state-transition, and error-handling behavior in proportion to the evidence and risk.
- Make every case independent. Include prerequisites, unique data, captured identifiers, and owned cleanup in its steps.
- For every request, state the HTTP method, URL or path, required non-secret headers, query/path parameters, and a concrete sanitized payload example when a body is used.
- Include explicit verification steps for the status, required headers, schema/shape, important field values, and persisted state when the contract supports them.
- Do not invent case IDs, status codes, negative behavior, credentials, server capabilities, or cleanup endpoints.
- Avoid duplicate cases that exercise the same behavior without adding risk coverage.
- Ensure proposed cases can run independently and safely under the framework's configured TestNG parallelism. Do not rely on execution order or shared mutable identifiers.

## Deliver CSV

Read and follow [references/csv-test-case-format.md](references/csv-test-case-format.md). Write every generated CSV under `src/test/resources/testcases` using a descriptive kebab-case filename ending in `.csv`. If the caller supplies only a filename, resolve it inside that directory. Do not write generated plans elsewhere, overwrite an existing CSV without explicit approval, or modify implementation code.

Return the created file path, then report only material evidence gaps or operations that were not probed due to missing authorization. Request agreement before `$write-restassured-tests` changes code.
