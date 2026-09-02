# Mobile exploration evidence format

Create one append-only Markdown record per explored flow at:

`docs/mobile-evidence/<app-slug>/<flow-slug>-<YYYYMMDDTHHMMSSZ>.md`

Store sanitized PNGs beside it under `screenshots/` and link them relatively. Do not retain a full hierarchy dump when a focused, redacted excerpt proves the same locator.

## Required record

1. **Scope and source**: requirement/story, case names, and whether behavior is required, documented, or only observed.
2. **Runtime metadata**: UTC time, platform, emulator/device model and UDID alias, OS/API, orientation, app name/version/build, package/bundle ID, APK/IPA filename and SHA-256 when available, Appium/driver versions, reset method, and session capability summary. Never store secrets.
3. **Preconditions**: starting screen, app/account state, permissions, data reset, and compatibility dialogs.
4. **Numbered observations**: for every action record the starting state, semantic element, locator strategy/value, action/gesture or sanitized input class, expected transition, actual visible result, and screenshot filename.
5. **Locator inventory**: a table with screen, semantic name, preferred strategy, exact selector, observed class/resource ID/text/content description, scroll requirement, fallback justification, and confidence (`proven` or `unverified`).
6. **Assertions**: exact visible state/text/count/navigation result that was actually checked. Redact entered passwords and personal data.
7. **Cleanup**: cart/account/app reset, session deletion, and whether cleanup succeeded.
8. **Case mapping and gaps**: each retained manual case mapped to the successful observation section; list unverified devices, orientations, permissions, and behavior separately.

Screenshots must show only the app under test. Crop or omit system notifications, user accounts, keyboards containing sensitive text, and unrelated applications. Use accessibility ID before ID, platform-native selectors next, and XPath only with a recorded reason.
