package com.calculator.vault.privacy.domain.validators;

import java.util.regex.Pattern;

public final class PinValidator {
    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4,8}$");

    private PinValidator() {}

    public static boolean isValid(String pin) {
        return pin != null && PIN_PATTERN.matcher(pin).matches();
    }

    public static boolean areDistinct(String realPin, String fakePin) {
        return fakePin == null || fakePin.isBlank() || !realPin.equals(fakePin);
    }
}
