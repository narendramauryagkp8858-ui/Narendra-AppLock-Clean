
package com.narendra.applock;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppMonitorService extends Service {

    private Handler handler;
    private Runnable monitorTask;
    private String lastPackage = "";

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler(Looper.getMainLooper());

        monitorTask = new Runnable() {
            @Override
            public void run() {
                checkRunningApp();
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(monitorTask);
    }

    private void checkRunningApp() {

        UsageStatsManager manager =
            (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);

        if (manager == null) return;

        long end = System.currentTimeMillis();
        long start = end - 10000;

        List<UsageStats> stats = manager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            start,
            end
        );

        if (stats == null || stats.isEmpty()) return;

        UsageStats recent = null;

        for (UsageStats item : stats) {
            if (recent == null ||
                item.getLastTimeUsed() > recent.getLastTimeUsed()) {
                recent = item;
            }
        }

        if (recent == null) return;

        String packageName = recent.getPackageName();

        if (packageName.equals(getPackageName())) return;

        if (packageName.equals(lastPackage)) return;

        lastPackage = packageName;

        SharedPreferences preferences =
            getSharedPreferences("narendra_applock", MODE_PRIVATE);

        Set<String> lockedApps = preferences.getStringSet(
            "locked_apps",
            new HashSet<String>()
        );

        if (lockedApps == null ||
            !lockedApps.contains(packageName)) {
            return;
        }

        Intent lockIntent =
            new Intent(this, LockScreenActivity.class);

        lockIntent.putExtra("target_package", packageName);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(lockIntent);
    }

    @Override
    public void onDestroy() {
        if (handler != null && monitorTask != null) {
            handler.removeCallbacks(monitorTask);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
