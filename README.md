# NBAI API and Mobile Automation Framework

This repository contains two independent automation suites built on a shared Java foundation:

- REST API automation for the public Restful Booker test service.
- Android UI automation for Sauce Labs My Demo App.

The project supports local development, a repeatable manual-to-automation workflow, and a GitHub
Actions quality gate for the API suite. Mobile device-cloud execution and external test-management
publishing remain deferred until their target platforms are selected.

## Framework capabilities

- Java 17 and the Maven Wrapper for reproducible builds.
- TestNG suites, groups, lifecycle management, and controlled parallel execution.
- REST Assured behind a transport interface and endpoint-specific services.
- Typed request and response models using Lombok builders and Jackson.
- Appium with UiAutomator2, Page Objects, Appium PageFactory, and explicit waits.
- JSON Schema validation plus field-level and persistence assertions.
- Correlation IDs, sanitized logs, sanitized Allure API evidence, and mobile failure screenshots.
- Surefire reports and a neutral JSON run summary for later CI or test-management integration.
- Repository agents for planning, implementation, review, and failure repair.

Current approved coverage is defined by two manual plans:

- `src/test/resources/testcases/restful-booker-api-test-plan.csv`: 27 API cases.
- `src/test/resources/testcases/my-demo-app-android-test-plan.csv`: 5 Android cases.

## Architecture

```text
                              Shared core
        Configuration | JSON | HTTP | sanitization | logging | reporting
                              /         \
                    Restful Booker       Android Appium
                         API                 adapter
                          |                    |
              Test -> service -> client   Test -> screen -> driver
                          |                    |
                REST Assured filters      PageFactory + waits
```

The API and mobile adapters share core configuration and reporting concepts, but neither depends on
the other. API tests do not create mobile objects, and mobile tests do not use the API client.

### Repository structure

```text
nbai-api-mobile-automation/
├── AGENTS.md                         Repository-wide engineering rules
├── README.md                         Setup, execution, and contribution guide
├── pom.xml                           Dependencies, Java level, and Surefire setup
├── mvnw / mvnw.cmd                   Maven Wrapper for macOS/Linux and Windows
├── .codex/agents/                    Planning, implementation, review, and healer roles
├── .agents/skills/                   Reusable workflows used by the agents
├── apps/                             Local APK location; APK files are ignored by Git
├── docs/
│   ├── ANDROID_APPIUM.md             Detailed emulator, Appium, and MCP guide
│   ├── restful-booker-evidence/      Sanitized curl observations and case mapping
│   └── mobile-evidence/              Appium observations, screenshots, and locator evidence
├── scripts/
│   └── check-framework.sh            Deterministic architecture and safety checks
└── src/
    ├── main/java/com/nbai/automation/
    │   ├── core/                     Shared configuration, HTTP, JSON, filters, and reporting
    │   ├── api/booker/               Endpoints, API models, and services
    │   └── mobile/                   Driver configuration, object factory, and screen objects
    └── test/
        ├── java/com/nbai/automation/
        │   ├── api/booker/           API tests, assertions, and test support
        │   ├── core/                 Local contract and sanitizer tests
        │   └── mobile/               Android tests and per-method driver lifecycle
        └── resources/
            ├── config/               Non-secret API and mobile defaults
            ├── schemas/              API response schemas
            ├── suites/               API and Android TestNG suites
            ├── testcases/            Approved reproducible manual test plans
            └── META-INF/services/    TestNG listener registration
```

### Responsibility boundaries

- `core/config` resolves immutable runtime configuration.
- `core/http` creates a fresh REST Assured request specification for every request.
- `api/booker/service` owns HTTP method, relative path, headers, and payload transport.
- API tests own business assertions, test data, captured IDs, and cleanup.
- `mobile/driver` owns Android driver creation and capabilities.
- `mobile/screen` owns locators, interactions, and screen-state waits.
- Mobile tests own workflow assertions; `BaseMobileTest` owns one driver session per method.
- TestNG listeners collect lifecycle evidence. The reporter aggregates only after the suite.

## Toolchain

