# Restful Booker curl evidence

This folder contains sanitized evidence used to design the manual API test cases in
`src/test/resources/testcases/restful-booker-api-test-plan.csv`.

## Evidence rules

- Target: `https://restful-booker.herokuapp.com`
- Observation date: 2026-09-03 Asia/Kolkata
- Every retained CSV case was executed with `curl` at least once.
- Every stateful case acquired its own token, created a uniquely named booking, captured its own
  booking ID, and deleted only that booking.
- Tokens, credentials, cookies, Authorization values, and raw secret-bearing authentication
  responses are not stored here.
- `<RUN_ID>`, `<TOKEN>`, `<BOOKING_ID>`, `<USERNAME>`, `<PASSWORD>`, and `<BASIC_VALUE>` are sanitized
  placeholders.
- These are observations from a resettable public playground, not guarantees that the service will
  never change. Re-probe before changing exact automation expectations.

See [verified-curl-cases.md](verified-curl-cases.md) for the case-to-result mapping and
[sanitized-contract-samples.md](sanitized-contract-samples.md) for reusable request/response shapes.

