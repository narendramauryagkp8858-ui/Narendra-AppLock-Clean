
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

        TextView info = new TextView(this);
        info.setText(
            "Allow Narendra AppLock to see which app is currently being used."
        );
        info.setTextColor(Color.LTGRAY);
        info.setGravity(Gravity.CENTER);

        Button permissionButton = new Button(this);
        permissionButton.setText("OPEN USAGE ACCESS SETTINGS");

        layout.addView(title);
        layout.addView(info);
        layout.addView(permissionButton);

        setContentView(layout);

        permissionButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });
    }

    private boolean hasUsagePermission() {
        AppOpsManager appOps =
            (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

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

        if (hasUsagePermission()) {
            Toast.makeText(
                this,
                "Usage Access permission granted",
                Toast.LENGTH_SHORT
            ).show();
        }
    }
}
