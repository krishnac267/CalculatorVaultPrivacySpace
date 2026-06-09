# Database Migration Plan

Privacy Space database: `privacy_space.db` — **version 4** (SQLCipher encrypted)

## Version History

| Version | Changes |
|---------|---------|
| **1** | Initial schema: `vault_apps`, `secure_notes`, `vault_notifications`, `vault_files` |
| **2** | `vaultScope` on apps/notes; `intruder_logs` table |
| **3** | `photoPath` on intruder logs; `vaultScope` on notifications/files |
| **4** | Note `encryptedContent`, `searchText`; file `category`, `favorite`, `deleted`, `internalFileName` |

## SQLCipher Integration (v3.0.0)

- Room opens via `SupportFactory` with a 256-bit passphrase from `DatabaseKeyManager`
- Passphrase stored in EncryptedSharedPreferences (never derived from PIN directly)
- **Destructive fallback removed** — upgrades use explicit migrations only

## Migration Paths

```
v1 ──MIGRATION_1_2──► v2 ──MIGRATION_2_3──► v3 ──MIGRATION_3_4──► v4
```

Implementations live in `DatabaseMigrations.java`.

## Post-Migration: Note Encryption

After schema v4, `NoteEncryptionMigrator` runs on app startup:

1. Finds notes where `encryptedContent` is empty but legacy `content` exists
2. Encrypts body via `ContentEncryptionService` (AES-256-GCM / Keystore)
3. Writes `searchText` metadata (title + lowercase content tokens for search)
4. Clears legacy `content` column

## File Vault Migration (v3 → v4)

New columns default safely:

- `category` → `DOCUMENT`
- `favorite` / `deleted` → `0`
- `internalFileName` → `''` (legacy rows with external `vaultPath` are ignored; re-import recommended)

## Rollback Policy

No automatic downgrade. Panic logout clears EncryptedSharedPreferences (including DB passphrase) — user must set up vault again.

## Testing Checklist

- [ ] Fresh install on v4 creates encrypted DB
- [ ] Upgrade from v3 preserves notes/apps/notifications
- [ ] Note encryption migrator encrypts legacy plaintext
- [ ] SQLCipher open fails with wrong passphrase (expected)
