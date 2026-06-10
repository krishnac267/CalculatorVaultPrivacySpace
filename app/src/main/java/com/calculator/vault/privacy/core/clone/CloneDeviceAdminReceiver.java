package com.calculator.vault.privacy.core.clone;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.calculator.vault.privacy.R;

public final class CloneDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {
        DevicePolicyManager dpm = context.getSystemService(DevicePolicyManager.class);
        ComponentName admin = new ComponentName(context, CloneDeviceAdminReceiver.class);
        if (dpm != null) {
            dpm.setProfileEnabled(admin);
        }
        Toast.makeText(context, R.string.clone_space_ready, Toast.LENGTH_LONG).show();
    }
}
