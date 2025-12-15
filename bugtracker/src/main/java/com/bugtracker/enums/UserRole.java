package com.bugtracker.enums;

public enum UserRole {
    ADMIN("Yönetici"),
    DEVELOPER("Geliştirici"),
    TESTER("Test Uzmanı / Raporlayıcı");
    
    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
