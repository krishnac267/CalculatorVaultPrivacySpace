# Security Architecture

## Components

| Component | Package | Role |
|-----------|---------|------|
| `PinManager` | core.security | PBKDF2 PIN hashing, EncryptedSharedPreferences, Keystore master key |
| `DatabaseKeyManager` | core.security | SQLCipher passphrase generation/storage |
| `ContentEncryptionService` | core.security | AES-256-GCM note/file encryption via Keystore |
| `KeyRotationManager` | core.security | Content key rotation with re-encryption |
| `NoteUnlockSession` | core.session | Per-note unlock state within vault session |
| `SessionManager` | core.session | Lock/unlock state, timeout, panic logout |
| `NoteEncryptionMigrator` | data.migration | Plaintext → encrypted note upgrade |
| `FileStorageManager` | data.storage | SAF import, encrypted app-private file storage |
| `DeviceSecurityChecker` | core.security | Root/emulator heuristics |
| `SecureScreenEffect` | presentation | FLAG_SECURE on calculator + vault |

## PIN Flow

1. User enters digits on calculator display
2. `CalculatorEngine.isPinAttempt()` — regex `^\d{4,8}$`
3. On `=`, `ValidatePinUseCase` compares PBKDF2 hashes
4. Result: `REAL_VAULT`, `FAKE_VAULT`, or `INVALID`
5. Invalid PIN falls through to normal calculator evaluation

## Credential Storage

- Real PIN hash + salt → EncryptedSharedPreferences
- Optional fake PIN (decoy vault)
- DB passphrase (`db_passphrase_v1`) → EncryptedSharedPreferences
- Content key version → EncryptedSharedPreferences
- Master key: AES256-GCM via Android Keystore alias `privacy_space_master`
- Content keys: `privacy_space_content_v{n}` in Keystore

## Session Rules

| Event | Action |
|-------|--------|
| App background (`onStop`) | Lock session + clear note unlocks |
| Inactivity timeout (default 5 min) | Lock on resume |
| Panic logout | Clear prefs + lock |
| Fake PIN unlock | `SessionState.FAKE_VAULT` (scoped data) |
| Locked note open | PIN re-auth via `UnlockNoteUseCase` |

## Threat Mitigations

| Threat | Mitigation | Status |
|--------|------------|--------|
| Plaintext PIN | PBKDF2 100k iterations | ✅ |
| Screenshot | FLAG_SECURE calculator + vault | ✅ |
| Root | `DeviceSecurityChecker` | ✅ Detect only |
| Emulator | `DeviceSecurityChecker` | ✅ Detect only |
| DB extraction | SQLCipher + Room | ✅ v3.0.0 |
| Note content at rest | AES-GCM encrypted column | ✅ |
| File exfiltration | Encrypted blobs, no source paths | ✅ |
| Recent apps preview | FLAG_SECURE | ✅ |
| Timing attacks | `MessageDigest.isEqual` | ✅ |
| Destructive DB migration | Explicit migrations 1→4 | ✅ |

## Data Flow

```
PIN unlock → SessionManager → Repository scope (real/fake)
Note write → ContentEncryptionService → encryptedContent + searchText
File import → SAF URI → encryptStream → filesDir/vault/*.enc
DB access → DatabaseKeyManager → SQLCipher SupportFactory
```

## Secure Logout

`SessionManager.panicLogout()` → lock + `PinManager.clearAll()` (wipes credentials and DB passphrase; user must re-setup)

See also: [ENCRYPTION.md](ENCRYPTION.md), [DATABASE_MIGRATION_PLAN.md](DATABASE_MIGRATION_PLAN.md)
