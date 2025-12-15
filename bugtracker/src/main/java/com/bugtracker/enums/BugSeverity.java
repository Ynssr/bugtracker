package com.bugtracker.enums;

public enum BugSeverity {
    MINOR("Küçük"),
    MAJOR("Büyük"),
    CRITICAL("Kritik"),
    BLOCKER("Çok Kritik");

    private final String displayName;
    BugSeverity(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
