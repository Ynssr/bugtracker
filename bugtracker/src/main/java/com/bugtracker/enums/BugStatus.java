package com.bugtracker.enums;

public enum BugStatus {
    OPEN("Açık"),
    IN_PROGRESS("Devam Ediyor"),
    RESOLVED("Çözüldü"),
    REOPENED("Yeniden Açıldı"),
    CLOSED("Kapatıldı");

    private final String displayName;

    BugStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