| Component | Purpose |
|---|---|
| JDK 17+ | Compile and run the framework |
| Maven Wrapper | Resolve dependencies and provide consistent build commands |
| TestNG | Test selection, lifecycle, grouping, and parallel execution |
| REST Assured | API request execution |
| Jackson and Lombok | Typed payload construction and serialization |
| Appium Java Client | Android automation |
| UiAutomator2 | Android driver implementation |
| Allure | Human-readable execution evidence |
| Surefire | Maven-to-TestNG integration and machine-readable reports |
| SLF4J and Logback | Sanitized console and file logging |

## Local setup

### Common requirements

Install Git, JDK 17 or newer, and Node.js LTS with npm. Node.js is required for Appium and for the
optional npm-distributed Allure CLI. Network access is required for Maven dependencies and the
selected test target.

The repository includes Maven Wrapper scripts. A separate Maven installation is not required.

### macOS

Install common command-line tools with Homebrew if they are not already available:

```bash
brew install openjdk@17 node
```

Ensure `JAVA_HOME` points to JDK 17 or newer, then validate the checkout from the project root:

```bash
java -version
./mvnw -version
./mvnw clean -DskipTests package
```

If the wrapper is not executable after checkout:

```bash
chmod +x mvnw scripts/check-framework.sh
```

### Windows

Install Git, a JDK 17 distribution, and Node.js LTS. Set `JAVA_HOME` through Windows Environment
Variables and add `%JAVA_HOME%\bin` to `Path`.

