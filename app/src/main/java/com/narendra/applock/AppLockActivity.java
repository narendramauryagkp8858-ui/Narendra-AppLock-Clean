
package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppLockActivity extends Activity {

    private LinearLayout appList;
    private final List<String> packageNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("SELECT APPS TO LOCK");
        title.setTextSize(22);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        appList = new LinearLayout(this);
        appList.setOrientation(LinearLayout.VERTICAL);

        android.widget.ScrollView scrollView =
                new android.widget.ScrollView(this);
        scrollView.addView(appList);

        layout.addView(scrollView,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0, 1
                ));

        Button saveButton = new Button(this);
        saveButton.setText("SAVE LOCKED APPS");
        layout.addView(saveButton);

        setContentView(layout);

        loadApps();

        saveButton.setOnClickListener(v -> saveApps());
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();

        Set<String> savedApps = getSharedPreferences(
                "AppLockSettings", MODE_PRIVATE
        ).getStringSet("locked_apps", new HashSet<>());

        Set<String> savedCopy = new HashSet<>(savedApps);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<android.content.pm.ResolveInfo> apps =
                pm.queryIntentActivities(intent, 0);

        for (android.content.pm.ResolveInfo info : apps) {
            String packageName = info.activityInfo.packageName;

            if (packageName.equals(getPackageName())) {
                continue;
            }

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(info.loadLabel(pm).toString());
            checkBox.setTextColor(Color.WHITE);
            checkBox.setTextSize(17);
            checkBox.setTag(packageName);
            checkBox.setChecked(savedCopy.contains(packageName));

            appList.addView(checkBox);
            packageNames.add(packageName);
        }

        if (apps.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No apps found");
            empty.setTextColor(Color.WHITE);
            appList.addView(empty);
        }
    }

    private void saveApps() {
        Set<String> selectedApps = new HashSet<>();

        for (int i = 0; i < appList.getChildCount(); i++) {
            android.view.View view = appList.getChildAt(i);

            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;

                if (checkBox.isChecked()) {
                    selectedApps.add(
                            checkBox.getTag().toString()
                    );
                }
            }
        }

        getSharedPreferences(
                "AppLockSettings", MODE_PRIVATE
        ).edit()
                .putStringSet("locked_apps", selectedApps)
                .apply();

        Toast.makeText(
                this,
                "Selected apps saved: " + selectedApps.size(),
                Toast.LENGTH_LONG
        ).show();
    }
}
