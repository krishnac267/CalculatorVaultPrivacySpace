package com.calculator.vault.privacy.data.datasource;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.calculator.vault.privacy.domain.interfaces.InstalledAppsRepository;
import com.calculator.vault.privacy.domain.model.InstalledApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public final class InstalledAppsRepositoryImpl implements InstalledAppsRepository {
    private final Context context;
    private List<InstalledApp> cachedApps;

    @Inject
    public InstalledAppsRepositoryImpl(@ApplicationContext Context context) {
        this.context = context;
    }

    @Override
    public List<InstalledApp> getLaunchableApps() {
        if (cachedApps == null) {
            cachedApps = loadApps();
        }
        return cachedApps;
    }

    @Override
    public List<InstalledApp> searchApps(String query) {
        if (query == null || query.isBlank()) return getLaunchableApps();
        String lower = query.toLowerCase(Locale.US);
        List<InstalledApp> results = new ArrayList<>();
        for (InstalledApp app : getLaunchableApps()) {
            if (app.getLabel().toLowerCase(Locale.US).contains(lower)
                    || app.getPackageName().toLowerCase(Locale.US).contains(lower)) {
                results.add(app);
            }
        }
        return results;
    }

    private List<InstalledApp> loadApps() {
        PackageManager pm = context.getPackageManager();
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_DEFAULT_ONLY);
        List<InstalledApp> apps = new ArrayList<>();
        String selfPackage = context.getPackageName();
        for (ResolveInfo info : activities) {
            String packageName = info.activityInfo.packageName;
            if (packageName.equals(selfPackage)) continue;
            CharSequence label = info.loadLabel(pm);
            String category = categorize(pm, packageName);
            apps.add(new InstalledApp(packageName, label == null ? packageName : label.toString(), category));
        }
        Collections.sort(apps, Comparator.comparing(InstalledApp::getLabel, String.CASE_INSENSITIVE_ORDER));
        return apps;
    }

    private String categorize(PackageManager pm, String packageName) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            int flags = info.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) return "System";
            return "User";
        } catch (PackageManager.NameNotFoundException e) {
            return "Other";
        }
    }
}