Validate the checkout in PowerShell from the project root:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd clean -DskipTests package
```

`scripts/check-framework.sh` requires Bash. Run it from Git Bash or WSL on Windows:

```bash
./scripts/check-framework.sh
```

## Runtime configuration and secrets

Do not store credentials, tokens, cookies, or authorization headers in source, property files,
manual plans, evidence files, or shell scripts.

API configuration precedence is:

1. JVM system property.
2. Environment variable.
3. `src/test/resources/config/<environment>.properties`.
4. `src/test/resources/config/default.properties`.

| Purpose | JVM property | Environment variable | Checked-in default |
|---|---|---|---|
| Environment | `test.env` | `TEST_ENV` | `qa` |
| API base URI | `api.baseUri` | `API_BASE_URI` | Restful Booker QA URL |
| Connect timeout | `api.connectTimeoutMs` | `API_CONNECT_TIMEOUT_MS` | 10000 ms |
| Read timeout | `api.readTimeoutMs` | `API_READ_TIMEOUT_MS` | 20000 ms |
| Booker username | `booker.username` | `BOOKER_USERNAME` | None |
| Booker password | `booker.password` | `BOOKER_PASSWORD` | None |

Set API credentials for the current macOS shell:

```bash
export BOOKER_USERNAME='<username>'
export BOOKER_PASSWORD='<password>'
```

Set API credentials for the current Windows PowerShell session:

```powershell
$env:BOOKER_USERNAME = '<username>'
$env:BOOKER_PASSWORD = '<password>'
```

Restful Booker uses published playground credentials. They are suitable for trying the framework,
but the values are intentionally not committed. Copy them from the Restful Booker documentation
and supply them through environment variables, JVM properties, or GitHub secrets. This keeps the
same setup usable when the public playground is replaced by a private test environment.

The loader rejects a partially configured credential pair. Configuration objects and logs redact
credential values.

## Running API tests

The default Maven test command launches `src/test/resources/suites/api-testng.xml`. The suite selects
the `api` group in `com.nbai.automation.api.booker` and runs methods in parallel with three workers.

### Complete API suite

macOS:

```bash
./mvnw clean test
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean test
```

For a one-command trial without changing the current macOS shell environment:

```bash
BOOKER_USERNAME='<published playground username>' \
BOOKER_PASSWORD='<published playground password>' \
./mvnw clean test
```

For the equivalent trial in Windows PowerShell:

```powershell
$env:BOOKER_USERNAME = '<published playground username>'
$env:BOOKER_PASSWORD = '<published playground password>'
.\mvnw.cmd clean test
Remove-Item Env:BOOKER_USERNAME
Remove-Item Env:BOOKER_PASSWORD
```

The macOS values apply only to that command. The PowerShell values apply to the current process, so
the final two lines remove them after the run.

Use `clean` so old Allure results are not mixed into the current run.

### Change the API worker count

Surefire applies `parallel` and `threadCount` to the loaded TestNG suite for the current invocation.
The XML remains unchanged and continues to provide the three-worker default.

macOS, ten workers:

```bash
./mvnw clean test -Dparallel=methods -DthreadCount=10
```

Windows PowerShell, ten workers:

```powershell
.\mvnw.cmd clean test "-Dparallel=methods" "-DthreadCount=10"
```

`threadCount` is a maximum, not a guarantee that ten methods will always be active. The public
Restful Booker service can reset data or become unstable under load. Reproduce any parallel failure
with the narrowest single method before changing assertions or timeouts.

### Run one class or method

macOS:

```bash
./mvnw clean -Dtest=BookingRetrievalTest test
./mvnw clean '-Dtest=BookingUpdateTest#patchScalarAndDateFieldsUsingCookieToken' test
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean "-Dtest=BookingRetrievalTest" test
.\mvnw.cmd clean "-Dtest=BookingUpdateTest#patchScalarAndDateFieldsUsingCookieToken" test
```

A focused `-Dtest` run bypasses the configured XML suite and is the preferred diagnosis command.

### Local checks that do not call Restful Booker

macOS:

```bash
./scripts/check-framework.sh
./mvnw clean -Dtest=SchemaContractTest,SensitiveDataSanitizerTest test
```

Windows PowerShell and Git Bash:

```powershell
.\mvnw.cmd clean "-Dtest=SchemaContractTest,SensitiveDataSanitizerTest" test
```

```bash
./scripts/check-framework.sh
```

## Android and Appium setup

The complete emulator and exploration guide is in `docs/ANDROID_APPIUM.md`. The summary below is
enough to run the checked-in suite.

### Install Android tooling

Install Android Studio and use its SDK Manager to install:

- A current Android SDK Platform; API 35 is suitable for this project.
- Android SDK Platform-Tools.
- Android SDK Command-line Tools.
- Android Emulator.
- An ARM64 system image on Apple silicon or an x86_64 image on Intel/Windows.

Create a Pixel emulator in Android Studio Device Manager and start it. Confirm the device is ready:

```bash
adb devices -l
adb shell getprop sys.boot_completed
```

The second command must return `1`.

On macOS, add the SDK tools to the current shell. Confirm the SDK location in Android Studio first:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin"
```

On Windows PowerShell, the usual SDK location is under `%LOCALAPPDATA%`:

```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
$env:Path += ";$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:ANDROID_HOME\cmdline-tools\latest\bin"
```

Persist these values through the operating system environment settings if required, then restart
the IDE and terminals so Appium inherits them.

### Install Appium and UiAutomator2

The commands are the same on macOS and Windows:

```bash
npm install -g appium
appium driver install uiautomator2
appium driver doctor uiautomator2
```

Resolve every required item reported by the doctor. Start Appium in a separate terminal:

```bash
appium
```

The default server URL is `http://127.0.0.1:4723`. Do not add `/wd/hub` unless Appium was explicitly
started with that base path.

### Download the test APK

The expected default path is:

```text
apps/Android-MyDemoAppRN.1.3.0.build-244.apk
```

macOS:

```bash
mkdir -p apps
curl -fL \
  https://github.com/saucelabs/my-demo-app-rn/releases/download/v1.3.0/Android-MyDemoAppRN.1.3.0.build-244.apk \
  -o apps/Android-MyDemoAppRN.1.3.0.build-244.apk
```

Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force apps | Out-Null
Invoke-WebRequest `
  -Uri 'https://github.com/saucelabs/my-demo-app-rn/releases/download/v1.3.0/Android-MyDemoAppRN.1.3.0.build-244.apk' `
  -OutFile 'apps\Android-MyDemoAppRN.1.3.0.build-244.apk'
