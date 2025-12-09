package com.bugtracker.enums;

public enum BugStatus {
    Open("Açık"),
    In_progress("Devam Ediyor"),
    Resolved("Çözüldü"),
    Reopened("Yeniden Açıldı"),
    Closed("Kapatıldı");

    private final String displayName;

    BugStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}