package com.bugtracker.enums;

public enum BugSeverity {
    Minor("Küçük"),
    Major("Büyük"),
    Critical("Kritik"),
    Blocker("Çok Kritik");

    private final String displayName;
    BugSeverity(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}

