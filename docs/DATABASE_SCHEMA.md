# Database Schema

Room database: `privacy_space.db` (v1)

## Tables

### vault_apps

| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER PK AUTO | |
| packageName | TEXT | Indexed |
| label | TEXT | Display name |
| category | TEXT | User/system category |
| favorite | INTEGER (bool) | |
| lastLaunchedAt | INTEGER | Epoch ms |
| launchCount | INTEGER | |

### secure_notes

| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER PK AUTO | |
| title | TEXT | |
| content | TEXT | Plaintext in DB v1; encrypt at rest in Phase 3 |
| favorite | INTEGER (bool) | |
| locked | INTEGER (bool) | Per-note lock flag |
| createdAt | INTEGER | |
| updatedAt | INTEGER | |

### vault_notifications

| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER PK AUTO | |
| packageName | TEXT | Indexed |
| appLabel | TEXT | |
| title | TEXT | |
| body | TEXT | |
| postedAt | INTEGER | |
| read | INTEGER (bool) | |

### vault_files

| Column | Type | Notes |
|--------|------|-------|
| id | INTEGER PK AUTO | |
| displayName | TEXT | |
| mimeType | TEXT | |
| vaultPath | TEXT | Internal app storage path |
| sizeBytes | INTEGER | |
| importedAt | INTEGER | |

## DAOs

- `VaultAppDao` — CRUD, search, favorites, recent
- `SecureNoteDao` — CRUD, search, favorite/lock toggles
- `VaultNotificationDao` — list, search, group by package, mark read
- `VaultFileDao` — list, total size, delete

## Future (Phase 3+)

- SQLCipher encryption via `SupportFactory`
- `intruder_logs` table for failed PIN captures
- `premium_entitlements` table for offline billing cache
