# Calculator Vault Privacy Space — Play Store Listing

**Package:** `com.calculator.vault.privacy`  
**Version:** 3.0.0

## Short description (80 chars max)

Privacy vault behind a calculator PIN. Encrypted notes, files, and apps.

## Full description

**Calculator Vault Privacy Space** is a privacy vault and secure app launcher — not an "app hider."

The app opens as a normal calculator. Enter your PIN to access your personal privacy space.

### What you get

- **Disguised calculator entry** — looks like an everyday calculator
- **PIN-protected privacy vault** — organize favorite and recent apps
- **Private notes** — AES-256-GCM encrypted, stored in a SQLCipher database
- **File vault** — import and store images, videos, and documents encrypted on device
- **Optional notification vault** — store copies of notifications when you grant Notification Access
- **Biometric unlock** — optional fingerprint/face after setup
- **Decoy vault** — optional second PIN for a separate scoped vault
- **Intruder detection log** — optional camera capture after failed PIN attempts
- **Screenshot protection** on calculator (after setup) and vault screens
- **Session auto-lock** — locks when you leave the app or after inactivity

### Honest privacy positioning

Calculator Vault does **not** remove apps from Android. Installed apps remain on your device. Vault apps are accessed through your secure PIN-protected experience.

### Current release (v3.0.0)

- **Free full access** — no app limits, no ads, no in-app purchases in this release
- **Local-only storage** — no cloud vault server
- Premium billing is planned for a future update and is **not** active in v3.0.0

## Keywords

calculator vault, privacy vault, secure launcher, app lock, pin vault, private space, encrypted notes, file vault, decoy vault

## Feature graphic recommendation

- Split design: calculator UI on left, blurred vault grid on right
- Tagline: "Your privacy space behind a calculator"
- Avoid: "hide apps", "invisible", "remove from phone"

## Screenshot recommendations

1. Calculator home screen (disguise)
2. PIN entry via calculator keys
3. Vault dashboard with favorites/recent
4. Private notes list
5. File vault (images/documents tabs)
6. Security settings (session timeout, biometric, intruder)
7. Notification vault (with listener disclosure)

## Review notes for Google Play

- **Notification listener:** Optional feature; user must enable in Android system settings. Used only to copy notifications into the local encrypted vault — not sent off device.
- **Camera:** Optional intruder capture only; off by default.
- **No deceptive behavior:** Calculator performs real arithmetic; PIN unlock is documented in listing.
