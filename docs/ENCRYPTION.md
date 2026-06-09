# Encryption Documentation

## Overview

Calculator Vault Privacy Space uses layered encryption:

| Layer | Mechanism | Protects |
|-------|-----------|----------|
| Database | SQLCipher 4.x + Room | All Room tables and metadata |
| Note bodies | AES-256-GCM (Android Keystore) | Note `encryptedContent` |
| Vault files | AES-256-GCM streaming | Files in `filesDir/vault/*.enc` |
| Preferences | EncryptedSharedPreferences | PIN hashes, DB passphrase, settings |

## Database Key (`DatabaseKeyManager`)

- 32-byte random passphrase generated on first launch
- Stored Base64-encoded in EncryptedSharedPreferences (`db_passphrase_v1`)
- Passed to SQLCipher via `SupportFactory`
- Independent of user PIN (PIN unlocks session, not DB key directly)

## Content Key (`ContentEncryptionService`)

- Keystore alias: `privacy_space_content_v{n}`
- AES/GCM/NoPadding, 12-byte IV prepended to ciphertext
- Used for note bodies and file blobs
- Active version tracked in prefs (`content_key_version`)

### Note Encryption Flow

**Write:** plaintext → encrypt → `encryptedContent`; `searchText` updated separately  
**List/search:** title + `searchText` only — content never decrypted  
**Read:** decrypt `encryptedContent` only when note is unlocked in session  
**Locked notes:** require PIN re-entry via `NoteUnlockSession`

## File Vault Encryption

1. User picks file via SAF (`OpenDocument`)
2. Bytes streamed through `ContentEncryptionService.encryptStream`
3. Stored as `{uuid}.enc` under app-private `filesDir/vault/`
4. Original URI/path never persisted
5. Preview decrypts to cache dir, served via `FileProvider`

Max import size: **100 MB**

## Key Rotation (`KeyRotationManager`)

Manual rotation (Settings / Security Center integration point):

1. Generate `privacy_space_content_v{n+1}` in Keystore
2. Decrypt all notes/files with old key
3. Re-encrypt with new key
4. Bump `content_key_version`

Database passphrase rotation requires export/re-import (future enhancement).

## Secure Memory

`SecureMemory.wipe()` clears char/byte arrays after PIN handling where applicable.

## FLAG_SECURE

`SecureScreenEffect` applied to:

- Calculator (after setup complete)
- All vault shell routes

Prevents screenshots and recent-apps preview leakage.

## Threat Model Notes

- Root/emulator reduces Keystore guarantees (detected in Security Center)
- Search metadata (`searchText`) is plaintext by design for local FTS
- Decrypted preview cache is ephemeral under `cacheDir/vault_preview/`
