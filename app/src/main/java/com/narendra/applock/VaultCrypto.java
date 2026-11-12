
package com.narendra.applock;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class VaultCrypto {

    private static final String KEY_ALIAS = "NarendraVaultKey";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final int IV_LENGTH = 12;

    private static SecretKey getSecretKey() throws Exception {

        java.security.KeyStore keyStore =
                java.security.KeyStore.getInstance(ANDROID_KEYSTORE);

        keyStore.load(null);

        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        }

        KeyGenerator generator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
        );

        generator.init(
                new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT |
                        KeyProperties.PURPOSE_DECRYPT
                )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_NONE
                )
                .setRandomizedEncryptionRequired(true)
                .build()
        );

        return generator.generateKey();
    }

    public static void encrypt(InputStream input, File outputFile)
            throws Exception {

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

        byte[] iv = cipher.getIV();

        try (FileOutputStream fileOutput =
                     new FileOutputStream(outputFile)) {

            fileOutput.write(iv);

            try (CipherOutputStream encryptedOutput =
                         new CipherOutputStream(fileOutput, cipher)) {

                byte[] buffer = new byte[8192];
                int count;

                while ((count = input.read(buffer)) != -1) {
                    encryptedOutput.write(buffer, 0, count);
                }
            }
        } finally {
            input.close();
        }
    }

    public static InputStream decrypt(File encryptedFile)
            throws Exception {

        FileInputStream input = new FileInputStream(encryptedFile);

        byte[] iv = new byte[IV_LENGTH];

        int read = input.read(iv);

        if (read != IV_LENGTH) {
            input.close();
            throw new Exception("Invalid encrypted file");
        }

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                getSecretKey(),
                new GCMParameterSpec(128, iv)
        );

        return new CipherInputStream(input, cipher);
    }
}