```

APK files are intentionally ignored by Git.

## Running mobile tests

The Android suite runs serially through `src/test/resources/suites/android-testng.xml`. A single
emulator cannot safely execute multiple Appium sessions in parallel. Do not override the one-thread
setting unless a device pool, unique UDIDs, and isolated Appium ports are implemented.

| Purpose | JVM property | Environment variable | Default |
|---|---|---|---|
| Appium server | `appium.serverUrl` | `APPIUM_SERVER_URL` | `http://127.0.0.1:4723` |
| APK path | `android.app` | `ANDROID_APP` | Default APK path above |
| Device name | `android.deviceName` | `ANDROID_DEVICE_NAME` | `Android Emulator` |
| Device UDID | `android.udid` | `ANDROID_UDID` | None |
| Explicit wait | `mobile.waitSeconds` | `MOBILE_WAIT_SECONDS` | 15 seconds |
| Valid username | `mobile.validUsername` | `MOBILE_VALID_USERNAME` | None |
| Valid password | `mobile.validPassword` | `MOBILE_VALID_PASSWORD` | None |
| Invalid username | `mobile.invalidUsername` | `MOBILE_INVALID_USERNAME` | None |
| Invalid password | `mobile.invalidPassword` | `MOBILE_INVALID_PASSWORD` | None |

The local mobile verification used the Android Virtual Device `Pixel_API_35` with UDID
`emulator-5554`. These are safe device identifiers, not credentials. Confirm the current UDID with
`adb devices -l`; replace both values when your AVD has a different name or identifier.

Run the same locally verified configuration on macOS. Replace the credential placeholders with the
published My Demo App login values and keep the invalid pair deliberately invalid:

```bash
MOBILE_VALID_USERNAME='<published demo username>' \
MOBILE_VALID_PASSWORD='<published demo password>' \
MOBILE_INVALID_USERNAME='<invalid test username>' \
MOBILE_INVALID_PASSWORD='<invalid test password>' \
ANDROID_UDID='emulator-5554' \
ANDROID_DEVICE_NAME='Pixel_API_35' \
./mvnw clean test \
  -Dtest.suite=src/test/resources/suites/android-testng.xml
```

Run the equivalent configuration on Windows PowerShell:

```powershell
$env:MOBILE_VALID_USERNAME = '<published demo username>'
$env:MOBILE_VALID_PASSWORD = '<published demo password>'
$env:MOBILE_INVALID_USERNAME = '<invalid test username>'
$env:MOBILE_INVALID_PASSWORD = '<invalid test password>'
$env:ANDROID_UDID = 'emulator-5554'
$env:ANDROID_DEVICE_NAME = 'Pixel_API_35'

.\mvnw.cmd clean test "-Dtest.suite=src/test/resources/suites/android-testng.xml"

Remove-Item Env:MOBILE_VALID_USERNAME
Remove-Item Env:MOBILE_VALID_PASSWORD
Remove-Item Env:MOBILE_INVALID_USERNAME
Remove-Item Env:MOBILE_INVALID_PASSWORD
Remove-Item Env:ANDROID_UDID
Remove-Item Env:ANDROID_DEVICE_NAME
```

Optional device overrides on macOS:

```bash
./mvnw clean test \
  -Dtest.suite=src/test/resources/suites/android-testng.xml \
  -Dandroid.udid=emulator-5554 \
  -Dandroid.deviceName=Pixel_API_35 \
  -Dandroid.app=/absolute/path/to/Android-MyDemoAppRN.1.3.0.build-244.apk
```

Optional device overrides on Windows PowerShell:

```powershell
.\mvnw.cmd clean test `
  "-Dtest.suite=src/test/resources/suites/android-testng.xml" `
  "-Dandroid.udid=emulator-5554" `
  "-Dandroid.deviceName=Pixel_API_35" `
  "-Dandroid.app=C:\absolute\path\to\Android-MyDemoAppRN.1.3.0.build-244.apk"
```

Each mobile method creates a new driver with `noReset=false` and `fullReset=true`, starts from clean
application state, and quits in `@AfterMethod`. On failure, visible credential fields are cleared
before a screenshot is attached to Allure.

