# Manual Sign-Off Checklist — v3.0.0

Complete on a **physical device** (recommended) or API 35 emulator before public Play Store submission. Check each box and note device model + Android version.

**Tester:** _______________ **Date:** _______________ **Build:** release signed / debug _______________

## Release build smoke test

- [ ] Install release APK (signed) without crash on cold start
- [ ] Complete first-time setup (PIN + confirm)
- [ ] Unlock vault via calculator PIN
- [ ] Create, edit, and reopen an encrypted note
- [ ] Import a file via SAF; preview opens
- [ ] Lock app (home button); reopen requires PIN
- [ ] No ProGuard-related crashes (Settings, Notes, Files, Apps screens)

## Biometric unlock

- [ ] Enable biometric in Security settings (device with enrolled fingerprint/face)
- [ ] Lock vault; unlock with biometric succeeds
- [ ] Wrong biometric / cancel falls back to PIN
- [ ] Disable biometric; PIN-only unlock works

## Session timeout

- [ ] Set session timeout to 1 minute in Security settings
- [ ] Unlock vault; wait >1 minute without interaction
- [ ] Return to app — vault is locked, PIN required

## Panic logout

- [ ] Add test note or app to vault
- [ ] Trigger panic logout from Security settings
- [ ] App returns to setup flow; previous PIN no longer works
- [ ] Complete new setup; old vault data is not accessible with new PIN

## Decoy vault (optional)

- [ ] Configure decoy PIN during setup or settings
- [ ] Decoy PIN shows decoy-scoped data only
- [ ] Real PIN shows real vault; no cross-leakage

## Intruder capture (optional, camera device)

- [ ] Enable intruder capture
- [ ] Enter wrong PIN until capture triggers
- [ ] Intruder log entry appears in Security

## Notification vault (optional)

- [ ] Grant Notification Access in system settings
- [ ] Receive test notification from another app
- [ ] Notification appears in vault Notifications screen

## Accessibility (TalkBack)

- [ ] Enable TalkBack; calculator keys announce labels
- [ ] Navigate vault dashboard and open Notes
- [ ] No focus traps on back navigation

## Sign-off

| Area | Pass / Fail | Notes |
|------|-------------|-------|
| Release smoke | | |
| Biometric | | |
| Session timeout | | |
| Panic logout | | |
| Decoy vault | | |
| Intruder | | |
| Notifications | | |
| Accessibility | | |

**Overall:** ☐ Approved for Play Store  ☐ Blocked — see notes
