# Calculator Vault – Privacy Space Architecture

## Overview

Hybrid Android architecture: **Kotlin + Jetpack Compose** for presentation, **Java 17** for domain/data/core business logic. Single `:app` module with strict package layering.

**Application ID:** `com.calculator.vault.privacy`  
**Package root:** `com.calculator.vault.privacy`

## Module Architecture

```
CalculatorVaultPrivacySpace/
├── app/                    # Single deployable module
├── gradle/libs.versions.toml
├── settings.gradle.kts
└── docs/                   # Architecture & roadmap
```

Future scaling path (optional):

| Module | Language | Responsibility |
|--------|----------|----------------|
| `:app` | Kotlin | Compose UI, ViewModels, Hilt entry |
| `:domain` | Java | Use cases, models, interfaces |
| `:data` | Java | Room, repositories, datasources |
| `:core` | Java | Security, session, utilities |

Current MVP uses a **monolithic `:app` module** with package-level separation to ship faster while preserving Clean Architecture boundaries.

## Package Structure

```
com.calculator.vault.privacy/
├── PrivacySpaceApplication.kt
├── presentation/           # Kotlin only
│   ├── MainActivity.kt
│   ├── compose/theme/
│   ├── navigation/
│   ├── screens/
│   ├── viewmodels/
│   └── components/
├── domain/                 # Java
│   ├── model/
│   ├── interfaces/
│   ├── usecases/
│   └── validators/
├── data/                   # Java
│   ├── repositories/
│   ├── datasource/
│   ├── database/
│   └── DataModule.java
└── core/                   # Java
    ├── security/
    ├── session/
    └── utilities/
```

## Dependency Graph

```
Presentation (Kotlin ViewModels)
        │
        ▼
Domain Use Cases (Java)
        │
        ▼
Repository Interfaces (Java)
        │
        ▼
Repository Implementations (Java)
        │
        ├── Room DAOs / Entities
        ├── EncryptedSharedPreferences (PinManager)
        └── SessionManager
```

**Rule:** Dependencies point inward. UI never imports Room entities or `PinManager` directly.

## Layer Responsibilities

| Layer | Language | Contains |
|-------|----------|----------|
| Presentation | Kotlin | Screens, navigation, UI state, animations, theme |
| Domain | Java | Use cases, models, validators, repository contracts |
| Data | Java | Room, repository impls, entity mappers |
| Core | Java | Keystore, encrypted prefs, session, calculator engine |

## DI (Hilt)

- `@HiltAndroidApp` — `PrivacySpaceApplication`
- `@AndroidEntryPoint` — `MainActivity`
- `@HiltViewModel` — all ViewModels
- Modules: `DataModule`, `CoreModule`

## Tech Stack

- Compose Material 3, Navigation Compose
- Room 2.6
- Hilt 2.52
- EncryptedSharedPreferences + Android Keystore
- Retrofit/OkHttp (scaffolded for future sync/backup)
- Coroutines (ViewModel scope only; Java stays synchronous)

## Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`
