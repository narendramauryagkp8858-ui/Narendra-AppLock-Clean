package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PinActivity extends Activity {

    private final String DEFAULT_PIN = "8546";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("Narendra AppLock");
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        EditText pinInput = new EditText(this);
        pinInput.setHint("Enter PIN");
        pinInput.setInputType(2);
        pinInput.setTextColor(Color.WHITE);
        pinInput.setHintTextColor(Color.LTGRAY);

        Button unlockButton = new Button(this);
        unlockButton.setText("UNLOCK");

        layout.addView(title);
        layout.addView(pinInput);
        layout.addView(unlockButton);

        setContentView(layout);

        unlockButton.setOnClickListener(v -> {
            if (DEFAULT_PIN.equals(pinInput.getText().toString())) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
