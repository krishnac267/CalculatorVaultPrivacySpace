# Privacy Policy — Calculator Vault Privacy Space

**Effective date:** June 7, 2026  
**App:** Calculator Vault Privacy Space (`com.calculator.vault.privacy`)  
**Version:** 3.0.0

## Summary

Calculator Vault Privacy Space stores your vault data **locally on your device**. We do not operate a cloud vault server and do not sell your vault contents. This app does **not** include ads, analytics SDKs, or in-app billing in the current release.

## Data stored on your device

| Data | How it is protected |
|------|---------------------|
| PIN credentials | PBKDF2 hash only — never stored in plaintext |
| Vault app bookmarks | Encrypted SQLCipher database (Room) |
| Private notes | AES-256-GCM encrypted content in SQLCipher database |
| Vault files (images, videos, documents) | Encrypted blobs in app-private storage |
| Security settings and session metadata | EncryptedSharedPreferences + SQLCipher |
| Intruder log (optional) | Timestamps and optional local photo paths in SQLCipher |
| Notification copies (optional) | Stored in vault when Notification Access is enabled |

## Data we do not collect

- Your PIN in plaintext
- Vault note or file contents sent to our servers (we have none)
- Contents of third-party apps you launch from the vault
- Personal data for advertising or analytics (no AdMob/Firebase in v3.0.0)

## Optional features and permissions

| Permission / access | Why we ask | Your control |
|---------------------|------------|--------------|
| **Camera** | Optional intruder photo after failed PIN attempts | Off by default; enable in Security settings |
| **Biometric (USE_BIOMETRIC)** | Optional fingerprint/face unlock after PIN setup | Off until you enable it |
| **Notification listener** | Optional: copy notifications from selected apps into your vault | Requires explicit system grant in Android settings |
| **Internet** | Reserved for future updates; no third-party analytics in v3.0.0 | Not used for vault sync |
| **POST_NOTIFICATIONS** | Local alerts from the app (e.g. session/security) | Can be denied on Android 13+ |

## Encryption

- **Database:** SQLCipher (AES-256) with a device-bound passphrase in EncryptedSharedPreferences
- **Notes & files:** AES-256-GCM via Android Keystore (`ContentEncryptionService`)
- **Backups:** Disabled (`android:allowBackup="false"`) so vault data is not copied to Google backup

## Screenshot protection

Vault and calculator (after setup) screens use `FLAG_SECURE` to reduce screenshot and recent-apps preview leakage.

## Decoy vault

If you configure a secondary PIN, it unlocks a separate scoped vault. Data in the real and decoy vaults is isolated.

## Panic logout

Security settings may offer panic logout, which clears stored credentials and locks the app. You must complete setup again afterward.

## Children's privacy

This app is not directed at children under 13.

## Data retention and deletion

All vault data remains on your device until you delete it in the app or uninstall the app. Uninstalling removes app-private storage including the encrypted database and files.

## Third-party services (current release)

**None.** v3.0.0 does not integrate Google AdMob, Firebase Analytics, or Google Play Billing. Premium upsell is disabled (`PREMIUM_ENABLED = false`); all users receive full vault limits.

## Changes to this policy

We may update this policy when features change (e.g. ads, billing, or cloud sync). Continued use after the effective date constitutes acceptance.

## Contact

Replace before publication: **privacy@yourdomain.com**
