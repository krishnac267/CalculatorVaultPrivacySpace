# Implementation Roadmap

## Phase 1 — MVP Shell ✅

- [x] Gradle project + Hilt + Room + Compose
- [x] Java domain/data/core layers
- [x] Calculator engine with PIN detection
- [x] Setup wizard (PIN, fake PIN, biometric flag)
- [x] Session lock on background
- [x] Vault shell with One UI–inspired dashboard
- [x] Bottom nav: Home / Apps / Notes / Files / Settings
- [x] Notes list (read/search from Room)
- [x] Debug APK builds successfully

## Phase 2 — Core Privacy Features ✅

- [x] Biometric unlock (BiometricPrompt → real vault session)
- [x] Notes CRUD UI (create, edit, delete, favorite, lock flags)
- [x] App shortcuts (PackageManager query, launch tracking)
- [x] Fake vault data partition (`vaultScope` on notes/apps)
- [x] Failed PIN attempt logging (`intruder_logs` after 3 failures)
- [x] Pull-to-refresh + skeleton loading on dashboard
- [x] Haptic feedback on bottom nav

## Phase 3 — Vault Content

- [ ] NotificationListenerService → vault notification center
- [ ] File import (SAF), preview (Coil/PdfRenderer)
- [ ] SQLCipher database encryption
- [ ] Encrypted note content (AES-GCM per note)
- [ ] Storage quota enforcement (free tier)

## Phase 4 — Monetization & Production

- [ ] Google Play Billing (one-time premium)
- [ ] AdMob banner (free tier, calculator screen only)
- [ ] Firebase Analytics (privacy-safe events)
- [ ] Backup/export (encrypted archive)
- [ ] ProGuard tuning + release signing
- [ ] Play Store listing, privacy policy, data safety form

## Phase 5 — Premium UX Polish

- [ ] Shared element transitions
- [ ] Dynamic color (Material You)
- [ ] Theme picker (premium)
- [ ] Widgets: frequently used apps
- [ ] Multi-vault support (premium)

## Estimated Timeline

| Phase | Duration | Outcome |
|-------|----------|---------|
| 1 | Done | Compilable MVP |
| 2 | Done | Usable daily driver |
| 3 | 3–4 weeks | Full vault feature set |
| 4 | 2 weeks | Play Store candidate |
| 5 | Ongoing | Premium flagship UX |
