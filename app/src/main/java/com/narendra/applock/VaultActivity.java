
package com.narendra.applock;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

public class VaultActivity extends Activity {

    private LinearLayout layout;
    private TextView fileList;

    private static final int PICK_FILE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 25, 25, 25);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("NARENDRA PRIVATE VAULT");
        title.setTextSize(23);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        Button addButton = new Button(this);
        addButton.setText("ADD PHOTO / VIDEO");

        fileList = new TextView(this);
        fileList.setTextColor(Color.WHITE);
        fileList.setTextSize(16);
        fileList.setPadding(10, 25, 10, 25);

        layout.addView(title);
        layout.addView(addButton);
        layout.addView(fileList);

        setContentView(layout);

        addButton.setOnClickListener(v -> openFilePicker());

        showVaultFiles();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,
                new String[]{"image/*", "video/*"});
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE &&
                resultCode == RESULT_OK &&
                data != null) {

            Uri uri = data.getData();

            if (uri != null) {
                saveFile(uri);
            }
        }
    }

    private void saveFile(Uri uri) {
        try {
            String mime = getContentResolver().getType(uri);

            if (mime == null ||
                    (!mime.startsWith("image/") &&
                     !mime.startsWith("video/"))) {
                Toast.makeText(this, "Only photos and videos allowed",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String name = getFileName(uri);
            String extension = name.contains(".")
                    ? name.substring(name.lastIndexOf("."))
                    : "";

            File folder = new File(getFilesDir(), "vault");

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File destination = new File(
                    folder,
                    "vault_" + System.currentTimeMillis() + extension
            );

            InputStream input = getContentResolver()
                    .openInputStream(uri);

            if (input == null) {
                throw new Exception("Cannot open selected file");
            }

            FileOutputStream output = new FileOutputStream(destination);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            input.close();
            output.close();

            Toast.makeText(this, "File saved in private vault",
                    Toast.LENGTH_LONG).show();

            showVaultFiles();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getFileName(Uri uri) {
        String name = "private_file";

        Cursor cursor = getContentResolver().query(
                uri, null, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME);

                    if (index >= 0) {
                        name = cursor.getString(index);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return name;
    }

    private void showVaultFiles() {
        File folder = new File(getFilesDir(), "vault");

        if (!folder.exists()) {
            fileList.setText("Your vault is empty.");
            return;
        }

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            fileList.setText("Your vault is empty.");
            return;
        }

        StringBuilder list = new StringBuilder();
        list.append("SAVED PRIVATE FILES\n\n");

        for (File file : files) {
            list.append("• ")
                .append(file.getName())
                .append("\n\n");
        }

        fileList.setText(list.toString());
    }
}
