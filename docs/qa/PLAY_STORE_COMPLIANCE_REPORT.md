# Play Store Compliance Report — v3.0.0 (updated)

| Policy | Status | Evidence |
|--------|--------|----------|
| Privacy policy | ✅ Draft ready | `docs/legal/PRIVACY_POLICY.md` — SQLCipher, file vault, notification listener, no third-party SDKs |
| Data safety form | ✅ Checklist ready | `docs/store/DATA_SAFETY_FORM.md` |
| Terms of service | ✅ Draft ready | `docs/legal/TERMS_OF_SERVICE.md` |
| Store listing | ✅ Updated | `docs/store/PLAY_STORE_LISTING.md` — no premium/ads claims for v3 |
| Permissions justification | ✅ | CAMERA, BIOMETRIC, notification listener documented |
| No backup | ✅ | `android:allowBackup="false"` |
| Target SDK 35 | ✅ | `targetSdk = 35` |
| In-app billing | ✅ N/A | Premium disabled; free full tier — accurately declared |
| Deceptive behavior | ✅ | Calculator disguise documented; real calculator math |
| FLAG_SECURE | ✅ | Documented in privacy policy |
| Families policy | N/A | Not child-directed |

## Action Items Before Submission

1. Publish privacy policy to public HTTPS URL; replace placeholder contact email
2. Complete Data safety form in Play Console using `DATA_SAFETY_FORM.md`
3. Sign release APK with upload key (not debug keystore)
4. Complete [MANUAL_SIGNOFF_CHECKLIST.md](../qa/MANUAL_SIGNOFF_CHECKLIST.md) on physical device
5. Provide demo video for review (calculator → PIN → vault)

## Release build

- `.\gradlew assembleRelease` — **PASS**
- Output: `app/build/outputs/apk/release/app-release-unsigned.apk`
- ProGuard mapping: `app/build/outputs/mapping/release/`
