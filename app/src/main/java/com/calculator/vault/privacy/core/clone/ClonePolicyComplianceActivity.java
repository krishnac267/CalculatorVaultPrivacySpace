package com.calculator.vault.privacy.core.clone;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;

/**
 * Finalizes managed profile provisioning on Android 12+.
 */
public final class ClonePolicyComplianceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DevicePolicyManager dpm = getSystemService(DevicePolicyManager.class);
        ComponentName admin = new ComponentName(this, CloneDeviceAdminReceiver.class);
        if (dpm != null && dpm.isProfileOwnerApp(getPackageName())) {
            dpm.setProfileEnabled(admin);
        }
        setResult(RESULT_OK);
        finish();
    }
}
