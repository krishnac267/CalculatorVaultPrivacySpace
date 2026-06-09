# Security Audit Report — v3.0.0

**Auditor role:** Senior Android Security Auditor  
**Date:** June 2025  
**Scope:** CalculatorVaultPrivacySpace production build

## Executive Summary

| Area | Rating | Notes |
|------|--------|-------|
| Data at rest | **Pass** | SQLCipher DB + AES-GCM notes/files |
| Authentication | **Pass** | PBKDF2 PIN, session lock, locked notes |
| Session handling | **Pass with fix** | Preview cache now cleared on lock |
| Fake vault isolation | **Pass with fix** | Notification scope now session-aware |
| Key management | **Pass** | Keystore content keys, EncryptedSharedPreferences |
| Transport / network | N/A | No vault cloud sync |

**Overall security posture:** 88/100

## Verified Controls

- SQLCipher passphrase independent of PIN (`DatabaseKeyManager`)
- Note bodies stored as `encryptedContent`; list views do not decrypt
- Locked notes require PIN re-auth (`NoteUnlockSession`)
- Vault files stored as encrypted blobs; no source URI persisted
- `FLAG_SECURE` on calculator (post-setup) and vault shell
- Tampered ciphertext rejected (unit test coverage)
- Destructive DB migrations removed

## Findings

### Fixed During Audit

| ID | Severity | Finding | Resolution |
|----|----------|---------|------------|
| SEC-001 | Medium | Preview cache not cleared on session lock | `SessionManager.lock()` → `FileStorageManager.clearPreviewCache()` |
| SEC-002 | Medium | Notifications always stored in REAL scope | `storeNotification` uses `scope()` |

### Open Findings

| ID | Severity | Finding | Recommendation |
|----|----------|---------|----------------|
| SEC-003 | Low | `searchText` plaintext for FTS | Document trade-off; optional blind index in v3.1 |
| SEC-004 | Low | Key rotation not exposed in UI | Wire `KeyRotationManager` to Security Center |
| SEC-005 | Info | Root/emulator detection only | Display warnings; no hard block |
| SEC-006 | Medium | No certificate pinning | N/A until remote APIs added |
| SEC-007 | Low | Panic logout wipes DB passphrase | Expected; document re-setup requirement |

## Cryptography Checklist

- [x] AES-256-GCM for content (`AES/GCM/NoPadding`)
- [x] 12-byte IV prepended
- [x] Android Keystore non-exportable keys
- [x] SQLCipher 4.x for Room
- [x] PBKDF2-HMAC-SHA256 PIN hashing (100k iterations)
- [x] Constant-time hash compare

## Release Recommendation

**No critical security blockers** after SEC-001/SEC-002 fixes. Proceed to closed beta with documented open items.
