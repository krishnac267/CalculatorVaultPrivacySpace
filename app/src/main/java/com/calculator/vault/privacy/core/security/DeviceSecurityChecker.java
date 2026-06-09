package com.calculator.vault.privacy.core.security;

import android.os.Build;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class DeviceSecurityChecker {
    @Inject
    public DeviceSecurityChecker() {}

    public boolean isRooted() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/system/xbin/su",
                "/system/bin/su",
                "/sbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su"
        };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        String tags = Build.TAGS;
        return tags != null && tags.contains("test-keys");
    }

    public boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
