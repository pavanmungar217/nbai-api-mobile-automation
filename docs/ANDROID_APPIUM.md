# Android Appium assignment guide

## Suite design

The sample deliberately contains five independent tests: product details, valid login, invalid
login, add one backpack and verify the cart, and reach the checkout address form. This demonstrates
the requested behavior without turning a representative assignment into a large regression suite.

Production mobile code lives under `src/main/java/com/nbai/automation/mobile`:

- `config` resolves the server, APK, device, UDID, and explicit-wait settings.
- `driver` creates a UiAutomator2 `AndroidDriver`; it does not mutate global Appium state.
- `screen` contains one Page Object per app screen. Every screen uses Appium PageFactory and
  accessibility IDs where the app supplies them.

Tests live under `src/test/java/com/nbai/automation/mobile`. Each method creates and owns one driver
session, and teardown always quits it. `noReset=false` gives every test clean app data because this
app persists cart and sign-in state across ordinary restarts. No test depends on another test or on
suite ordering. `android-testng.xml` explicitly disables parallel execution and uses one thread.

## 1. Install the local prerequisites

Install:

1. JDK 17 or newer (already required by this Maven project).
2. Android Studio from the official Android developer site.
3. In Android Studio, open **Settings > Languages & Frameworks > Android SDK** and install:
   - one current stable Android SDK Platform (API 35 is a reasonable choice);
   - Android SDK Platform-Tools;
   - Android SDK Command-line Tools (latest);
   - Android Emulator;
   - an ARM64 system image on Apple silicon, or x86_64 on Intel.
4. Node.js LTS and npm.

On macOS, add the Android SDK to your shell (confirm the actual path shown in Android Studio):

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin"
```

Put those exports in `~/.zshrc`, run `source ~/.zshrc`, and restart Codex/your IDE so Appium MCP
inherits them. Validate the tools:

```bash
adb version
emulator -version
java -version
```

## 2. Create and run an emulator

The simplest route is Android Studio:

1. Open **Tools > Device Manager**.
2. Choose **Create Virtual Device** and select a Pixel phone.
3. Select the system image installed above and finish the wizard.
4. Start the AVD and wait for the Android home screen.
5. Confirm it is visible and fully booted:

```bash
adb devices -l
adb shell getprop sys.boot_completed
```

The first command should show one `emulator-...` entry with state `device`; the second should print
`1`. If it says `offline`, cold-boot the AVD from Device Manager before troubleshooting Appium.

## 3. Download and install My Demo App

The [target repository release page](https://github.com/saucelabs/my-demo-app-rn/releases) is
archived but its latest release remains v1.3.0. Download build 244 into the
ignored `apps` directory:

```bash
curl -fL \
  https://github.com/saucelabs/my-demo-app-rn/releases/download/v1.3.0/Android-MyDemoAppRN.1.3.0.build-244.apk \
  -o apps/Android-MyDemoAppRN.1.3.0.build-244.apk
```

Installing with `adb` is useful for a manual smoke check, although the Appium `app` capability also
installs the APK when a session starts:

```bash
adb install -r apps/Android-MyDemoAppRN.1.3.0.build-244.apk
adb shell monkey -p com.saucelabs.mydemoapp.rn 1
```

Confirm the catalog opens. Supply the app's published demo account to the suite only through the
runtime keys in section 6; do not save credentials in source or properties files.

## 4. Install and start Appium

Install Appium and the official Android driver:

```bash
npm install -g appium
appium driver install uiautomator2
appium driver doctor uiautomator2
```

Resolve every required item reported by the doctor, then start the server in its own terminal:

```bash
appium
```

The Java suite defaults to `http://127.0.0.1:4723`. Appium 2/3 uses this root URL; do not add the old
`/wd/hub` base path unless you explicitly start Appium with that path.

## 5. Explore with Appium MCP

Appium MCP is an exploration aid; the checked-in Java Page Objects remain the source for tests.
With the emulator running and environment variables available to Codex:

1. Ask Appium MCP to select an Android device. If several appear, select the emulator UDID printed
   by `adb devices`.
