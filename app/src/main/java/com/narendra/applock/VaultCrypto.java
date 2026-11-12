
package com.narendra.applock;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.security.KeyStore;

public class VaultCrypto {

    private static final String KEY_ALIAS = "NarendraVaultKey";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    private static SecretKey getKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        }

        KeyGenerator generator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
        );

        generator.init(new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT |
                KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setRandomizedEncryptionRequired(true)
                .build());

        return generator.generateKey();
    }

    public static void encrypt(
            InputStream input,
            File destination
    ) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getKey());

        byte[] iv = cipher.getIV();

        try (InputStream source = input;
             OutputStream fileOutput = new FileOutputStream(destination)) {

            fileOutput.write(iv);

            try (CipherOutputStream encrypted =
                         new CipherOutputStream(fileOutput, cipher)) {

                byte[] buffer = new byte[8192];
                int length;

                while ((length = source.read(buffer)) != -1) {
                    encrypted.write(buffer, 0, length);
                }
            }
        }
    }

    public static InputStream decrypt(File source)
            throws Exception {

        FileInputStream input = new FileInputStream(source);

        try {
            byte[] iv = new byte[12];
            int offset = 0;

            while (offset < iv.length) {
                int count = input.read(iv, offset, iv.length - offset);

                if (count == -1) {
                    throw new IOException("Invalid encrypted file");
                }

                offset += count;
            }

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getKey(),
                    new GCMParameterSpec(128, iv)
            );

            return new CipherInputStream(input, cipher);

        } catch (Exception e) {
            input.close();
            throw e;
        }
    }
}
