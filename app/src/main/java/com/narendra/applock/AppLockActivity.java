package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

    private SharedPreferences preferences;
    private LinearLayout appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(
            "narendra_applock", MODE_PRIVATE
        );

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("SELECT APPS TO LOCK");
        title.setTextSize(23);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView info = new TextView(this);
        info.setText("Choose the apps you want to protect.");
        info.setTextColor(Color.LTGRAY);
        layout.addView(info);

        appList = new LinearLayout(this);
        appList.setOrientation(LinearLayout.VERTICAL);

        android.widget.ScrollView scroll =
            new android.widget.ScrollView(this);
        scroll.addView(appList);

        layout.addView(scroll, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));

        Button saveButton = new Button(this);
        saveButton.setText("SAVE LOCKED APPS");
        layout.addView(saveButton);

        setContentView(layout);

        loadApps();

        saveButton.setOnClickListener(v -> {
            Set<String> selected = new HashSet<>();

            for (int i = 0; i < appList.getChildCount(); i++) {
                CheckBox checkBox =
                    (CheckBox) appList.getChildAt(i);

                if (checkBox.isChecked()) {
                    selected.add((String) checkBox.getTag());
                }
            }

            preferences.edit()
                .putStringSet("locked_apps", selected)
                .apply();

            Toast.makeText(this,
                "Selected apps saved: " + selected.size(),
                Toast.LENGTH_LONG).show();

            finish();
        });
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps =
            pm.queryIntentActivities(intent, 0);

        Set<String> saved = preferences.getStringSet(
            "locked_apps", new HashSet<>()
        );

        List<String> packages = new ArrayList<>();

        for (ResolveInfo app : apps) {
            String packageName =
                app.activityInfo.packageName;

            if (!packageName.equals(getPackageName())
                && !packages.contains(packageName)) {

                packages.add(packageName);

                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(
                    app.loadLabel(pm).toString()
                );
                checkBox.setTextColor(Color.WHITE);
                checkBox.setTag(packageName);
                checkBox.setChecked(saved.contains(packageName));

                appList.addView(checkBox);
            }
        }

        if (appList.getChildCount() == 0) {
            TextView empty = new TextView(this);
            empty.setText("No apps found.");
            empty.setTextColor(Color.WHITE);
            appList.addView(empty);
        }
    }
}
