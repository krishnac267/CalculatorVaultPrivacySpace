# E2E Test Report — v3.0.0

## Suite Inventory

| Suite | Tests | Status |
|-------|-------|--------|
| `AuthenticationE2ETest` | 4 | Implemented |
| `NotesE2ETest` | 2 | Implemented |
| `FakeVaultIsolationE2ETest` | 1 | Implemented |
| `FileVaultIntegrationTest` | 1 | Integration (import path) |
| `NotificationVaultIntegrationTest` | 2 | Integration |
| `SessionSecurityTest` | 1 | Security |
| `DatabaseMigrationInstrumentedTest` | 2 | Migration |

## Coverage Matrix

| Requirement | Covered | Notes |
|-------------|---------|-------|
| First launch setup | ✅ | `firstLaunch_showsSetup_thenCalculatorAfterFinish` |
| PIN creation | ✅ | Setup flow |
| Fake PIN | ✅ | `fakePinUnlock_opensFakeVault` |
| PIN unlock | ✅ | `realPinUnlock_opensVaultHome` |
| Biometric unlock | ⏭ | Requires enrolled emulator fingerprint |
| Panic logout | ⏭ | Manual / future test |
| Session timeout | ⏭ | Future: advance clock |
| Background lock | ✅ | `backgroundLock_returnsToCalculatorAndHidesVault` |
| Note CRUD | ✅ Partial | Create + lock; delete/edit manual |
| Note restore | N/A | Feature not implemented |
| File SAF import UI | ⏭ | Integration covers encrypted storage |
| Notification capture | ⏭ | Service requires listener permission |
| Fake vault isolation | ✅ | Notes + notifications scope |

## Execution

```powershell
.\gradlew connectedDebugAndroidTest
```

Report: `app/build/reports/androidTests/connected/index.html`

## Pass Criteria

- Zero crashes in implemented suites
- Vault content not visible after lock
- Encrypted note content verified in DB
