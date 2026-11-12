
package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("Welcome to Narendra AppLock");
        title.setTextSize(24);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        Button lockButton = new Button(this);
        lockButton.setText("APP LOCK");

        Button usageButton = new Button(this);
        usageButton.setText("USAGE ACCESS");

        Button vaultButton = new Button(this);
        vaultButton.setText("PHOTO & VIDEO VAULT");

        Button changePinButton = new Button(this);
        changePinButton.setText("CHANGE PIN");

        layout.addView(title);
        layout.addView(lockButton);
        layout.addView(usageButton);
        layout.addView(vaultButton);
        layout.addView(changePinButton);

        setContentView(layout);

        lockButton.setOnClickListener(v ->
            startActivity(new Intent(this, AppLockActivity.class))
        );

        usageButton.setOnClickListener(v ->
            startActivity(new Intent(this, UsageAccessActivity.class))
        );

        vaultButton.setOnClickListener(v ->
            startActivity(new Intent(this, VaultActivity.class))
        );

        changePinButton.setOnClickListener(v ->
            startActivity(new Intent(this, ChangePinActivity.class))
        );
    }
}
