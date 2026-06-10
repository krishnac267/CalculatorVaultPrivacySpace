package com.calculator.vault.privacy.core.clone;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserHandle;

import com.calculator.vault.privacy.domain.model.CloneSpaceAlternative;
import com.calculator.vault.privacy.domain.model.CloneSpaceStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class WorkProfileCloneManager {
    public static final String EXTRA_PACKAGE_NAME = "clone_package_name";
    public static final String EXTRA_INSTALL_SUCCESS = "clone_install_success";
    public static final int REQUEST_CLONE_INSTALL = 9101;

    private final Context context;
    private final SamsungDualAppHelper samsungDualAppHelper;

    @Inject
    public WorkProfileCloneManager(
            @ApplicationContext Context context,
            SamsungDualAppHelper samsungDualAppHelper
    ) {
        this.context = context;
        this.samsungDualAppHelper = samsungDualAppHelper;
    }

    public ComponentName getAdminComponent() {
        return new ComponentName(context, CloneDeviceAdminReceiver.class);
    }

    public CloneSpaceStatus getStatus() {
        UserHandle workUser = findWorkProfileUser();
        if (workUser != null) {
            return new CloneSpaceStatus(
                    true,
                    true,
                    "Clone Space is ready. Cloned apps keep separate accounts and storage."
            );
        }
        if (shouldUseSamsungDualApps()) {
            return new CloneSpaceStatus(
                    samsungDualAppHelper.isDualMessengerAvailable(),
                    false,
                    "Samsung Knox blocks in-app Clone Space setup on this phone. "
                            + "Use Samsung Dual Messenger instead: Settings → Advanced features "
                            + "→ Dual Messenger. Turn on the app you want, then come back here "
                            + "and tap Clone.",
                    CloneSpaceAlternative.SAMSUNG_DUAL_MESSENGER
            );
        }
        if (!isProvisioningAllowed()) {
            return new CloneSpaceStatus(
                    false,
                    false,
                    "Clone Space is blocked on this phone. Remove any existing work/school "
                            + "profile in Settings → Accounts, then try again."
            );
        }
        if (!canProvisionCloneSpace()) {
            return new CloneSpaceStatus(
                    false,
                    false,
                    "This device does not support Clone Space setup."
            );
        }
        return new CloneSpaceStatus(
                false,
                true,
                "Enable Clone Space to run a second copy of apps with separate data."
        );
    }

    public boolean shouldUseSamsungDualApps() {
        return samsungDualAppHelper.isSamsungDevice()
                && samsungDualAppHelper.isKnoxSecuredDevice()
                && findWorkProfileUser() == null;
    }

    public Intent buildSamsungDualMessengerIntent() {
        return samsungDualAppHelper.buildDualMessengerSettingsIntent();
    }

    public boolean isProvisioningAllowed() {
        if (shouldUseSamsungDualApps()) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        DevicePolicyManager dpm = context.getSystemService(DevicePolicyManager.class);
        if (dpm == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return dpm.isProvisioningAllowed(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
        }
        return canProvisionCloneSpace();
    }

    public Intent buildProvisioningIntent() {
        ComponentName admin = getAdminComponent();
        Intent intent = new Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE);
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME, admin);
        intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_SKIP_EDUCATION_SCREENS, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intent.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_ALLOW_OFFLINE, true);
        }
        return intent;
    }

    public boolean canProvisionCloneSpace() {
        if (shouldUseSamsungDualApps()) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        Intent intent = buildProvisioningIntent();
        return intent.resolveActivity(pm) != null;
    }

    public void startCloneInstall(Activity activity, String packageName) {
        if (shouldUseSamsungDualApps()) {
            if (samsungDualAppHelper.isInstalledInSecondaryProfile(packageName)) {
                return;
            }
            activity.startActivity(samsungDualAppHelper.buildDualMessengerSettingsIntent());
            return;
        }
        UserHandle workUser = findWorkProfileUser();
        if (workUser == null) {
            throw new IllegalStateException("Clone Space is not enabled");
        }
        Intent intent = new Intent(activity, CloneInstallActivity.class);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            android.content.pm.CrossProfileApps crossProfileApps =
                    activity.getSystemService(android.content.pm.CrossProfileApps.class);
            if (crossProfileApps != null && crossProfileApps.canInteractAcrossProfiles()) {
                crossProfileApps.startActivity(intent, workUser, activity, null);
                return;
            }
        }
        activity.startActivityForResult(intent, REQUEST_CLONE_INSTALL);
    }

    public boolean isCloned(String packageName) {
        UserHandle workUser = findWorkProfileUser();
        if (workUser != null) {
            LauncherApps launcherApps = context.getSystemService(LauncherApps.class);
            if (launcherApps == null) {
                return false;
            }
            List<LauncherActivityInfo> activities = launcherApps.getActivityList(packageName, workUser);
            return activities != null && !activities.isEmpty();
        }
        if (shouldUseSamsungDualApps()) {
            return samsungDualAppHelper.isInstalledInSecondaryProfile(packageName);
        }
        return false;
    }

    public void launchClone(String packageName) {
        UserHandle workUser = findWorkProfileUser();
        if (workUser != null) {
            LauncherApps launcherApps = context.getSystemService(LauncherApps.class);
            if (launcherApps == null) {
                throw new IllegalStateException("Launcher unavailable");
            }
            List<LauncherActivityInfo> activities = launcherApps.getActivityList(packageName, workUser);
            if (activities == null || activities.isEmpty()) {
                throw new IllegalStateException("Cloned app is not installed");
            }
            launcherApps.startMainActivity(activities.get(0).getComponentName(), workUser, null, null);
            return;
        }
        if (shouldUseSamsungDualApps()) {
            samsungDualAppHelper.launchSecondaryApp(packageName);
            return;
        }
        throw new IllegalStateException("Clone Space is not enabled");
    }

    public UserHandle findWorkProfileUser() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return null;
        }
        android.content.pm.CrossProfileApps crossProfileApps =
                context.getSystemService(android.content.pm.CrossProfileApps.class);
        if (crossProfileApps == null || !crossProfileApps.canInteractAcrossProfiles()) {
            return null;
        }
        List<UserHandle> targets = crossProfileApps.getTargetUserProfiles();
        if (targets.isEmpty()) {
            return null;
        }
        return targets.get(0);
    }
}
