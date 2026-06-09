package com.calculator.vault.privacy.data.datasource;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class AppLauncher {
    private final Context context;

    @Inject
    public AppLauncher(@ApplicationContext Context context) {
        this.context = context;
    }

    public void launch(String packageName) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            throw new IllegalStateException("App cannot be launched");
        }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launchIntent);
    }
}
