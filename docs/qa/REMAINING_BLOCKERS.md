# Remaining Blockers Before Play Store Release

Prioritized by impact. Complete P0–P1 before public launch.

## P0 — Must Fix

1. ~~**Release build validation**~~ — ✅ `assembleRelease` succeeds; ProGuard mapping generated. Sign APK for device install.
2. ~~**Privacy policy + Data safety**~~ — ✅ `docs/legal/PRIVACY_POLICY.md`, `docs/store/DATA_SAFETY_FORM.md`
3. **Publish privacy policy URL** — Host policy at HTTPS URL in Play Console (placeholder email still in doc)
4. **Device test run** — `connectedDebugAndroidTest` + manual sign-off checklist

## Deferred (premium disabled)

- Play Billing integration — parked behind `FeatureFlags.PREMIUM_ENABLED = false`

## P1 — Should Fix

5. ~~**Compose E2E harness**~~ — Fixed: intruder capture default after reset, CLEAR_TASK relaunch, setup VM refresh (verify on device)
6. **Manual sign-off** — Biometric, session timeout, panic logout ([MANUAL_SIGNOFF_CHECKLIST.md](MANUAL_SIGNOFF_CHECKLIST.md))
7. **Note soft-delete / restore** — Product gap; delete-only for notes (files support restore)
8. **Accessibility** — TalkBack pass on vault screens (calculator keys have content descriptions)

## P2 — Nice to Have

9. Key rotation UI in Security Center
10. File import UI limit messaging (100 MB)
11. Macrobenchmark cold-start module
12. Notification listener E2E with test double service

## Test Commands

```powershell
cd CalculatorVaultPrivacySpace
.\gradlew testDebugUnitTest
.\gradlew assembleRelease
.\gradlew connectedDebugAndroidTest
.\scripts\generate-test-reports.ps1
```

## Release smoke (signed debug keystore for local QA)

```powershell
$apk = "app\build\outputs\apk\release\app-release-unsigned.apk"
$signed = "app\build\outputs\apk\release\app-release-smoke.apk"
Copy-Item $apk $signed -Force
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 `
  -keystore "$env:USERPROFILE\.android\debug.keystore" `
  -storepass android -keypass android $signed androiddebugkey
adb install -r $signed
```

## Current Gate Status

| Gate | Required | Actual |
|------|----------|--------|
| Critical security issues | 0 | 0 |
| Migration failures | 0 | 0 (automated paths) |
| Session bypass | 0 | 0 (automated lock test) |
| Vault data leakage | 0 | 0 (fake vault test) |
| Release ProGuard build | Pass | ✅ Pass |
| Legal / Data safety docs | Complete | ✅ Draft complete |
| E2E on device | Pass | **Pending re-run** (harness fixed; needs emulator) |
| Readiness score | ≥ 90% | **~86%** (estimate after E2E fix) |