## Allure reporting

Every TestNG run writes raw Allure results to `target/allure-results`. The Java adapter creates the
results; the Allure command-line tool renders them as HTML.

### Install Allure

macOS with Homebrew:

```bash
brew install allure
allure --version
```

On either macOS or Windows, run Allure without a global installation:

```bash
npx --yes allure-commandline --version
```

Windows users who prefer a global command can install the npm package:

```powershell
npm install -g allure-commandline
allure --version
```

### Serve a temporary report

Run the required suite first, then start the report server:

```bash
allure serve target/allure-results
```

Without a global installation:

```bash
npx --yes allure-commandline serve target/allure-results
```

The command creates a temporary report, opens it in the browser, and occupies the terminal until
the server is stopped.

### Generate a persistent HTML report

```bash
allure generate target/allure-results --clean -o target/allure-report
allure open target/allure-report
```

Without a global installation:

```bash
npx --yes allure-commandline generate target/allure-results --clean -o target/allure-report
npx --yes allure-commandline open target/allure-report
```

| Path | Contents |
|---|---|
| `target/surefire-reports` | TestNG/Surefire XML and diagnostic text |
| `target/allure-results` | Raw Allure results and sanitized API/mobile evidence |
| `target/allure-report` | Generated persistent HTML report |
| `target/test-run-summary.json` | Post-suite totals and run metadata |
| `target/logs/automation.log` | Correlated framework logs |

## Agent-assisted delivery workflow

Agents are stored under `.codex/agents`; their supporting workflows are under `.agents/skills`.
Attach the relevant TOML file in Codex/IntelliJ and state the target, scope, authorization, and
expected output. An agent does not expand its own authority: live mutations, external publishing,
commits, pushes, and pull requests still require explicit permission.

### Available agents

| Agent | Purpose | Primary input | Output |
|---|---|---|---|
| `api_planner.toml` | Verify API behavior before automation | Contract and authorized target | Sanitized curl evidence and manual CSV |
| `api_test_automation.toml` | Implement approved API cases | Manual CSV and matching evidence | REST Assured/TestNG automation |
| `api_reviewer.toml` | Independently review API changes | Framework and tests | Read-only findings |
| `api_test_automation_healer.toml` | Reproduce and repair API failures | Failure artifacts, CSV, evidence | Minimal repair or conflict record |
| `mobile_test_planner.toml` | Explore mobile behavior before automation | App, emulator, Appium MCP | Locator evidence and manual CSV |
| `mobile_test_automation.toml` | Implement approved mobile cases | Manual CSV and matching evidence | Appium/TestNG/Page Object automation |
| `mobile_reviewer.toml` | Independently review mobile changes | Framework, tests, plans, evidence | Read-only findings |
| `mobile_test_healer.toml` | Reproduce and repair mobile failures | Failure artifacts, CSV, evidence | Minimal repair or conflict record |

### Generate manual API test cases

Use `.codex/agents/api_planner.toml`. Provide the contract, authorized base URL, credential source,
maximum case count, and explicit permission for state-changing curl probes.

Example request:

```text
Use @file:.codex/agents/api_planner.toml.
Verify every proposed case against <authorized target> with curl before writing it.
Create no more than 50 independent positive, negative, edge, and chained cases.
Save sanitized evidence under docs/<api-name>-evidence and the CSV under
src/test/resources/testcases. Include exact method, path, headers, payload, captured values,
field-level expectations, persistence checks, and owned cleanup in every case.
```

The API planning gate is:

1. Read the contract and repository context.
2. Resolve the target, credential source, mutation permission, and cleanup method.
3. Execute every candidate once with curl using unique owned data.
4. Save sanitized requests, observations, assertions, and cleanup outcomes under `docs`.
5. Retain only cases supported by successful observations.
6. Write an RFC 4180 CSV with `Test Case Name`, `Steps`, and `Expectation`.
7. Validate that every CSV row maps to evidence and contains no secrets.

The planner does not write automation and does not overwrite an existing plan without approval.

