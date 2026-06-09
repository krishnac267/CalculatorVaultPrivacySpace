# Google Play Data Safety Form — v3.0.0

Use this checklist when completing the Play Console **Data safety** section for `com.calculator.vault.privacy`.

## Overview

| Question | Answer |
|----------|--------|
| Does your app collect or share user data? | **No collection to developer servers** — all vault data stays on device |
| Is data encrypted in transit? | **N/A** — no vault data transmitted |
| Is data encrypted at rest? | **Yes** — SQLCipher database + AES-GCM for notes/files |
| Can users request data deletion? | **Yes** — delete in app or uninstall |
| Independent security review | No |

## Data types — declare as collected **on device only** (not shared)

Play Console may still ask about data **processed** locally. Declare accurately:

### Personal info

| Type | Collected? | Shared? | Purpose | Optional? |
|------|------------|---------|---------|-----------|
| Name, email, address | No | No | — | — |
| User IDs | No | No | — | — |

### App activity / content

| Type | Collected? | Shared? | Purpose | Optional? |
|------|------------|---------|---------|-----------|
| App interactions | No (no analytics SDK) | No | — | — |
| In-app search history | No | No | — | — |
| Other user-generated content | **Yes (on device)** | **No** | Notes, files, app bookmarks in vault | Core feature |

### Photos and videos

| Type | Collected? | Shared? | Purpose | Optional? |
|------|------------|---------|---------|-----------|
| Photos | **Yes (on device)** | **No** | User-imported file vault; optional intruder capture | File vault optional; intruder optional |
| Videos | **Yes (on device)** | **No** | User-imported file vault | Optional |

### Device or other IDs

| Type | Collected? | Shared? | Purpose | Optional? |
|------|------------|---------|---------|-----------|
| Device IDs | No | No | — | — |

## Permissions mapping

| Permission | Data safety note |
|------------|------------------|
| CAMERA | Optional intruder photos stored locally only |
| USE_BIOMETRIC | Local authentication only; no biometric data leaves device |
| Notification listener | Notification text stored locally in vault when user enables |
| INTERNET | Declared; not used for vault sync in v3.0.0 |
| POST_NOTIFICATIONS | Local app notifications only |

## Security practices to highlight

- Data encrypted at rest (SQLCipher + Keystore)
- Users can delete all data (in-app delete + uninstall)
- `allowBackup="false"`
- No third-party ad or analytics SDKs in v3.0.0

## Privacy policy URL

Publish `docs/legal/PRIVACY_POLICY.md` to a public HTTPS URL before submission and enter that URL in Play Console.

## Third-party SDK disclosure (v3.0.0)

**None required** — no AdMob, Firebase, or Play Billing in current build.

When billing/ads are added in a future release, update this form and the privacy policy before shipping that version.
