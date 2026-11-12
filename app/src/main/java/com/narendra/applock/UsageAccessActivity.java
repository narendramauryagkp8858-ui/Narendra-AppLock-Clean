
package com.narendra.applock;

import android.app.Activity;
import android.app.AppOpsManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UsageAccessActivity extends Activity {

    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("USAGE ACCESS");
        title.setTextSize(24);
        title.setTextColor(Color.WHITE);

        statusText = new TextView(this);
        statusText.setTextColor(Color.LTGRAY);
        statusText.setGravity(Gravity.CENTER);
        statusText.setText("Check permission status below.");

        Button permissionButton = new Button(this);
        permissionButton.setText("OPEN USAGE ACCESS SETTINGS");

        Button startButton = new Button(this);
        startButton.setText("START MONITORING");

        layout.addView(title);
        layout.addView(statusText);
        layout.addView(permissionButton);
        layout.addView(startButton);

        setContentView(layout);

        permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                Settings.ACTION_USAGE_ACCESS_SETTINGS
            );
            startActivity(intent);
        });

        startButton.setOnClickListener(v -> {
            if (hasUsagePermission()) {
                Intent serviceIntent =
                    new Intent(this, AppMonitorService.class);

                startService(serviceIntent);

                statusText.setText("Monitoring service started.");
                Toast.makeText(
                    this,
                    "Monitoring started",
                    Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                    this,
                    "Please allow Usage Access first.",
                    Toast.LENGTH_LONG
                ).show();
                startActivity(
                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                );
            }
        });
    }

    private boolean hasUsagePermission() {
        AppOpsManager appOps =
            (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        if (appOps == null) return false;

        int mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (statusText != null) {
            if (hasUsagePermission()) {
                statusText.setText("Usage Access: Permission granted");
            } else {
                statusText.setText("Usage Access: Permission not granted");
            }
        }
    }
}
