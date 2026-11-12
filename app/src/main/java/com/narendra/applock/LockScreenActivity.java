
package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("Narendra AppLock");
        title.setTextSize(26);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView message = new TextView(this);
        message.setText("Enter PIN to unlock this app");
        message.setTextColor(Color.LTGRAY);

        EditText pinInput = new EditText(this);
        pinInput.setHint("Enter PIN");
        pinInput.setInputType(2);
        pinInput.setTextColor(Color.WHITE);
        pinInput.setHintTextColor(Color.LTGRAY);

        Button unlockButton = new Button(this);
        unlockButton.setText("UNLOCK");

        layout.addView(title);
        layout.addView(message);
        layout.addView(pinInput);
        layout.addView(unlockButton);

        setContentView(layout);

        unlockButton.setOnClickListener(v -> {
            String pin = pinInput.getText().toString();

            if (PinStore.checkPin(this, pin)) {
                finish();
            } else {
                pinInput.setText("");
                Toast.makeText(
                    this,
                    "Wrong PIN",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Back button is disabled on this screen.
    }
}
