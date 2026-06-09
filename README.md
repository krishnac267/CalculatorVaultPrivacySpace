# Calculator Vault – Privacy Space

Production-oriented rebuild of Calculator Vault (v3.1.0). Disguised calculator with encrypted vault, notes, files, fake vault, app cloning, and session lock.

**Package ID:** `com.calculator.vault.privacy` · **Min SDK:** 26

## Download APK (phone testing)

Debug build for sideloading on a physical device:

- **In repo:** [`releases/CalculatorVaultPrivacy-v3.1.0-debug.apk`](releases/CalculatorVaultPrivacy-v3.1.0-debug.apk)
- **GitHub Release:** [v3.1.0-debug](https://github.com/krishnac267/CalculatorVaultPrivacySpace/releases/tag/v3.1.0-debug)

### Install on your phone

1. Download the APK (GitHub → `releases/` folder, or clone repo).
2. On the phone: **Settings → Security** → allow installs from your browser/files app.
3. Open the APK and tap **Install**.

Or with USB debugging:

```bash
adb install releases/CalculatorVaultPrivacy-v3.1.0-debug.apk
```

### First launch

1. Complete setup (secret PIN, confirm PIN).
2. On the calculator, enter your PIN and press **=** to open the vault.

### v3.1.0 highlights

- **App Clone Space** — enable Clone Space in Apps, then clone apps with separate accounts/data (Android work profile)
- **Improved app picker** — search all installed apps (no 50-app limit)

## Architecture

- **UI:** Kotlin + Jetpack Compose + Material 3
- **Business logic:** Java 17 (domain, data, core)
- **DI:** Hilt | **DB:** Room | **Security:** Keystore + EncryptedSharedPreferences

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for full design.

## Build

```bash
./gradlew assembleDebug
```

## Project Layout

```
app/src/main/java/com/calculator/vault/privacy/
├── presentation/   # Kotlin – Compose only
├── domain/         # Java – use cases & models
├── data/           # Java – Room & repositories
└── core/           # Java – security & session
```

## Docs

- [Architecture](docs/ARCHITECTURE.md)
- [Database Schema](docs/DATABASE_SCHEMA.md)
- [Navigation Graph](docs/NAVIGATION_GRAPH.md)
- [Security Architecture](docs/SECURITY_ARCHITECTURE.md)
- [Implementation Roadmap](docs/IMPLEMENTATION_ROADMAP.md)
