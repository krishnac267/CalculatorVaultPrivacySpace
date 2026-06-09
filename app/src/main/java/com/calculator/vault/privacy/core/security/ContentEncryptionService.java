package com.calculator.vault.privacy.core.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;
import javax.inject.Singleton;

/** AES-256-GCM content encryption using Android Keystore. */
@Singleton
public final class ContentEncryptionService {
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS_PREFIX = "privacy_space_content_v";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int STREAM_BUFFER = 8192;

    private final PinManager pinManager;

    @Inject
    public ContentEncryptionService(PinManager pinManager) {
        this.pinManager = pinManager;
        ensureKey(getActiveKeyVersion());
    }

    public String encryptText(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return "";
        }
        byte[] encrypted = encryptBytes(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

    public String decryptText(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return "";
        }
        byte[] decoded = Base64.decode(ciphertext, Base64.NO_WRAP);
        byte[] plain = decryptBytes(decoded);
        return new String(plain, StandardCharsets.UTF_8);
    }

    public byte[] encryptBytes(byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(getActiveKeyVersion()));
            byte[] iv = cipher.getIV();
            byte[] encrypted = cipher.doFinal(plaintext);
            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
            return payload;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to encrypt content", e);
        }
    }

    public byte[] decryptBytes(byte[] payload) {
        if (payload.length <= IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted payload");
        }
        try {
            byte[] iv = Arrays.copyOfRange(payload, 0, IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(payload, IV_LENGTH, payload.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(getActiveKeyVersion()),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decrypt content", e);
        }
    }

    public byte[] decryptBytesWithVersion(byte[] payload, int keyVersion) {
        if (payload.length <= IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted payload");
        }
        try {
            byte[] iv = Arrays.copyOfRange(payload, 0, IV_LENGTH);
            byte[] encrypted = Arrays.copyOfRange(payload, IV_LENGTH, payload.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(keyVersion),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to decrypt content", e);
        }
    }

    public void encryptStream(InputStream input, OutputStream output) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(getActiveKeyVersion()));
            output.write(cipher.getIV());
            try (CipherOutputStream cipherOut = new CipherOutputStream(output, cipher)) {
                byte[] buffer = new byte[STREAM_BUFFER];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    cipherOut.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            throw new IOException("Unable to encrypt stream", e);
        }
    }

    public void decryptStream(InputStream input, OutputStream output) throws IOException {
        decryptStream(input, output, getActiveKeyVersion());
    }

    public void decryptStream(InputStream input, OutputStream output, int keyVersion) throws IOException {
        try {
            byte[] iv = input.readNBytes(IV_LENGTH);
            if (iv.length != IV_LENGTH) {
                throw new IOException("Missing IV");
            }
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getSecretKey(keyVersion),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );
            try (CipherInputStream cipherIn = new CipherInputStream(input, cipher)) {
                byte[] buffer = new byte[STREAM_BUFFER];
                int read;
                while ((read = cipherIn.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
            }
        } catch (Exception e) {
            throw new IOException("Unable to decrypt stream", e);
        }
    }

    public byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[STREAM_BUFFER];
        int read;
        while ((read = input.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }

    public int getActiveKeyVersion() {
        return pinManager.getInt("content_key_version", 1);
    }

    public void setActiveKeyVersion(int version) {
        pinManager.putInt("content_key_version", version);
    }

    public void ensureKey(int version) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            String alias = aliasForVersion(version);
            if (!keyStore.containsAlias(alias)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        ANDROID_KEYSTORE
                );
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(
                                alias,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                        )
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .setUserAuthenticationRequired(false)
                                .build()
                );
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize content encryption key", e);
        }
    }

    private SecretKey getSecretKey(int version) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        return (SecretKey) keyStore.getKey(aliasForVersion(version), null);
    }

    private String aliasForVersion(int version) {
        return KEY_ALIAS_PREFIX + version;
    }
}
