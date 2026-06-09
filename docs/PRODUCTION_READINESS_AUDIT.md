# Production Readiness Audit — Calculator Vault Privacy Space

**Audit date:** June 2025  
**Version audited:** 1.0.0 (pre–2.0)  
**Target:** Samsung Secure Folder–class quality

---

## Executive Summary

The app delivers a credible **MVP privacy shell**: calculator disguise, PIN/fake PIN/biometric auth, scoped notes/apps, session timeout, and basic intruder logging. It is **not production-ready** for Play Store launch without addressing encryption, session–navigation coupling, missing vault features (files/notifications), and compliance documentation.

Version **2.0** (this release) closes the highest-impact gaps: intruder selfie, Security Center, Notification Vault, enhanced fake vault, analytics, and premium UX.

---

## 1. Missing Premium Features

| Feature | Pre-2.0 | v2.0 Action |
|---------|---------|-------------|
| Play Billing / premium unlock | Stub boolean only | Roadmap Phase 4 |
| Storage quota enforcement | Model exists, not enforced | Storage analytics + limits UI |
| Unlimited notes/apps (premium) | Not gated | Premium banner in Security Center |
| Backup/export | Not started | Phase 4 |
| Themes / Material You | Static palette | Phase 5 |
| AdMob (free tier) | Not started | Phase 4 |
| SQLCipher DB encryption | Plaintext Room | Phase 3 (documented) |
| File vault import/preview | Placeholder screen | Phase 3 |
| Notification vault | DAO only | **Implemented in 2.0** |
| Intruder selfie | Text logs only | **Implemented in 2.0** |
| Security Center hub | Settings fragment only | **Implemented in 2.0** |
| Decoy vault seed content | Empty fake vault | **Implemented in 2.0** |

---

## 2. Security Weaknesses

| Severity | Issue | Recommendation |
|----------|-------|----------------|
| **Critical** | Room DB stores notes in plaintext | SQLCipher + field encryption (Phase 3) |
| **Critical** | Session locks on `onStop` but vault UI remains visible | Navigate to calculator when locked (**2.0**) |
| **High** | Calculator screen not `FLAG_SECURE` | Optional secure flag when PIN setup complete |
| **High** | Biometric always opens real vault | Document; decoy biometric in Phase 5 |
| **High** | `fallbackToDestructiveMigration()` wipes user data | Proper migrations before public launch |
| **Medium** | Note `locked` flag has no re-auth gate | PIN prompt before opening locked notes |
| **Medium** | Root/emulator detection unused | Surfaced in Security Center (**2.0**) |
| **Medium** | File import stores external paths | Copy to app-private storage (Phase 3) |
| **Low** | Retrofit/OkHttp/INTERNET unused | Remove or justify in privacy policy |
| **Low** | Panic logout wipes all prefs | Expected; add confirmation (**2.0**) |

---

## 3. UX Inconsistencies

| Issue | Fix |
|-------|-----|
| Dashboard Notifications card not tappable | Wire to Notifications route (**2.0**) |
| Files screen is placeholder | Premium empty state + Phase 3 import |
| Settings uses generic icons for all cards | Security Center with proper iconography (**2.0**) |
| Apps favorite toggle not in UI | Long-press or star action (**2.0**) |
| Fake vault opens empty | Auto-seed decoy content (**2.0**) |
| No loading/error states on some screens | Skeleton + empty states (**2.0**) |
| Bottom nav hidden only on note editor | Consistent for Security Center (**2.0**) |

---

## 4. Performance Bottlenecks

| Area | Issue | Mitigation |
|------|-------|------------|
| Installed apps list | Full PackageManager scan cached once | OK for MVP; paginate picker (**2.0**) |
| Notes/apps queries | Synchronous on main via ViewModel IO | Already on `viewModelScope`; add indexes |
| Dashboard load | Aggregates multiple DAO calls | Single `LoadDashboardUseCase` (**existing**) |
| Notification listener | Could flood DB | Cap per-app history (**2.0**) |
| Camera capture | CameraX bind on main | Minimize latency mode (**2.0**) |

---

## 5. Database Scaling Issues

| Issue | Status |
|-------|--------|
| No pagination on lists | Load all rows; add LIMIT for large vaults (Phase 3) |
| `LIKE '%query%'` searches | Full table scan; acceptable <10k rows |
| No FTS for notes | Phase 3 |
| Notifications unbounded | **2.0:** trim to 500 newest |
| Schema v2→v3 destructive | Acceptable pre-release; migrations required for prod |
| Files/notifications lacked `vaultScope` | **2.0:** added |

---

## 6. Navigation Problems

| Issue | Fix |
|-------|-----|
| Lock on background doesn't exit vault graph | Session observer pops to calculator (**2.0**) |
| No Notifications route | **2.0** |
| No Security Center route | **2.0** |
| Panic logout only path to forced lock UI | Session observer handles all locks |
| Nested NavControllers (outer + inner) | Acceptable; document back behavior |

---

## 7. Accessibility Issues

| Issue | Priority |
|-------|----------|
| Icon buttons missing consistent `contentDescription` | **2.0** improved on new screens |
| Calculator display not announced to TalkBack | Add semantics on display |
| Color-only status (favorite star) | Add text labels |
| Touch targets on calc buttons | Adequate (~48dp) |
| No font scaling overrides | Uses sp; OK |

---

## 8. Play Store Compliance Risks

| Risk | Mitigation |
|------|------------|
| **Notification listener** — sensitive permission | Clear in-app disclosure + privacy policy; user must enable in system settings |
| **Camera** — intruder selfie | Opt-in toggle; disclose in privacy policy; no background capture |
| **Misleading “hide apps” claims** | Market as privacy vault / secure space, not app hider |
| **Data safety form** | Declare local storage, PIN hash, optional photos |
| **No privacy policy URL in app** | Add link in Settings (**2.0**) |
| **BIND_NOTIFICATION_LISTENER** review scrutiny | Justify as core feature with UX to enable |
| **Target API / 64-bit** | targetSdk 35 — OK |

---

## Version 2.0 Release Scope

Implemented in this release:

1. **Intruder selfie capture** (opt-in, CameraX, front camera)
2. **Security Center** — device status, intruder gallery, toggles
3. **Notification Vault** — listener service + grouped UI
4. **Enhanced fake vault** — decoy seed data on first fake unlock
5. **Dashboard redesign** — analytics cards, security summary
6. **Premium empty states** — reusable component with CTA
7. **Premium animations** — staggered entry, animated progress
8. **Storage analytics** — usage vs limits
9. **Security analytics** — failed attempts, intruder count, posture score

---

## Recommended Post-2.0 Roadmap

1. SQLCipher + note body encryption  
2. Play Billing + quota enforcement  
3. File vault (SAF + encrypted copy)  
4. Proper Room migrations  
5. E2E tests + privacy policy hosting  
