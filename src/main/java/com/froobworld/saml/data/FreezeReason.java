package com.froobworld.saml.data;

public enum FreezeReason {
    TPS,
    PASSIVE,
    COMMAND,
    CUSTOM,
    DEFAULT;

    public static FreezeReason valueOfOrDefault(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return DEFAULT;
        }
    }
}
