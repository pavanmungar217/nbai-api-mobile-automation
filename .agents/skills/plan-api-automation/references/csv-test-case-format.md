# API Test Case CSV Contract

Produce RFC 4180-compatible CSV with exactly these columns and this header:

```csv
"Test Case Name","Steps","Expectation"
```

Formatting rules:

- Emit one test case per data row.
- Double-quote every cell. Escape a literal double quote inside a cell by doubling it.
- Keep all steps for a case in its single `Steps` cell. Use numbered steps separated by literal newlines inside the quoted cell.
- Do not add identifiers, priority, group, owner, automation status, comments, or other columns unless the caller explicitly requests them.
- Use sanitized concrete examples. Represent secrets with placeholders such as `<AUTH_TOKEN>` or `<SESSION_COOKIE>`.
- A JSON payload in a CSV cell must escape its quotes. For example, `{ "name": "API User" }` becomes `{ ""name"": ""API User"" }` in the CSV text.
- Keep expectations observable and exact only when supported by documented or live-verified evidence.

Each `Steps` cell should include, when applicable:

1. Preconditions and uniquely owned test-data setup.
2. HTTP method and complete non-secret example URL, or configured base URL plus path.
3. Required headers with secret values replaced by placeholders.
4. Concrete path/query values and a complete sanitized request payload example.
5. The instruction to send the request and capture the response.
6. Verification of status, relevant headers, response schema/shape, and important values.
7. Follow-up request needed to prove persistence or absence.
8. Cleanup of only the data created by this case.

Example structure only; replace the method, endpoint, data, and expectations with verified behavior:

```csv
"Test Case Name","Steps","Expectation"
"Create an entity with valid required fields","1. Prepare a unique name such as API-User-<UNIQUE_SUFFIX>.
2. Send POST <BASE_URL>/entities with header Content-Type: application/json and Authorization: Bearer <AUTH_TOKEN>.
3. Send payload: {""name"":""API-User-<UNIQUE_SUFFIX>"",""enabled"":true}.
4. Capture the response status and body.
5. Verify the documented success status, response schema, generated identifier, name, and enabled value.
6. Send GET <BASE_URL>/entities/<CAPTURED_ID> and verify the persisted values.
7. Delete only <CAPTURED_ID> when the documented cleanup operation is available.","The create request returns the documented success status and a response matching the confirmed contract. The returned identifier is usable to retrieve the entity, and the retrieved values match the request. Owned cleanup succeeds when supported."
```

Do not copy the example's endpoint or expected behavior into a real deliverable without evidence.