### Generate manual mobile test cases

Use `.codex/agents/mobile_test_planner.toml`. Provide the app build, authorized emulator/device, and
requested workflows. The planner uses Appium MCP instead of curl.

For every retained case it records the device/API/app build, starting state, test data, actions,
visible results, sanitized screenshots, focused hierarchy evidence, proven locator, and reset
outcome. Evidence is append-only under `docs/mobile-evidence/<app-slug>`. The CSV is written under
`src/test/resources/testcases`, and the Appium MCP session is deleted after exploration.

### Create automation from an approved plan

Use `api_test_automation.toml` or `mobile_test_automation.toml` only after reviewing the manual plan
and evidence:

1. Read `AGENTS.md`, this README, the complete CSV, and all mapped evidence.
2. Reconcile every manual case with one test method or one isolated data-provider row.
3. Stop and report CSV/evidence conflicts; do not choose an expectation silently.
4. Implement through the existing services, models, screens, factories, filters, and listeners.
5. Keep data local to the test and preserve independent execution.
6. Run static checks, compilation, focused tests, and the appropriate suite.
7. Reconcile automated names against every CSV row and inspect all result artifacts.

API tests use explicit builders in the test method so every field is visible. Stateful cases show
the complete token, create, action, assertion, and `finally` cleanup flow. Mobile tests use
evidence-backed Page Object methods and a new driver session for every method.

### Review automation

Use `api_reviewer.toml` or `mobile_reviewer.toml` after implementation. Review agents are read-only
and report findings by severity. They check traceability, isolation, cleanup, assertion strength,
locator quality, secret handling, reporting, and maintainability.

### Heal a failing test

Use `api_test_automation_healer.toml` or `mobile_test_healer.toml` only after reproducing a failure:

1. Reproduce the narrowest method with the same configuration.
2. Map it to the exact CSV row and saved evidence.
3. Compare the current request or UI flow with the approved behavior.
4. Use bounded curl or Appium MCP only when current evidence is required and authorized.
5. Classify the failure before editing code.
6. Apply the smallest verified automation repair.
7. Rerun the focused method, checks, compilation, and relevant parallel or serial suite.

If current behavior conflicts with the baseline, the healer writes a sanitized, timestamped
conflict under the relevant evidence directory. It does not silently rewrite the plan, weaken
assertions, add blind retries, add sleeps, or accept multiple statuses to make a test pass.

## Engineering decisions and safeguards

### Test independence

- Every stateful API test creates collision-resistant data inside the test method.
- The returned booking ID is local; IDs are never selected arbitrarily from a shared list.
- Each stateful test acquires its own token and deletes only its booking in a `finally` block.
- Restful Booker tokens are administrative rather than booking-owned. Per-test tokens prevent one
  test from depending on another test's authentication state.
- Tests have no priorities, method dependencies, shared mutable IDs, tokens, responses, request
  specifications, or drivers.

### API transport and validation

- Endpoints and transport details remain in services; business assertions remain in tests or
  assertion helpers.
- A fresh REST Assured specification is created per request. Global REST Assured state is not
  mutated.
- REST Assured does not append an implicit charset to `Content-Type`; the XML endpoint requires the
  exact observed media type.
- Responses are checked for status, media type, schema where applicable, field values, and persisted
  state. Negative mutations confirm the original resource is unchanged.
- XML parsing disables external DTD and entity access.
- POST, PUT, PATCH, and DELETE requests are never retried automatically.

### Logging, evidence, and secrets

- Correlation IDs connect requests, responses, logs, and Allure attachments.
- Sensitive headers, cookies, credentials, and token fields are sanitized before persistence.
- Uncontrolled REST Assured `.log().all()` calls are forbidden by static checks.
- Logs show request/result summaries at INFO. Set `LOG_LEVEL=DEBUG` only for sanitized detail.
- External Jira, Xray, Zephyr, or other publishing is disabled. `NoOpTestRunPublisher` retains the
  local summary without network calls from parallel test methods.

### Mobile stability

