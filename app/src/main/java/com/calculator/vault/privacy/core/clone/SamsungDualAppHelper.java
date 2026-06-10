package com.calculator.vault.privacy.core.clone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class SamsungDualAppHelper {
    private static final String SAMSUNG_MANUFACTURER = "samsung";
    private static final String DUAL_MESSENGER_PACKAGE = "com.samsung.android.da.daagent";
    private static final String SECURE_FOLDER_PACKAGE = "com.samsung.knox.securefolder";

    private final Context context;

    @Inject
    public SamsungDualAppHelper(@ApplicationContext Context context) {
        this.context = context;
    }

    public boolean isSamsungDevice() {
        return SAMSUNG_MANUFACTURER.equalsIgnoreCase(Build.MANUFACTURER);
    }

    public boolean isDualMessengerAvailable() {
        return isPackageInstalled(DUAL_MESSENGER_PACKAGE);
    }

    public boolean isKnoxSecuredDevice() {
        if (!isSamsungDevice()) {
            return false;
        }
        return isDualMessengerAvailable()
                || isPackageInstalled(SECURE_FOLDER_PACKAGE)
                || hasSecondaryUserProfile()
                || hasKnoxRuntime();
    }

    public Intent buildDualMessengerSettingsIntent() {
        PackageManager pm = context.getPackageManager();
        String[] activities = {
                "com.samsung.android.da.daagent.DualMessengerSettingActivity",
                "com.samsung.android.da.daagent.settings.DualMessengerSettingsActivity",
        };
        for (String activity : activities) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(DUAL_MESSENGER_PACKAGE, activity));
            if (intent.resolveActivity(pm) != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
            }
        }
        Intent fallback = new Intent(android.provider.Settings.ACTION_SETTINGS);
        fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return fallback;
    }

    public UserHandle findSecondaryAppUser(String packageName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return null;
        }
        UserManager userManager = context.getSystemService(UserManager.class);
        LauncherApps launcherApps = context.getSystemService(LauncherApps.class);
        if (userManager == null || launcherApps == null) {
            return null;
        }
        UserHandle primary = Process.myUserHandle();
        for (UserHandle profile : userManager.getUserProfiles()) {
            if (profile.equals(primary)) {
                continue;
            }
            List<LauncherActivityInfo> activities = launcherApps.getActivityList(packageName, profile);
            if (activities != null && !activities.isEmpty()) {
                return profile;
            }
        }
        return null;
    }

    public boolean isInstalledInSecondaryProfile(String packageName) {
        return findSecondaryAppUser(packageName) != null;
    }

    public void launchSecondaryApp(String packageName) {
        UserHandle user = findSecondaryAppUser(packageName);
        if (user == null) {
            throw new IllegalStateException("Dual copy is not installed for this app");
        }
        LauncherApps launcherApps = context.getSystemService(LauncherApps.class);
        if (launcherApps == null) {
            throw new IllegalStateException("Launcher unavailable");
        }
        List<LauncherActivityInfo> activities = launcherApps.getActivityList(packageName, user);
        if (activities == null || activities.isEmpty()) {
            throw new IllegalStateException("Dual copy is not installed for this app");
        }
        launcherApps.startMainActivity(activities.get(0).getComponentName(), user, null, null);
    }

    private boolean hasSecondaryUserProfile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        UserManager userManager = context.getSystemService(UserManager.class);
        if (userManager == null) {
            return false;
        }
        return userManager.getUserProfiles().size() > 1;
    }

    private boolean hasKnoxRuntime() {
        try {
            Class.forName("com.samsung.android.knox.EnterpriseDeviceManager");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }
}
