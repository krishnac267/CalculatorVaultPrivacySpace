package com.calculator.vault.privacy.core.clone;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Required on Android 12+ before managed profile provisioning can start.
 */
public final class CloneProvisioningModeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mode = DevicePolicyManager.PROVISIONING_MODE_MANAGED_PROFILE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ArrayList<Integer> allowed = getIntent().getIntegerArrayListExtra(
                    DevicePolicyManager.EXTRA_PROVISIONING_ALLOWED_PROVISIONING_MODES
            );
            if (allowed != null && !allowed.isEmpty() && !allowed.contains(mode)) {
                for (int candidate : allowed) {
                    if (candidate == DevicePolicyManager.PROVISIONING_MODE_MANAGED_PROFILE) {
                        mode = candidate;
                        break;
                    }
                }
                if (!allowed.contains(mode)) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return;
                }
            }
        }
        Intent result = new Intent();
        result.putExtra(DevicePolicyManager.EXTRA_PROVISIONING_MODE, mode);
        setResult(RESULT_OK, result);
        finish();
    }
}