- Accessibility IDs and resource IDs are preferred over UIAutomator and XPath.
- Fixed sleeps, coordinate taps, absolute XPath, fallback locator chains, and runtime self-healing
  are not used.
- Explicit waits verify screen state before interaction.
- Each method owns and quits its driver session. Clean app state prevents login and cart leakage.
- Mobile execution remains serial until a real multi-device allocation strategy exists.

### Framework enforcement

Run `scripts/check-framework.sh` before handoff. It rejects global REST Assured configuration,
TestNG order dependencies, fixed sleeps, uncontrolled request logging, absolute endpoint URLs in
Java source, unsupported API model patterns, known sample credentials, and missing framework files.

## GitHub Actions

`.github/workflows/api-tests.yml` defines the API quality gate. It runs on pull requests, pushes to
`main`, and manual dispatches.

The workflow has two jobs:

1. `Framework checks` runs the static rules, compiles the project, and executes the local schema and
   sanitizer tests without calling Restful Booker.
2. `Live API suite` runs the complete `api-testng.xml` suite after the framework job succeeds. It
   uses the suite's reviewed three-worker default.

Manual dispatch displays these inputs before starting the run:

| Input | Available choices | Default | Effect |
|---|---|---|---|
| Module | `api` | `api` | Selects the currently CI-enabled API module. Mobile will be added only with a device-cloud job. |
| Group | `all`, `smoke`, `regression`, `negative` | `all` | Overrides Surefire's TestNG group selection. `all` maps to the existing `api` group. |
| Threads | `1` through `10` | `3` | Overrides Surefire/TestNG method-level worker count for that run. |

To use them, open **Actions > API quality gate > Run workflow**, select the branch and dropdown
values, then select **Run workflow**. The resolved module, TestNG group, and maximum worker count are
written to the GitHub job summary. Push and pull-request runs do not receive manual inputs, so they
continue to execute all API cases with three workers.

The selectable groups come directly from the current test annotations:

- `smoke`: health and valid-token checks.
- `regression`: successful query, creation, retrieval, update, deletion, and lifecycle coverage.
- `negative`: rejected authentication or mutation requests and not-found/empty-result behavior.

Thread selection changes concurrency only; it does not shard, duplicate, or reorder tests. The
chosen value is a maximum, so the number of simultaneously active methods can be lower.

The live job runs for same-repository pull requests, pushes, and manual dispatches. GitHub does not
provide repository or environment secrets to workflows triggered by pull requests from forks, so a
fork receives the secret-free framework checks only. A maintainer must run the live suite from a
trusted branch before such a change is merged.

Anyone using a fork can still run the complete API suite. In the fork, create the `qa` Environment
and add the two secrets described below, then open **Actions > API quality gate > Run workflow**.
The manual `workflow_dispatch` run belongs to the fork, so it can use secrets configured in that
fork. Alternatively, use the one-command local example above. The public demo credentials are not
copied into this repository or workflow; each fork owner supplies the published values explicitly.

### Configure the API secrets

The live job references a GitHub Environment named `qa`. Create it and its secrets as follows:

1. Open the repository on GitHub.
2. Select **Settings > Environments**.
3. Select **New environment**, enter `qa`, and select **Configure environment**.
4. Under **Environment secrets**, select **Add secret**.
5. Add `BOOKER_USERNAME` with the API username.
6. Add `BOOKER_PASSWORD` with the API password.
7. Optionally, under **Environment variables**, add `API_BASE_URI` when CI must target a URL other
   than the checked-in QA default.
8. Optionally restrict deployment branches or require an approver before the environment releases
   its secrets.

The secret names are case-sensitive and must match the workflow exactly. Do not include quotes in
the stored values. The workflow validates that both credential secrets are present before starting
the live suite.

Repository-level secrets may be used instead when GitHub Environments are unavailable. Add the same
names under **Settings > Secrets and variables > Actions > Repository secrets**. Environment secrets
are preferred because they keep target-specific credentials and protection rules together.

The same setup can be performed with GitHub CLI after authenticating with repository-admin access:

