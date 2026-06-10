package com.calculator.vault.privacy.core.clone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

    public boolean isSecureFolderAvailable() {
        return isPackageInstalled(SECURE_FOLDER_PACKAGE);
    }

    public Intent buildDualMessengerSettingsIntent() {
        Intent fromPackage = findActivityInPackage(DUAL_MESSENGER_PACKAGE, "dual");
        if (fromPackage != null) {
            return fromPackage;
        }
        String[] activityNames = {
                "com.samsung.android.da.daagent.DualMessengerSettingActivity",
                "com.samsung.android.da.daagent.settings.DualMessengerSettingsActivity",
                "com.samsung.android.da.daagent.dualsettings.DualSettingsActivity",
                "com.samsung.android.da.daagent.DualAppActivity",
        };
        for (String activity : activityNames) {
            Intent intent = explicitIntent(DUAL_MESSENGER_PACKAGE, activity);
            if (intent != null) {
                return intent;
            }
        }
        String[] actions = {
                "com.samsung.android.da.action.DUAL_APP_SETTINGS",
                "com.samsung.android.settings.action.DUAL_MESSENGER_SETTINGS",
        };
        for (String action : actions) {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                return intent;
            }
        }
        return buildSamsungSettingsFallback(
                "Open Settings → Advanced features → Dual Messenger (or Dual Apps)."
        );
    }

    public Intent buildSecureFolderIntent() {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(SECURE_FOLDER_PACKAGE);
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return launch;
        }
        Intent fromPackage = findActivityInPackage(SECURE_FOLDER_PACKAGE, "setup");
        if (fromPackage != null) {
            return fromPackage;
        }
        return buildSamsungSettingsFallback(
                "Open Settings → Security and privacy → Secure Folder."
        );
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
        boolean inPrimary = hasLauncherActivities(launcherApps, packageName, primary);
        UserHandle bestMatch = null;
        for (UserHandle profile : userManager.getUserProfiles()) {
            if (profile.equals(primary)) {
                continue;
            }
            if (hasLauncherActivities(launcherApps, packageName, profile)) {
                if (inPrimary) {
                    return profile;
                }
                bestMatch = profile;
            }
        }
        return bestMatch;
    }

    public boolean isInstalledInSecondaryProfile(String packageName) {
        return findSecondaryAppUser(packageName) != null;
    }

    public void launchSecondaryApp(String packageName) {
        UserHandle user = findSecondaryAppUser(packageName);
        if (user == null) {
            throw new IllegalStateException(
                    "No dual copy found. Enable it in Dual Messenger or move the app into Secure Folder first."
            );
        }
        LauncherApps launcherApps = context.getSystemService(LauncherApps.class);
        if (launcherApps == null) {
            throw new IllegalStateException("Launcher unavailable");
        }
        List<LauncherActivityInfo> activities = launcherApps.getActivityList(packageName, user);
        if (activities == null || activities.isEmpty()) {
            throw new IllegalStateException(
                    "No dual copy found. Enable it in Dual Messenger or move the app into Secure Folder first."
            );
        }
        LauncherActivityInfo target = activities.get(activities.size() - 1);
        try {
            launcherApps.startMainActivity(target.getComponentName(), user, null, null);
        } catch (SecurityException firstFailure) {
            launcherApps.startMainActivity(
                    target.getComponentName(),
                    Process.myUserHandle(),
                    null,
                    null
            );
        }
    }

    private Intent findActivityInPackage(String packageName, String nameHint) {
        PackageManager pm = context.getPackageManager();
        Intent probe = new Intent(Intent.ACTION_MAIN);
        probe.addCategory(Intent.CATEGORY_DEFAULT);
        probe.setPackage(packageName);
        List<ResolveInfo> matches = pm.queryIntentActivities(probe, PackageManager.MATCH_DEFAULT_ONLY);
        Intent fallback = null;
        for (ResolveInfo match : matches) {
            if (match.activityInfo == null) {
                continue;
            }
            Intent intent = explicitIntent(match.activityInfo.packageName, match.activityInfo.name);
            if (intent == null) {
                continue;
            }
            String className = match.activityInfo.name.toLowerCase();
            if (className.contains(nameHint.toLowerCase())) {
                return intent;
            }
            if (fallback == null) {
                fallback = intent;
            }
        }
        return fallback;
    }

    private Intent explicitIntent(String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setComponent(new ComponentName(packageName, className));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            return intent;
        }
        return null;
    }

    private Intent buildSamsungSettingsFallback(String hint) {
        Intent settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
        settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        settings.putExtra(":settings:show_fragment_title", hint);
        return settings;
    }

    private boolean hasLauncherActivities(LauncherApps launcherApps, String packageName, UserHandle user) {
        List<LauncherActivityInfo> activities = launcherApps.getActivityList(packageName, user);
        return activities != null && !activities.isEmpty();
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
