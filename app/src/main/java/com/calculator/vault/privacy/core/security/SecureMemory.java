package com.calculator.vault.privacy.core.security;

import java.util.Arrays;

public final class SecureMemory {
    private SecureMemory() {}

    public static void wipe(char[] data) {
        if (data != null) {
            Arrays.fill(data, '\0');
        }
    }

    public static void wipe(byte[] data) {
        if (data != null) {
            Arrays.fill(data, (byte) 0);
        }
    }
}