2. Create a local/embedded Android session with these capabilities:

```json
{
  "appium:automationName": "UiAutomator2",
  "appium:app": "/absolute/path/to/apps/Android-MyDemoAppRN.1.3.0.build-244.apk",
  "appium:appPackage": "com.saucelabs.mydemoapp.rn",
  "appium:noReset": false
}
```

3. Capture the page source and a screenshot at each new screen.
4. Find elements in this order: accessibility ID, resource ID, Android UIAutomator, then XPath only
   as a last resort.
5. Walk the smallest representative paths:
   - menu -> login -> valid credentials -> products;
   - menu -> login -> invalid credentials -> error message;
   - Backpack -> Add To Cart -> cart;
   - logged-in cart -> Proceed To Checkout -> address form.
6. Record only selectors proven by the hierarchy. The official app IDs used here include
   `open menu`, `menu item log in`, `Username input field`, `Password input field`, `Login button`,
   `Add To Cart button`, `cart badge`, `Proceed To Checkout button`, and
   `checkout address screen`.
7. Delete the MCP session after exploration so the emulator is not left locked by a stale driver.

Appium MCP can run an embedded local server, so it does not require the separately started `appium`
process. The Java tests do require that server. If MCP reports that `ANDROID_HOME` is absent, restart
the application that hosts MCP after exporting the SDK variables; changing only an already-open
terminal will not update that process.

## 6. Run the serial Android suite

With the emulator and Appium server running:

```bash
export MOBILE_VALID_USERNAME='<valid demo username>'
export MOBILE_VALID_PASSWORD='<valid demo password>'
export MOBILE_INVALID_USERNAME='<invalid username>'
export MOBILE_INVALID_PASSWORD='<invalid password>'
./mvnw clean test -Dtest.suite=src/test/resources/suites/android-testng.xml
```

Optional overrides:

```bash
./mvnw clean test \
  -Dtest.suite=src/test/resources/suites/android-testng.xml \
  -Dandroid.udid=emulator-5554 \
  -Dandroid.deviceName=Pixel_API_35 \
  -Dandroid.app=/absolute/path/to/Android-MyDemoAppRN.1.3.0.build-244.apk \
  -Dappium.serverUrl=http://127.0.0.1:4723
```

Configuration precedence is JVM property, environment variable, then the local default:

| Purpose | JVM property | Environment variable |
|---|---|---|
| Server URL | `appium.serverUrl` | `APPIUM_SERVER_URL` |
| APK path | `android.app` | `ANDROID_APP` |
| Device name | `android.deviceName` | `ANDROID_DEVICE_NAME` |
| Device UDID | `android.udid` | `ANDROID_UDID` |
| Explicit wait | `mobile.waitSeconds` | `MOBILE_WAIT_SECONDS` |
| Valid username | `mobile.validUsername` | `MOBILE_VALID_USERNAME` |
| Valid password | `mobile.validPassword` | `MOBILE_VALID_PASSWORD` |
| Invalid username | `mobile.invalidUsername` | `MOBILE_INVALID_USERNAME` |
| Invalid password | `mobile.invalidPassword` | `MOBILE_INVALID_PASSWORD` |

Credential values are required only by the corresponding login cases. `BaseMobileTest` initializes
the complete per-method object graph through `MobileObjectsFactory`; each test uses
`mobile.credentials()` to request the valid or invalid credential it needs. JVM properties take
precedence over environment variables, values are never logged, and the credential holder redacts
both fields from its string representation.

Reports are written to `target/surefire-reports` and `target/allure-results`; a failed mobile test
also attaches a screenshot to Allure.

## 7. Extend it live

For another flow, first explore it with MCP, add behavior to the relevant screen object (or add one
new screen), and keep assertions in the test. Prefer explicit waits for screen state, never fixed
sleeps. Keep test data inside each method, preserve the serial suite setting, and avoid TestNG
dependencies or priorities. A sensible next checkout test would fill the address form and verify the
payment screen; it should be added only after those locators are inspected on the chosen app build.
