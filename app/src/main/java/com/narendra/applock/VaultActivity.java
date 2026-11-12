
package com.narendra.applock;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;

public class VaultActivity extends Activity {

    private LinearLayout layout;
    private LinearLayout fileList;

    private static final int PICK_FILE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPinDialog();
    }

    private void showPinDialog() {
        android.widget.EditText pinInput =
                new android.widget.EditText(this);

        pinInput.setInputType(
                2 | 16
        );
        pinInput.setHint("Enter PIN");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Narendra Private Vault")
                .setMessage("Enter PIN to open Vault")
                .setView(pinInput)
                .setCancelable(false)
                .setPositiveButton("UNLOCK", null)
                .setNegativeButton("CANCEL",
                        (d, w) -> finish())
                .create();

        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String pin =
                                    pinInput.getText().toString();

                            if (PinStore.checkPin(this, pin)) {
                                dialog.dismiss();
                                showVaultScreen();
                            } else {
                                pinInput.setText("");
                                pinInput.setError("Wrong PIN");
                            }
                        })
        );

        dialog.show();
    }

    private void showVaultScreen() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setBackgroundColor(Color.rgb(18, 24, 38));

        TextView title = new TextView(this);
        title.setText("NARENDRA PRIVATE VAULT");
        title.setTextSize(23);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        Button addButton = new Button(this);
        addButton.setText("ADD PHOTO / VIDEO");

        fileList = new LinearLayout(this);
        fileList.setOrientation(LinearLayout.VERTICAL);

        android.widget.ScrollView scroll =
                new android.widget.ScrollView(this);
        scroll.addView(fileList);

        layout.addView(title);
        layout.addView(addButton);
        layout.addView(scroll,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                ));

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
    protected void onActivityResult(
            int requestCode,
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
                throw new Exception("Only photos and videos allowed");
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
        fileList.removeAllViews();

        File folder = new File(getFilesDir(), "vault");
        File[] files = folder.listFiles();

        int count = 0;

        if (files != null) {
            for (File file : files) {
                if (file.isFile() &&
                        file.getName().endsWith(".nrv")) {

                    count++;

                    Button button = new Button(this);
                    button.setText("VIEW SECURE FILE " + count);

                    button.setOnClickListener(v ->
                            previewPhoto(file));

                    fileList.addView(button);
                }
            }
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("Your vault is empty.");
            empty.setTextColor(Color.WHITE);
            fileList.addView(empty);
        }
    }

    private void previewPhoto(File encryptedFile) {
        File tempFile = null;

        try {
            tempFile = File.createTempFile(
                    "vault_preview_", ".tmp", getCacheDir()
            );

            try (InputStream input =
                         VaultCrypto.decrypt(encryptedFile);
                 FileOutputStream output =
                         new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[8192];
                int length;

                while ((length = input.read(buffer)) != -1) {
                    output.write(buffer, 0, length);
                }
            }

            BitmapFactory.Options options =
                    new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeFile(
                    tempFile.getAbsolutePath(), options
            );

            if (bitmap == null) {
                throw new Exception(
                        "Preview is currently available for photos only"
                );
            }

            ImageView image = new ImageView(this);
            image.setImageBitmap(bitmap);
            image.setAdjustViewBounds(true);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Vault Photo")
                    .setView(image)
                    .setPositiveButton("CLOSE", (d, w) -> {
                        image.setImageDrawable(null);
                        bitmap.recycle();
                    })
                    .create();

            dialog.setOnDismissListener(d -> {
                image.setImageDrawable(null);
                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            });

            dialog.show();

        } catch (Exception e) {
            Toast.makeText(this,
                    "Preview Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}
