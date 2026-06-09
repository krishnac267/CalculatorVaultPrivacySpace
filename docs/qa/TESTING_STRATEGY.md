# QA Testing Strategy

Calculator Vault Privacy Space **v3.0.0**

## Test Pyramid

| Layer | Framework | Location | Purpose |
|-------|-----------|----------|---------|
| Unit | JUnit 5 + Robolectric + MockK + Truth | `app/src/test` | Crypto, domain, session logic |
| Integration | Hilt + Room + device | `app/src/androidTest/.../integration` | Repositories, file import, notifications |
| E2E | Compose UI Test + Espresso + Hilt | `app/src/androidTest/.../e2e` | Auth, notes, fake vault, session |
| Migration | Room MigrationTestHelper | `app/src/androidTest/.../migration` | v1→v4 schema regression |
| Performance | Instrumented benchmarks | `app/src/androidTest/.../performance` | Encrypt/decrypt latency |
| Accessibility | Compose semantics audit | `app/src/androidTest/.../accessibility` | Content descriptions |

## Running Tests

```powershell
# Unit tests (JVM)
.\gradlew testDebugUnitTest

# Instrumented (device/emulator required)
.\gradlew connectedDebugAndroidTest

# Generate HTML reports
.\scripts\generate-test-reports.ps1
```

Reports:

- Unit: `app/build/reports/tests/testDebugUnitTest/index.html`
- Android: `app/build/reports/androidTests/connected/index.html`

## Test Tags

Compose `testTag` constants live in `presentation/testing/TestTags.kt`.

## Reset Harness

`ResetAppForTestingUseCase` clears DB, prefs, vault files, preview cache, and session state before each E2E scenario.

## Coverage Targets (v3.0.0)

- Crypto round-trip + tamper rejection
- Migration paths 1→4, 3→4
- PIN unlock / fake PIN / background lock
- Note encryption at rest
- File import encrypted copy
- Fake vault data isolation
- Session preview cache purge

## Known Gaps

- Note restore (soft-delete) not implemented — tests marked N/A
- Biometric E2E requires emulator with fingerprint enrolled
- SAF file picker E2E uses integration import instead of UI picker
- Play Billing flows untested (not implemented)
