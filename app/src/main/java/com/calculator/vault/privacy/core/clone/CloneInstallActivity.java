package com.calculator.vault.privacy.core.clone;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

/**
 * Runs inside the managed (clone) profile to install a copy of an app from the main profile.
 */
public final class CloneInstallActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String packageName = getIntent().getStringExtra(WorkProfileCloneManager.EXTRA_PACKAGE_NAME);
        boolean success = false;
        if (packageName != null) {
            DevicePolicyManager dpm = getSystemService(DevicePolicyManager.class);
            ComponentName admin = new ComponentName(this, CloneDeviceAdminReceiver.class);
            if (dpm != null && dpm.isProfileOwnerApp(getPackageName())) {
                try {
                    success = dpm.installExistingPackage(admin, packageName);
                } catch (Exception ignored) {
                    success = false;
                }
            }
        }
        Intent data = new Intent();
        data.putExtra(WorkProfileCloneManager.EXTRA_PACKAGE_NAME, packageName);
        data.putExtra(WorkProfileCloneManager.EXTRA_INSTALL_SUCCESS, success);
        setResult(success ? RESULT_OK : RESULT_CANCELED, data);
        finish();
    }
}
