package com.calculator.vault.privacy.core.security;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import dagger.hilt.android.qualifiers.ApplicationContext;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class PinManager {
    private static final String PREFS_NAME = "privacy_space_secure_prefs";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEYSTORE_ALIAS = "privacy_space_master";
    private static final String KEY_SETUP_COMPLETE = "setup_complete";
    private static final String KEY_REAL = "real_pin";
    private static final String KEY_FAKE = "fake_pin";
    private static final String KEY_FAKE_VAULT = "fake_vault_enabled";
    private static final String KEY_BIOMETRIC = "biometric_enabled";
    private static final String KEY_INTRUDER_CAPTURE = "intruder_capture_enabled";
    private static final String KEY_LAST_LOGIN = "last_login";
    private static final String KEY_LAST_ACTIVITY = "last_activity";
    private static final String KEY_SESSION_TIMEOUT = "session_timeout_minutes";
    private static final String KEY_FAILED_ATTEMPTS = "failed_attempts";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;

    private final android.content.SharedPreferences prefs;

    @Inject
    public PinManager(@ApplicationContext Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize encrypted preferences", e);
        }
        ensureKeystoreKey();
    }

    public boolean isSetupComplete() {
        return prefs.getBoolean(KEY_SETUP_COMPLETE, false);
    }

    public void storePin(String pin, boolean isFakePin) {
        String salt = generateSalt();
        String hash = hashPin(pin, salt);
        String prefix = isFakePin ? KEY_FAKE : KEY_REAL;
        prefs.edit()
                .putString(prefix + "_salt", salt)
                .putString(prefix + "_hash", hash)
                .apply();
    }

    public boolean verifyPin(String pin, boolean isFakePin) {
        String prefix = isFakePin ? KEY_FAKE : KEY_REAL;
        String salt = prefs.getString(prefix + "_salt", null);
        String storedHash = prefs.getString(prefix + "_hash", null);
        if (salt == null || storedHash == null) return false;
        return constantTimeEquals(storedHash, hashPin(pin, salt));
    }

    public boolean hasFakePin() {
        return prefs.contains(KEY_FAKE + "_hash");
    }

    public void clearFakePin() {
        prefs.edit()
                .remove(KEY_FAKE + "_salt")
                .remove(KEY_FAKE + "_hash")
                .apply();
    }

    public void markSetupComplete() {
        prefs.edit().putBoolean(KEY_SETUP_COMPLETE, true).apply();
    }

    public void setFakeVaultEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_FAKE_VAULT, enabled).apply();
    }

    public boolean isFakeVaultEnabled() {
        return prefs.getBoolean(KEY_FAKE_VAULT, false);
    }

    public void setBiometricEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_BIOMETRIC, enabled).apply();
    }

    public boolean isBiometricEnabled() {
        return prefs.getBoolean(KEY_BIOMETRIC, false);
    }

    public void setIntruderCaptureEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_INTRUDER_CAPTURE, enabled).apply();
    }

    public boolean isIntruderCaptureEnabled() {
        return prefs.getBoolean(KEY_INTRUDER_CAPTURE, true);
    }

    public void setLastLoginAt(long timestamp) {
        prefs.edit().putLong(KEY_LAST_LOGIN, timestamp).apply();
    }

    public long getLastLoginAt() {
        return prefs.getLong(KEY_LAST_LOGIN, 0L);
    }

    public void putLong(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return prefs.getLong(key, defaultValue);
    }

    public void putInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void putString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    /** Clears vault credentials for instrumentation tests without rotating the SQLCipher passphrase. */
    public void resetVaultStateForTesting() {
        prefs.edit()
                .remove(KEY_SETUP_COMPLETE)
                .remove(KEY_REAL + "_salt")
                .remove(KEY_REAL + "_hash")
                .remove(KEY_FAKE + "_salt")
                .remove(KEY_FAKE + "_hash")
                .remove(KEY_FAKE_VAULT)
                .remove(KEY_BIOMETRIC)
                .putBoolean(KEY_INTRUDER_CAPTURE, false)
                .remove(KEY_LAST_LOGIN)
                .remove(KEY_LAST_ACTIVITY)
                .remove(KEY_FAILED_ATTEMPTS)
                .putInt(KEY_SESSION_TIMEOUT, 5)
                .apply();
    }

    public int getFailedAttempts() {
        return prefs.getInt(KEY_FAILED_ATTEMPTS, 0);
    }

    public int incrementFailedAttempts() {
        int count = getFailedAttempts() + 1;
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, count).apply();
        return count;
    }

    public void resetFailedAttempts() {
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, 0).apply();
    }

    public static final String KEY_LAST_ACTIVITY_PREF = KEY_LAST_ACTIVITY;
    public static final String KEY_SESSION_TIMEOUT_PREF = KEY_SESSION_TIMEOUT;

    private String hashPin(String pin, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    pin.toCharArray(),
                    Base64.decode(salt, Base64.NO_WRAP),
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash PIN", e);
        }
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    private boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }

    private void ensureKeystoreKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        ANDROID_KEYSTORE
                );
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(
                                KEYSTORE_ALIAS,
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
            throw new IllegalStateException("Unable to initialize keystore", e);
        }
    }
}
