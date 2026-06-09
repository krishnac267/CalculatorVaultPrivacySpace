package com.calculator.vault.privacy.core.security;

import android.util.Base64;

import java.security.SecureRandom;

import javax.inject.Inject;
import javax.inject.Singleton;

/** Provides a stable passphrase for SQLCipher-backed Room storage. */
@Singleton
public final class DatabaseKeyManager {
    private static final String KEY_DB_PASSPHRASE = "db_passphrase_v1";

    private final PinManager pinManager;

    @Inject
    public DatabaseKeyManager(PinManager pinManager) {
        this.pinManager = pinManager;
    }

    public byte[] getOrCreatePassphrase() {
        String encoded = pinManager.getString(KEY_DB_PASSPHRASE, "");
        if (!encoded.isEmpty()) {
            return Base64.decode(encoded, Base64.NO_WRAP);
        }
        byte[] passphrase = new byte[32];
        new SecureRandom().nextBytes(passphrase);
        pinManager.putString(KEY_DB_PASSPHRASE, Base64.encodeToString(passphrase, Base64.NO_WRAP));
        return passphrase;
    }
}
