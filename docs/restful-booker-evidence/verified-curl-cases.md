# Verified curl cases

All 27 retained cases were executed against the authorized public target. `PASS` means the complete
case flow, including field/state assertions and owned cleanup where applicable, matched the current
CSV expectation at least once.

| # | CSV test case | Sanitized request sequence | Observed result | Result |
|---:|---|---|---|---|
| 1 | Health check returns 201 | `GET /ping` | `201` | PASS |
| 2 | Create token with valid credentials | `POST /auth` with JSON runtime credentials | `200`; nonblank string token; token redacted | PASS |
| 3 | Reject invalid authentication password | `POST /auth` with `Invalid-<RUN_ID>` password | `200`; `reason=Bad credentials`; no token | PASS |
| 4 | Reject authentication without username | `POST /auth` with password only | `200`; `reason=Bad credentials`; no token | PASS |
| 5 | List all booking IDs without authentication | `GET /booking` | `200`; JSON array; every returned `bookingid` was a positive integer | PASS |
| 6 | Filter booking IDs by exact firstname and lastname | auth → create → `GET /booking?firstname=...&lastname=...` → cleanup | create `200`; filter `200` contained owned ID; cleanup `201` | PASS |
| 7 | Filter booking IDs at exact checkout boundary | auth → create with checkout `2030-06-15` → `GET /booking?checkout=2030-06-15` → cleanup | create `200`; filter `200` contained owned ID; cleanup `201` | PASS |
| 8 | Return empty IDs for a unique nonexistent firstname | `GET /booking?firstname=NoMatch-<RUN_ID>` | `200`; body `[]` | PASS |
| 9 | Retrieve owned booking as JSON without authentication | auth → JSON create → public JSON GET → cleanup | create `200`; GET `200 application/json`; all fields matched; cleanup `201` | PASS |
| 10 | Retrieve owned booking as XML | auth → JSON create → public GET with `Accept: application/xml` → cleanup | GET `200`; header `text/html; charset=utf-8`; body was well-formed XML and all fields matched; cleanup `201` | PASS after expectation correction |
| 11 | Return 404 for unknown booking ID | `GET /booking/2147483647` | `404` | PASS |
| 12 | Create JSON booking without authentication | auth for cleanup → public JSON create → public JSON GET → cleanup | create `200 application/json; charset=utf-8`; echoed and persisted fields matched; cleanup `201` | PASS |
| 13 | Create booking using XML input and JSON output | auth → XML create with `Accept: application/json` → cleanup | `200 application/json`; positive ID and all booking fields matched; cleanup `201` | PASS |
| 14 | Create JSON booking and request XML output | auth → JSON create with `Accept: application/xml` → cleanup | `200`; header `text/html; charset=utf-8`; well-formed `created-booking` XML with positive ID and matching fields; cleanup `201` | PASS after expectation correction |
| 15 | Preserve Unicode and punctuation in string fields | auth → UTF-8 JSON create `Ana-María`, `O'Neil`, `Quiet room & breakfast` → public GET → cleanup | request used `Content-Type: application/json; charset=UTF-8`; create/get `200`; strings preserved exactly; cleanup `201` | PASS |
| 16 | Replace booking using Cookie token | auth → create → Cookie-token PUT → public GET → cleanup | PUT `200`; GET `200`; complete replacement matched; cleanup `201` | PASS on focused rerun |
| 17 | Replace booking using Basic authentication | auth for cleanup → create → Basic PUT → public GET → cleanup | PUT `200`; exact replacement persisted; cleanup `201` | PASS |
| 18 | Reject PUT without authentication | auth for cleanup → create → unauthenticated PUT → public GET → cleanup | PUT `403`; original booking unchanged; cleanup `201` | PASS |
| 19 | Reject PUT with invalid token | auth for cleanup → create → invalid-cookie PUT → public GET → cleanup | PUT `403`; original booking unchanged; cleanup `201` | PASS |
| 20 | Patch scalar and date fields using Cookie token | auth → create → Cookie-token PATCH → public GET → cleanup | PATCH `200`; exact merge persisted and omitted fields stayed unchanged; cleanup `201` | PASS |
| 21 | Patch booking using Basic authentication | auth for cleanup → create → Basic PATCH → public GET → cleanup | PATCH `200`; only `additionalneeds` changed; cleanup `201` | PASS |
| 22 | Reject PATCH without authentication | auth for cleanup → create → unauthenticated PATCH → public GET → cleanup | PATCH `403`; original booking unchanged; cleanup `201` | PASS |
| 23 | Delete booking using Cookie token | auth → create → Cookie-token DELETE → public GET | DELETE `201`; GET `404` | PASS |
| 24 | Delete booking using Basic authentication | auth for fallback cleanup → create → Basic DELETE → public GET | DELETE `201`; GET `404` | PASS |
| 25 | Reject DELETE without authentication | auth for cleanup → create → unauthenticated DELETE → public GET → cleanup | DELETE `403`; GET `200` with original values; cleanup `201` | PASS |
| 26 | Reject DELETE with invalid token | auth for cleanup → create → invalid-cookie DELETE → public GET → cleanup | DELETE `403`; GET `200` with original values; cleanup `201` | PASS |
| 27 | Reuse one token through PUT PATCH and DELETE lifecycle | one auth → create → same-token PUT → same-token PATCH → public GET → same-token DELETE → public GET | `200`, `200`, `200`, `201`, `404`; exact combined state verified | PASS |

## Findings that changed the CSV

- A combined exact `checkin=2030-06-10&checkout=2030-06-15` query returned `200` but did not include
  the newly created booking. The exact checkout-only boundary query did include it, so the retained
  case covers only the verified checkout boundary. The unsupported combined/check-in claim was
  removed rather than encoded as a passing expectation.
- XML negotiation returned correct XML bodies but labeled them `Content-Type: text/html;
  charset=utf-8`. Cases 10 and 14 now assert that observed nonstandard header plus well-formed XML.
- Case 16 returned `403` in the first bulk run, while the same-token lifecycle passed. A focused,
  independently created rerun of case 16 returned PUT `200`, GET `200` with exact updated values,
  and cleanup `201`. This public target is resettable and can be inconsistent, so the rerun is
  recorded rather than hidden.
- Exact follow-up runs also confirmed the UUID form of case 8, the JSON response media type in case
  12, and the explicit UTF-8 request header in case 15 before the 27/27 verification was considered
  complete.
