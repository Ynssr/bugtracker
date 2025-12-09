package com.bugtracker.enums;

public enum UserRole {
    Admin("Yönetici"),
    Developer("Geliştirici"),
    Tester("Test Uzmanı"),
    Reporter("Raporlayıcı");
    private final String displayName;
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }


}
