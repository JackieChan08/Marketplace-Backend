package com.example.marketplace_backend.enums;

public enum SimType {
    DUAL_SIM("Dual SIM"),
    SIM_AND_ESIM("SIM + eSIM");

    private final String displayName;

    SimType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
