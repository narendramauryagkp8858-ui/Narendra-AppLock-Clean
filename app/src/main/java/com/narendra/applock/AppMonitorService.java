
package com.narendra.applock;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import android.content.Context;

import java.util.List;

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
            (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        if (manager == null) return;

        long end = System.currentTimeMillis();
        long start = end - 10000;

        List<UsageStats> stats =
            manager.queryUsageStats(
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

        if (packageName.equals(getPackageName())) {
            return;
        }

        if (packageName.equals(lastPackage)) return;

        lastPackage = packageName;
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