```bash
gh secret set --env qa BOOKER_USERNAME
gh secret set --env qa BOOKER_PASSWORD
gh variable set --env qa API_BASE_URI --body 'https://test-environment.example'
```

Each `gh secret set` command securely prompts for its value. Omit `API_BASE_URI` when the configured
QA URL is correct.

### Failure and artifact behavior

The Maven test step does not use `continue-on-error`. A compilation error, configuration error,
assertion failure, or TestNG failure returns a non-zero exit code and fails the job.

Report generation and artifact upload use `if: always()`. They still execute after a failed test so
the failure can be diagnosed; they do not change the failed job result. Artifacts are retained for
14 days and include:

- Surefire XML and diagnostic output.
- Raw Allure results.
- A generated Allure HTML report.
- Sanitized framework logs.
- `test-run-summary.json`.

Open **Actions > API quality gate > workflow run > Artifacts** to download them. Reproduce a failure
locally with the focused `-Dtest=Class#method` commands documented above before changing code.

### Merge quality gate

Configure branch protection for `main` and require these checks for trusted pull requests:

- `Framework checks` must pass.
- `Live API suite` must pass.
- All owned test data must be cleaned up; cleanup failures are test failures, not warnings to ignore.
- The approved manual-plan-to-automation mapping must remain complete.
- Required review findings must be resolved.

Do not make artifact generation or external publishing a merge blocker unless the report itself is
the deliverable. The test result is authoritative; report rendering is diagnostic.

### Flaky-test policy

A failing test is treated as a real failure until its cause is established. The pipeline does not
use TestNG retry analyzers or automatic reruns to turn a failure green.

1. Reproduce the narrowest method sequentially with the same environment.
2. Compare Surefire, Allure, sanitized logs, the manual CSV, and saved curl evidence.
3. Classify the issue as an automation defect, product defect, configuration problem, public-service
   reset, cleanup failure, concurrency issue, or documented behavior change.
4. Fix the responsible code or baseline through the appropriate planner/healer workflow.
5. If temporary quarantine is unavoidable, require a defect, owner, reason, and expiry date. Keep
   the test visible in reports and restore it to the gate promptly.

Mutating POST, PUT, PATCH, and DELETE requests are never retried automatically. A future GET retry
requires repeated transient-failure evidence, a strict bound, and visible reporting.

### Mobile tests in CI

The Android suite is intentionally absent from the hosted-runner API job. The preferred production
design is a separate device-cloud job:

1. Build or obtain the versioned APK.
2. Upload the APK and test package to the selected provider.
3. Supply provider credentials through a protected GitHub Environment.
4. Start one isolated Appium session per allocated device and pass provider-specific capabilities,
   device ID, Android version, app reference, and server URL.
5. Run `android-testng.xml` on the allocated device.
6. Download Appium logs, screenshots, video, device logs, Surefire results, and Allure evidence.
7. Always close provider sessions and publish artifacts after the run.

A hosted Android emulator is suitable for an inexpensive smoke job only when the runner supports
hardware acceleration and the emulator boot is verified. A device cloud is better for merge or
scheduled coverage across real devices and Android versions. Do not start parallel sessions against
one emulator; parallel mobile execution requires a device matrix with one UDID and isolated Appium
connection per worker.

### Keeping feedback time short

As coverage grows:

- Keep static checks and local contract tests first so structural failures return quickly.
- Keep `cancel-in-progress` enabled so obsolete commits stop consuming runners.
- Retain Maven dependency caching through `actions/setup-java`.
- Run a small, stable smoke group on pull requests and the full regression after merge or on a
  schedule once suite duration justifies the split.
- Shard API tests only after independence, cleanup, and target capacity are proven. Keep each shard's
  reports and case mapping visible.
- Run the smallest relevant mobile device matrix on pull requests and broader OS/device coverage on
  scheduled runs.
- Track duration, failure category, quarantine age, and cleanup failures rather than optimizing only
  for pass percentage.

External Jira, Xray, Zephyr, or other result publishing remains disabled. When introduced, it must
run after the suite, use secret-backed credentials and an explicit enable switch, and remain outside
parallel test methods.
