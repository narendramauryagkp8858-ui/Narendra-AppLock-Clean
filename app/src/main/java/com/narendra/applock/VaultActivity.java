
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
                saveEncryptedFile(uri);
            }
        }
    }

    private void saveEncryptedFile(Uri uri) {
        File destination = null;

        try {
            String mime = getContentResolver().getType(uri);

            if (mime == null ||
                    (!mime.startsWith("image/") &&
                     !mime.startsWith("video/"))) {
                Toast.makeText(this,
                        "Only photos and videos allowed",
                        Toast.LENGTH_LONG).show();
                return;
            }

            File folder = new File(getFilesDir(), "vault");

            if (!folder.exists() && !folder.mkdirs()) {
                throw new Exception("Cannot create vault folder");
            }

            destination = new File(
                    folder,
                    "vault_" + System.currentTimeMillis() + ".nrv"
            );

            InputStream input =
                    getContentResolver().openInputStream(uri);

            if (input == null) {
                throw new Exception("Cannot open selected file");
            }

            VaultCrypto.encrypt(input, destination);

            Toast.makeText(this,
                    "Encrypted file saved successfully",
                    Toast.LENGTH_LONG).show();

            showVaultFiles();

        } catch (Exception e) {

            if (destination != null && destination.exists()) {
                destination.delete();
            }

            Toast.makeText(this,
                    "Vault Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
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
        list.append("ENCRYPTED PRIVATE FILES\n\n");

        int count = 0;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".nrv")) {
                count++;
                list.append(count)
                    .append(". Secure file ")
                    .append(count)
                    .append("\n\n");
            }
        }

        if (count == 0) {
            list.append("Your vault is empty.");
        }

        fileList.setText(list.toString());
    }
}
