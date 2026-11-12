package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePinActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("CHANGE PIN");
        title.setTextSize(26);
        title.setTextColor(Color.WHITE);

        EditText oldPin = new EditText(this);
        oldPin.setHint("Enter Old PIN");
        oldPin.setInputType(2);

        EditText newPin = new EditText(this);
        newPin.setHint("Enter New 4 Digit PIN");
        newPin.setInputType(2);

        EditText confirmPin = new EditText(this);
        confirmPin.setHint("Confirm New PIN");
        confirmPin.setInputType(2);

        Button saveButton = new Button(this);
        saveButton.setText("SAVE NEW PIN");

        layout.addView(title);
        layout.addView(oldPin);
        layout.addView(newPin);
        layout.addView(confirmPin);
        layout.addView(saveButton);

        setContentView(layout);

        saveButton.setOnClickListener(v -> {
            String oldValue = oldPin.getText().toString();
            String newValue = newPin.getText().toString();
            String confirmValue = confirmPin.getText().toString();

            if (!PinStore.checkPin(this, oldValue)) {
                Toast.makeText(this, "Old PIN is incorrect",
                    Toast.LENGTH_SHORT).show();
            } else if (!newValue.matches("[0-9]{4}")) {
                Toast.makeText(this, "New PIN must be 4 digits",
                    Toast.LENGTH_SHORT).show();
            } else if (!newValue.equals(confirmValue)) {
                Toast.makeText(this, "PIN does not match",
                    Toast.LENGTH_SHORT).show();
            } else {
                PinStore.savePin(this, newValue);
                Toast.makeText(this, "PIN changed successfully",
                    Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
