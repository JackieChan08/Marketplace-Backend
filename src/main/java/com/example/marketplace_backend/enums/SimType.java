package com.example.marketplace_backend.enums;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public enum SimType {
    NONE("No SIM"),
    DUAL_SIM("Dual SIM"),
    SIM_AND_ESIM("SIM + eSIM"),
    DUAL_SIM_AND_SIM_AND_ESIM("Dual SIM + SIM + eSIM");

    private final String displayName;

    SimType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
